/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Payment stored in Self.
 * @author Ali FELLAHI (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class StoredPayment implements Payment {

    /**
     * Id of the Invoice to which this Payment belongs.
     */
    private int invoiceId;

    /**
     * Successful Payment transaction id.
     */
    private final String transactionId;

    /**
     * Payment timestamp.
     */
    private final LocalDateTime paymentTime;

    /**
     * Payment amount.
     */
    private final BigDecimal value;

    /**
     * Payment status.
     */
    private final String status;

    /**
     * Payment failure reason.
     */
    private final String failReason;

    /**
     * Self storage API.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param invoiceId Id of the Invoice to which this Payment belongs.
     * @param transactionId Successful payment transaction id.
     * @param paymentTime Payment time.
     * @param value Payment amount.
     * @param status Payment status (FAILED or SUCCESSFUL).
     * @param failReason Payment failure reason.
     * @param storage Self Storage.
     */
    public StoredPayment(
        final int invoiceId,
        final String transactionId,
        final LocalDateTime paymentTime,
        final BigDecimal value,
        final String status,
        final String failReason,
        final Storage storage
    ) {
        this.invoiceId = invoiceId;
        this.transactionId = transactionId;
        this.paymentTime = paymentTime;
        this.value = value;
        this.status = status;
        this.failReason = failReason;
        this.storage = storage;
    }

    @Override
    public Invoice invoice() {
        return this.storage.invoices().getById(this.invoiceId);
    }

    @Override
    public PlatformInvoice platformInvoice() {
        final PlatformInvoice platformInvoice;
        if (Status.SUCCESSFUL.equals(this.status())){
            platformInvoice = this.storage.platformInvoices().getByPayment(
                this.transactionId(),
                this.paymentTime()
            );
        } else {
            platformInvoice = null;
        }
        return platformInvoice;
    }

    @Override
    public String transactionId() {
        return this.transactionId;
    }

    @Override
    public LocalDateTime paymentTime() {
        return this.paymentTime;
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }

    @Override
    public String status() {
        return this.status;
    }

    @Override
    public String failReason() {
        return this.failReason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.invoiceId,
            this.paymentTime,
            this.transactionId
        );
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof StoredPayment
            && this.invoiceId == ((StoredPayment) obj).invoiceId
            && this.paymentTime.isEqual(((StoredPayment) obj).paymentTime)
            && this.transactionId.equals(((StoredPayment) obj).transactionId));
    }
}
