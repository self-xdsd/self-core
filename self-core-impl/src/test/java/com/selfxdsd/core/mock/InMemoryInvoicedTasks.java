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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.InvoicedTasks;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.InvoiceTasks;
import com.selfxdsd.core.contracts.invoices.StoredInvoicedTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * InvoicedTasks in memory.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class InMemoryInvoicedTasks implements InvoicedTasks {
    /**
     * Invoice id generator.
     */
    private int idGenerator;

    /**
     * Table for invoiced tasks.
     */
    private final Map<Integer, InvoicedTask> tasks = new HashMap<>();

    /**
     * Storage context.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param storage Storage
     */
    public InMemoryInvoicedTasks(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public InvoicedTasks ofInvoice(final Invoice invoice) {
        return new InvoiceTasks(
            invoice,
            () -> StreamSupport
                .stream(this.tasks.values().spliterator(), false)
                .filter(i -> i.invoice().invoiceId() == invoice.invoiceId()),
            this.storage
        );
    }

    @Override
    public InvoicedTask register(
        final Invoice invoice,
        final Task finished,
        final BigDecimal projectCommission,
        final BigDecimal contributorCommission
    ) {
        final InvoicedTask registered = new StoredInvoicedTask(
            this.idGenerator++,
            invoice.invoiceId(),
            finished.value(),
            projectCommission,
            contributorCommission,
            finished,
            this.storage
        );
        this.tasks.put(registered.invoicedTaskId(), registered);
        return registered;
    }

    @Override
    public Iterator<InvoicedTask> iterator() {
        throw new UnsupportedOperationException(
            "It is not possible to iterate over all invoiced tasks. "
          + "Call #ofInvoice(...) first."
        );
    }
}
