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
import com.selfxdsd.api.exceptions.PaymentMethodsException;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;
import com.stripe.model.SetupIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

/**
 * A Project's Fake wallet.
 * @author criske
 * @version $Id$
 * @since 0.0.39
 */
public final class FakeWallet implements Wallet {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        FakeWallet.class
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
     * Ctor.
     * @param storage Self storage.
     * @param project Project to which this wallet belongs.
     * @param limit Cash limit we're allowed to use.
     * @param identifier Wallet identifier.
     * @param active Is this wallet active or not?
     */
    public FakeWallet(
        final Storage storage,
        final Project project,
        final BigDecimal limit,
        final String identifier,
        final boolean active
    ) {
        this.storage = storage;
        this.project = project;
        this.limit = limit;
        this.identifier = identifier;
        this.active = active;
    }

    @Override
    public BigDecimal cash() {
        return this.limit;
    }

    @Override
    public Wallet pay(final Invoice invoice) {
        if (!this.project.equals(invoice.contract().project())) {
            throw new InvoiceException.NotPartOfProjectContract(invoice,
                this.project);
        }
        if(invoice.isPaid()) {
            throw new InvoiceException.AlreadyPaid(invoice);
        }
        final BigDecimal newCash = this.limit.subtract(invoice
            .totalAmount());
        if (newCash.longValueExact() < 0L) {
            throw new WalletPaymentException("No cash available in wallet "
                + "for paying invoice #" + invoice.invoiceId()
                + ". Please increase the limit from your dashboard with"
                + " at least " + newCash.abs().divide(BigDecimal
                .valueOf(1000), RoundingMode.HALF_UP) + "\u20ac."
            );
        }
        LOG.debug(
            "[FAKE] Paying Invoice #" + invoice.invoiceId()
            + " of Contract " + invoice.contract().contractId()
            + "..."
        );
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        this.storage
            .invoices()
            .registerAsPaid(
                new StoredInvoice(
                    invoice.invoiceId(),
                    invoice.contract(),
                    invoice.createdAt(),
                    LocalDateTime.now(),
                    "fake_payment_" + uuid,
                    invoice.billedBy(),
                    invoice.billedTo(),
                    "FK Country",
                    "FK Country",
                    BigDecimal.valueOf(0),
                    this.storage
                ),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(0)
            );
        return this.updateCash(newCash);
    }

    @Override
    public String type() {
        return Type.FAKE;
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
        return this.project()
            .wallets()
            .updateCash(this, cash);
    }

    @Override
    public SetupIntent paymentMethodSetupIntent() {
        throw new UnsupportedOperationException(
            "This operation is available only for Stripe wallets!"
        );
    }

    @Override
    public PaymentMethods paymentMethods() {
        return new PaymentMethods() {
            @Override
            public PaymentMethod register(final Wallet wallet,
                                          final String identifier) {
                throw new PaymentMethodsException("Can't register a "
                    + " payment method for a missing wallet.");
            }

            @Override
            public boolean remove(final PaymentMethod paymentMethod) {
                throw new PaymentMethodsException("Can't remove a "
                    + " payment method for a missing wallet.");
            }

            @Override
            public PaymentMethods ofWallet(final Wallet wallet) {
                throw new PaymentMethodsException("A missing wallet "
                    + " has no payment methods.");
            }

            @Override
            public PaymentMethod active() {
                return null;
            }

            @Override
            public PaymentMethod activate(
                final PaymentMethod paymentMethod
            ) {
                throw new PaymentMethodsException("Can't activate "
                    + "a FakeWallet payment method.");
            }

            @Override
            public PaymentMethod deactivate(final PaymentMethod paymentMethod) {
                throw new PaymentMethodsException("Can't deactivate "
                    + "a FakeWallet payment method.");
            }

            @Override
            public Iterator<PaymentMethod> iterator() {
                return Collections.emptyIterator();
            }
        };
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public BillingInfo billingInfo() {
        return new BillingInfo() {
            @Override
            public boolean isCompany() {
                return true;
            }

            @Override
            public String legalName() {
                return FakeWallet.this.project.repoFullName();
            }

            @Override
            public String firstName() {
                return "";
            }

            @Override
            public String lastName() {
                return "";
            }

            @Override
            public String country() {
                return "";
            }

            @Override
            public String address() {
                return FakeWallet.this.project.provider();
            }

            @Override
            public String city() {
                return "";
            }

            @Override
            public String zipcode() {
                return "";
            }

            @Override
            public String email() {
                return "";
            }

            @Override
            public String taxId() {
                return "";
            }

            @Override
            public String other() {
                return "";
            }

            @Override
            public String toString() {
                return "Project " + this.legalName()
                    + " at " + this.address() + ".";
            }
        };
    }

    @Override
    public Wallet activate() {
        final Wallet activated;
        if(this.active) {
            activated = this;
        } else {
            activated = this.storage.wallets().activate(this);
        }
        return activated;
    }

    @Override
    public boolean remove() {
        return this.storage.wallets().remove(this);
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
                final FakeWallet missing = (FakeWallet) other;
                equals = this.project.equals(missing.project)
                    && this.type().equals(missing.type());
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.project, this.type());
    }
}
