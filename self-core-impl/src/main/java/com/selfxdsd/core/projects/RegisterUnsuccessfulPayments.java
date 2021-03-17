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
import com.stripe.model.SetupIntent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Wallet decorator which will catch exceptions thrown by Wallet.pay(...)
 * and register the appropriate unsuccessful Payment.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class RegisterUnsuccessfulPayments implements Wallet  {

    /**
     * Original wallet.
     */
    private final Wallet original;

    /**
     * Ctor.
     * @param original Original Wallet.
     */
    public RegisterUnsuccessfulPayments(final Wallet original) {
        this.original = original;
    }

    @Override
    public BigDecimal cash() {
        return this.original.cash();
    }

    @Override
    public Payment pay(final Invoice invoice) {
        Payment payment;
        try {
            payment = this.original.pay(invoice);
        } catch (final WalletPaymentException failed) {
            payment = invoice.payments().register(
                invoice, "", LocalDateTime.now(),
                invoice.totalAmount(),
                Payment.Status.FAILED, failed.getMessage()
            );
        } catch (final IllegalStateException errored) {
            payment = invoice.payments().register(
                invoice, "", LocalDateTime.now(),
                invoice.totalAmount(),
                Payment.Status.ERROR, errored.getMessage()
            );
        }
        return payment;
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

    @Override
    public boolean equals(final Object obj) {
        return this.original.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.original.hashCode();
    }
}
