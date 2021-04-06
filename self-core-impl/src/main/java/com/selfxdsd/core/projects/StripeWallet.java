/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;
import com.selfxdsd.core.contracts.invoices.StoredPayment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A Project's Stripe wallet.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.27
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class StripeWallet implements Wallet {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        StripeWallet.class
    );

    /**
     * Self Storage.
     */
    private final Storage storage;

    /**
     * Project to which this wallet belongs.
     */
    private final Project project;

    /**
     * Cash limit that we are allowed to take from this Wallet.
     */
    private final BigDecimal limit;

    /**
     * Is this wallet active or not?
     */
    private final boolean active;

    /**
     * Wallet ID.
     */
    private final String identifier;

    /**
     * Stripe API token.
     */
    private final String stripeApiToken;

    /**
     * Ctor.
     * @param storage Self storage.
     * @param project Project to which this wallet belongs/
     * @param limit Cash limit we're allowed to use.
     * @param identifier Wallet identifier from Stripe's side.
     * @param active Is this wallet active or not?
     * @param stripeApiToken Stripe API token.
     */
    StripeWallet(
        final Storage storage,
        final Project project,
        final BigDecimal limit,
        final String identifier,
        final boolean active,
        final String stripeApiToken
    ) {
        this.storage = storage;
        this.project = project;
        this.identifier = identifier;
        this.limit = limit;
        this.active = active;
        this.stripeApiToken = stripeApiToken;
    }

    /**
     * Ctor.
     * @param storage Self storage.
     * @param project Project to which this wallet belongs/
     * @param limit Cash limit we're allowed to use.
     * @param identifier Wallet identifier from Stripe's side.
     * @param active Is this wallet active or not?
     */
    public StripeWallet(
        final Storage storage,
        final Project project,
        final BigDecimal limit,
        final String identifier,
        final boolean active
    ) {
        this(storage,
            project,
            limit,
            identifier,
            active,
            System.getenv(Env.STRIPE_API_TOKEN));
    }

    @Override
    public BigDecimal cash() {
        return this.limit;
    }

    /**
     * Collect money from the Customer and wire it directly to the
     * Contributor (connected account).
     * @see <a href='https://stripe.com/docs/connect/collect-then-transfer-guide'>docs</a>
     * @param invoice The Invoice to be paid.
     * @return Wallet with the updated cash limit.
     * @checkstyle CyclomaticComplexity (200 lines)
     * @checkstyle MethodLength (200 lines)
     */
    @Override
    public Payment pay(final Invoice invoice) {
        final Contract contract = invoice.contract();
        LOG.debug(
            "[STRIPE] Trying to pay Invoice #" + invoice.invoiceId() + " of "
            + " Contract " + contract.contractId()
            + " from Wallet " + this.identifier
        );
        final BigDecimal newLimit = this.limit.subtract(
            invoice.totalAmount()
        );

        ensureApiToken();

        try {
            final Contributor contributor = invoice.contract().contributor();
            final PayoutMethod payoutMethod = this.storage
                .payoutMethods()
                .ofContributor(contributor)
                .getByType(PayoutMethod.Type.STRIPE);
            if (payoutMethod == null) {
                LOG.error(
                    "[STRIPE] Contributor " + contributor.username()
                    + " from " + contributor.provider() + " has no Stripe "
                    + "PayoutMethod set up, cannot pay."
                );
                throw new WalletPaymentException(
                    "Contributor " + contributor.username() + " hasn't "
                    + "finished setting up their Stripe PayoutMethod."
                );
            }
            final PaymentMethod paymentMethod = this.storage
                .paymentMethods()
                .ofWallet(this)
                .active();
            if (paymentMethod == null) {
                LOG.error(
                    "[STRIPE] Project has no Payment Method (card) set up. "
                    + "Cannot make payment."
                );
                throw new WalletPaymentException(
                    "The project's wallet has no active card."
                );
            }

            final BillingInfo contributorBilling = payoutMethod.billingInfo();
            final BigDecimal vat = this.calculateVat(
                invoice.projectCommission(),
                contributorBilling
            );
            final BigDecimal totalAmount = invoice.totalAmount();
            final PaymentIntent paymentIntent = PaymentIntent
                .create(
                    PaymentIntentCreateParams.builder()
                        .setCurrency("eur")
                        .setAmount(totalAmount.longValueExact())
                        .setCustomer(this.identifier)
                        .setPaymentMethod(paymentMethod.identifier())
                        .setTransferData(
                            PaymentIntentCreateParams.TransferData
                                .builder()
                                .setDestination(payoutMethod.identifier())
                                .setAmount(
                                    invoice.amount().subtract(vat)
                                        .longValueExact()
                                )
                                .build()
                        )
                        .setDescription(
                            "Payment for Invoice #" + invoice.invoiceId() + " "
                            + "of Contract " + contract.contractId() + ". "
                        )
                        .setOffSession(true)
                        .setConfirm(true)
                        .build()
                );

            final String status = paymentIntent.getStatus();
            LOG.debug("[STRIPE] Payment Status " + status);
            if ("succeeded".equals(status) || "processing".equals(status)) {
                final LocalDateTime paymentDate = LocalDateTime
                    .ofEpochSecond(paymentIntent.getCreated(),
                        0, OffsetDateTime.now().getOffset());
                final BigDecimal eurToRon = new XmlBnr().euroToRon();
                final Payment payment = this.storage.invoices()
                    .registerAsPaid(
                        new StoredInvoice(
                            invoice.invoiceId(),
                            invoice.contract(),
                            invoice.createdAt(),
                            new StoredPayment(
                                invoice.invoiceId(),
                                paymentIntent.getId(),
                                paymentDate,
                                totalAmount,
                                Payment.Status.SUCCESSFUL,
                                "",
                                this.storage
                            ),
                            invoice.billedBy(),
                            invoice.billedTo(),
                            contributorBilling.country(),
                            contract.project().billingInfo().country(),
                            eurToRon,
                            this.storage
                        ),
                        vat,
                        eurToRon
                    );
                this.updateCash(newLimit);
                return payment;
            } else {
                LOG.error("[STRIPE] PaymentIntent status: " + status);
                LOG.error("[STRIPE] Cancelling PaymentIntent...");
                paymentIntent.cancel();
                LOG.error("[STRIPE] PaymentIntent successfully cancelled.");
                throw new WalletPaymentException(
                    "Stripe payment intent status \"" + status + "\". "
                    + "Payment intent cancelled. "
                );
            }
        } catch (final StripeException ex) {
            final int invoiceId = invoice.invoiceId();
            final String code = ex.getCode();
            LOG.error(
                String.format(
                    "[STRIPE] StripeException (code %s, request id %s) "
                    + "while trying to pay Invoice #%s.",
                    code,
                    ex.getRequestId(),
                    invoiceId
                ),
                ex
            );
            if("authentication_required".equalsIgnoreCase(code)) {
                throw new WalletPaymentException(
                    "The card requires authentication."
                );
            }
            if("card_declined".equalsIgnoreCase(code)) {
                final String message = ex.getMessage();
                throw new WalletPaymentException(
                    "Stripe message: "
                    + message.substring(0, message.indexOf(';')) + ". "
                );
            }
            throw new IllegalStateException(
                "Stripe threw an exception when trying execute PaymentIntent"
                + " for invoice #" + invoiceId + ": " + ex.getMessage(),
                ex
            );
        }
    }

    /**
     * Ensure that Stripe API token is set.
     */
    private void ensureApiToken() {
        if (this.stripeApiToken == null
            || this.stripeApiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = this.stripeApiToken;
    }

    @Override
    public String type() {
        return Type.STRIPE;
    }

    @Override
    public boolean active() {
        return this.active;
    }

    @Override
    public Project project() {
        return this.project;
    }

    @Override
    public Wallet updateCash(final BigDecimal cash) {
        return this.storage
            .wallets()
            .ofProject(this.project)
            .updateCash(this, cash);
    }

    @Override
    public SetupIntent paymentMethodSetupIntent() {
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            return SetupIntent.create(
                SetupIntentCreateParams.builder()
                    .setCustomer(this.identifier)
                    .setUsage(SetupIntentCreateParams.Usage.OFF_SESSION)
                    .build()
            );
        } catch (final StripeException ex) {
            LOG.error(
                "StripeException when trying to create a "
                + "PaymentMethod SetupIntent for Wallet " + this.identifier
            );
            throw new IllegalStateException(
                "Could not create Stripe SetupIntent",
                ex
            );
        }
    }

    @Override
    public PaymentMethods paymentMethods() {
        return this.storage.paymentMethods().ofWallet(this);
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public BillingInfo billingInfo() {
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            return new CustomerBillingInfo(
                Customer.retrieve(this.identifier)
            );
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "StripeException when fetching the Customer from Stripe",
                ex
            );
        }
    }

    @Override
    public Wallet activate() {
        final Wallet activated;
        if(this.active) {
            activated = this;
        } else {
            final Wallets all = this.storage.wallets();
            activated = all.activate(this);
            for (final Wallet wallet : all.ofProject(this.project)) {
                if (Type.FAKE.equalsIgnoreCase(wallet.type())) {
                    wallet.remove();
                    break;
                }
            }
        }
        return activated;
    }

    @Override
    public boolean remove() {
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final boolean deleted;
            final Customer removed = Customer.retrieve(this.identifier)
                .delete();
            if(removed.getDeleted()) {
                deleted = this.storage.wallets().remove(this);
            } else {
                deleted = false;
            }
            return deleted;
        } catch (final StripeException ex) {
            LOG.error(
                "StripeException when trying to delete "
                + "the Wallet (Customer) " + this.identifier
            );
            throw new IllegalStateException(
                "StripeException when trying to delete "
                + "the Wallet (Customer) " + this.identifier,
                ex
            );
        }
    }

    @Override
    public boolean equals(final Object other) {
        boolean equals;
        if (this == other) {
            equals = true;
        } else {
            if (!(other instanceof Wallet)) {
                equals = false;
            } else {
                final Wallet otherWallet = (Wallet) other;
                equals = this.type().equals(otherWallet.type())
                    && this.project.equals(otherWallet.project());
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.project, this.type());
    }

    /**
     * Calculate the VAT for Self's commission.<br><br>
     *
     * If Contributor is from Romania, VAT is 19% always.<br><br>
     *
     * If Contributor is from other countries of the EU, VAT is 19%
     * <b>unless</b> they provide a VAT number (TaxId), in which
     * case it is 0.<br><br>
     *
     * If Contributor is from outside of the EU, VAT is 0.
     *
     * @param commission Self's commission.
     * @param contributor BillingInfo of the Contributor.
     * @return BigDecimal.
     */
    private BigDecimal calculateVat(
        final BigDecimal commission,
        final BillingInfo contributor
    ) {
        final BigDecimal calculated;
        final String countryCode = contributor.country();
        if(Country.isFromEu(countryCode)) {
            final BigDecimal vat = commission.multiply(
                BigDecimal.valueOf(19)
            ).divide(
                BigDecimal.valueOf(100),
                0,
                RoundingMode.HALF_UP
            );
            if(!"RO".equalsIgnoreCase(countryCode)) {
                final String taxId = contributor.taxId();
                if (taxId != null && !taxId.isEmpty()) {
                    calculated = BigDecimal.valueOf(0);
                } else {
                    calculated = vat;
                }
            } else {
                calculated = vat;
            }
        } else {
            calculated = BigDecimal.valueOf(0);
        }
        return calculated;
    }


}
