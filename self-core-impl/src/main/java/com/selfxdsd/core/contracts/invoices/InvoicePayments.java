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
package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Payment;
import com.selfxdsd.api.Payments;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Payments belonging to an Invoice.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class InvoicePayments implements Payments {

    /**
     * Invoice to which these Payments belong.
     */
    private final Invoice invoice;

    /**
     * The payments.
     */
    private final Supplier<Stream<Payment>> payments;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param invoice Invoice to which the payments belong.
     * @param payments The payments.
     * @param storage Self's Storage.
     */
    public InvoicePayments(
        final Invoice invoice,
        final Supplier<Stream<Payment>> payments,
        final Storage storage
    ) {
        this.invoice = invoice;
        this.payments = payments;
        this.storage = storage;
    }

    @Override
    public Payment register(
        final Invoice invoice, final String transactionId,
        final LocalDateTime timestamp, final BigDecimal value,
        final String status, final String failReason
    ) {
        if(this.invoice.invoiceId() != invoice.invoiceId()) {
            throw new IllegalStateException(
                "These are the Payments of Invoice " + this.invoice.invoiceId()
                + ". You cannot register a Payment for another Invoice here."
            );
        }
        return this.storage.payments().register(
            invoice, transactionId, timestamp, value, status, failReason
        );
    }

    @Override
    public Payments ofInvoice(final Invoice invoice) {
        if(this.invoice.invoiceId() == invoice.invoiceId()){
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the Payments of Invoice " + this.invoice.invoiceId()
            + ". You cannot see the Payments of another Invoice here."
        );
    }

    @Override
    public Iterator<Payment> iterator() {
        return this.payments.get().iterator();
    }
}
