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
package com.selfxdsd.core;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PlatformInvoice stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.50
 */
public final class StoredPlatformInvoice implements PlatformInvoice {

    /**
     * Internal Id.
     */
    private final int id;

    /**
     * Creation date.
     */
    private final LocalDateTime createdAt;

    /**
     * Billing data of the contributor.
     */
    private final String billedTo;

    /**
     * Invoiced commission.
     */
    private final BigDecimal commission;

    /**
     * Invoiced VAT.
     */
    private final BigDecimal vat;

    /**
     * Transaction ID (id of the payment).
     */
    private final String transactionId;

    /**
     * Payment timestamp.
     */
    private final LocalDateTime paymentTime;

    /**
     * ID of the corresponding Invoice.
     */
    private final int invoiceId;

    /**
     * Self storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param id Internal ID of this PlatformInvoice.
     * @param createdAt Creation time.
     * @param billedTo Billing info of the contributor.
     * @param commission Invoiced commission.
     * @param vat Invoiced VAT.
     * @param transactionId Transaction (payment) ID.
     * @param paymentTime Payment timestamp.
     * @param invoiceId ID of the corresponding Invoice.
     * @param storage Self Storage.
     */
    public StoredPlatformInvoice(
        final int id,
        final LocalDateTime createdAt,
        final String billedTo,
        final BigDecimal commission,
        final BigDecimal vat,
        final String transactionId,
        final LocalDateTime paymentTime,
        final int invoiceId,
        final Storage storage
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.billedTo = billedTo;
        this.commission = commission;
        this.vat = vat;
        this.transactionId = transactionId;
        this.paymentTime = paymentTime;
        this.invoiceId = invoiceId;
        this.storage = storage;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    @Override
    public String billedTo() {
        return this.billedTo;
    }

    @Override
    public BigDecimal commission() {
        return this.commission;
    }

    @Override
    public BigDecimal vat() {
        return this.vat;
    }

    @Override
    public Invoice invoice() {
        return this.storage.invoices().getById(this.invoiceId);
    }

    @Override
    public String transactionId() {
        return this.transactionId;
    }

    @Override
    public LocalDateTime paymentTime() {
        return this.paymentTime;
    }
}
