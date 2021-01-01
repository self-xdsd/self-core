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
import com.selfxdsd.api.exceptions.InvoiceException;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
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
     */
    @Override
    public Wallet pay(final Invoice invoice) {
        final Contract contract = invoice.contract();
        LOG.debug(
            "[STRIPE] Trying to pay Invoice #" + invoice.invoiceId() + " of "
            + " Contract " + contract.contractId()
            + " from Wallet " + this.identifier
        );
        if (invoice.isPaid()) {
            LOG.error("[STRIPE] Invoice already paid.");
            throw new InvoiceException.AlreadyPaid(invoice);
        }

        if (invoice.amount().longValueExact() < 108 * 100) {
            LOG.error("[STRIPE] In order to be paid, Invoice amount must"
                + " be at least 108€.");
            throw new WalletPaymentException("In order to be paid, Invoice"
                + " amount must be at least 108€.");
        }

        final BigDecimal newLimit = this.limit.subtract(
            invoice.totalAmount()
        );
        if (newLimit.longValue() < 0L) {
            LOG.error("[STRIPE] Not enough cash to pay Invoice.");
            throw new WalletPaymentException("No cash available in wallet "
                + "for paying invoice #" + invoice.invoiceId()
                + ". Please increase the Limit from your dashboard with"
                + " at least " + newLimit.abs().add(invoice.totalAmount())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP) + " €."
            );
        }

        ensureApiToken();

        try {
            final Contributor contributor = invoice.contract().contributor();
            final PayoutMethod payoutMethod = this.storage
                .payoutMethods()
                .ofContributor(contributor)
                .active();
            if (payoutMethod == null) {
                LOG.error(
                    "[STRIPE] Contributor " + contributor.username()
                    + " from " + contributor.provider() + " has no "
                    + "PayoutMethod set up, cannot pay."
                );
                throw new WalletPaymentException(
                    "Contributor " + contributor.username()
                    + " hasn't finished setting up their Stripe account yet. "
                    + "We cannot make the payment yet."

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
                    "No active payment method for wallet #"
                    + this.identifier + " of project "
                    + this.project.repoFullName() + "/"
                    + this.project.provider()
                );
            }
            final PaymentIntent paymentIntent = PaymentIntent
                .create(
                    PaymentIntentCreateParams.builder()
                        .setCurrency("eur")
                        .setAmount(invoice.totalAmount().longValueExact())
                        .setCustomer(this.identifier)
                        .setPaymentMethod(paymentMethod.identifier())
                        .setTransferData(
                            PaymentIntentCreateParams.TransferData
                                .builder()
                                .setDestination(payoutMethod.identifier())
                                .setAmount(invoice.amount().longValueExact())
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
                this.storage.invoices()
                    .registerAsPaid(
                        new StoredInvoice(
                            invoice.invoiceId(),
                            invoice.contract(),
                            invoice.createdAt(),
                            paymentDate,
                            paymentIntent.getId(),
                            invoice.billedBy(),
                            invoice.billedTo(),
                            this.storage
                        )
                    );
            } else {
                LOG.error("[STRIPE] PaymentIntent status: " + status);
                throw new WalletPaymentException(
                    "Could not pay invoice #" + invoice.invoiceId() + " due to"
                    + " Stripe payment intent status \"" + status + "\""
                );
            }
        } catch (final StripeException ex) {
            LOG.error(
                "[STRIPE] StripeException while trying "
                + "to make the payment.",
                ex
            );
            throw new IllegalStateException(
                "Stripe threw an exception when trying execute PaymentIntent"
                + " for invoice #" + invoice.invoiceId(),
                ex
            );
        }
        return this.updateCash(newLimit);
    }

    /**
     * Ensure that Stripe API token is set.
     */
    private void ensureApiToken() {
        if (this.stripeApiToken == null
            || this.stripeApiToken.trim().isEmpty()) {
            throw new WalletPaymentException(
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
        final Map<String, Object> params = new HashMap<>();
        params.put("customer", this.identifier);
        try {
            return SetupIntent.create(params);
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
    public boolean equals(final Object other) {
        boolean equals;
        if (this == other) {
            equals = true;
        } else {
            if (other == null || getClass() != other.getClass()) {
                equals = false;
            } else {
                final StripeWallet stripeWallet = (StripeWallet) other;
                equals = this.project.equals(stripeWallet.project);
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.project);
    }
}
