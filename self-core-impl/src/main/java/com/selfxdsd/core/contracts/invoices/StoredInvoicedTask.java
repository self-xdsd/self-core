/**
 * Copyright (c) 2020, Self XDSD Contributors
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
import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;

/**
 * A finished Task attached to an Invoice.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class StoredInvoicedTask implements InvoicedTask {

    /**
     * ID of this invoiced Task.
     */
    private final int invoicedTaskId;

    /**
     * Invoice ID.
     */
    private final int invoiceId;

    /**
     * Value of this invoiced task, in cents.
     *
     * We store this value in the DB instead of
     * returning it on-the-fly from the Task because
     * the Contract's hourly rate can change, yet the
     * value of a Task which has already been invoiced should
     * not change.
     */
    private final BigDecimal value;

    /**
     * The finished Task.
     */
    private final Task task;

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param invoicedTaskId This task's ID.
     * @param invoiceId Invoice ID.
     * @param value This invoiced Task's value in gcents.
     * @param task The Task.
     * @param storage Parent storage.
     */
    public StoredInvoicedTask(
        final int invoicedTaskId,
        final int invoiceId,
        final BigDecimal value,
        final Task task,
        final Storage storage
    ) {
        this.invoicedTaskId = invoicedTaskId;
        this.invoiceId = invoiceId;
        this.value = value;
        this.task = task;
        this.storage = storage;
    }

    @Override
    public int invoicedTaskId() {
        return this.invoicedTaskId;
    }

    @Override
    public Invoice invoice() {
        return this.storage.invoices().getById(this.invoiceId);
    }

    @Override
    public Task task() {
        return this.task;
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }
}
