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
import com.stripe.model.SetupIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Wallet decorator which perform all the required pre-checks before making
 * a payment (invoice is not paid, the cash limit is not exceeded etc).
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
public final class PreCheckPayments implements Wallet  {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        StripeWallet.class
    );

    /**
     * Original wallet.
     */
    private final Wallet original;

    /**
     * Ctor.
     * @param original Original Wallet.
     */
    public PreCheckPayments(final Wallet original) {
        this.original = original;
    }

    @Override
    public BigDecimal cash() {
        return this.original.cash();
    }

    @Override
    public Payment pay(final Invoice invoice) {
        if (!this.project().equals(invoice.contract().project())) {
            LOG.error("[Payment-PreCheck] Invoice is not part of project."
                    + this.project().repoFullName());
            throw new InvoiceException.NotPartOfProjectContract(invoice,
                this.project());
        }
        if (invoice.isPaid()) {
            LOG.error("[Payment-PreCheck] Invoice already paid.");
            throw new InvoiceException.AlreadyPaid(invoice);
        }
        if (invoice.totalAmount().longValueExact() < 108 * 100) {
            LOG.error("[Payment-PreCheck] In order to be paid, Invoice amount"
                + " must be at least 108 €.");
            throw new WalletPaymentException("In order to be paid, Invoice"
                + " amount must be at least 108 €.");
        }
        final BigDecimal newLimit = this.cash().subtract(
            invoice.totalAmount()
        );
        if (newLimit.longValue() < 0L) {
            LOG.error("[Payment-PreCheck] Not enough cash to pay Invoice.");
            throw new WalletPaymentException(
                "Invoice value exceeds the limit of the project's wallet."
            );
        }
        return this.original.pay(invoice);
    }

    @Override
    public String type() {
        return this.original.type();
    }

    @Override
    public boolean active() {
        return this.original.active();
    }

    @Override
    public Project project() {
        return this.original.project();
    }

    @Override
    public Wallet updateCash(final BigDecimal cash) {
        return this.original.updateCash(cash);
    }

    @Override
    public SetupIntent paymentMethodSetupIntent() {
        return this.original.paymentMethodSetupIntent();
    }

    @Override
    public PaymentMethods paymentMethods() {
        return this.original.paymentMethods();
    }

    @Override
    public String identifier() {
        return this.original.identifier();
    }

    @Override
    public BillingInfo billingInfo() {
        return this.original.billingInfo();
    }

    @Override
    public Wallet activate() {
        return this.original.activate();
    }

    @Override
    public boolean remove() {
        return this.original.remove();
    }
}