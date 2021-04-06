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
import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.Invoices;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link StoredInvoicedTask}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class StoredInvoicedTaskTestCase {

    /**
     * StoredInvoicedTask can return its id.
     */
    @Test
    public void returnsItsId() {
        final InvoicedTask task = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            Mockito.mock(Task.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.invoicedTaskId(),
            Matchers.equalTo(12)
        );
    }

    /**
     * StoredInvoicedTask can return its Task.
     */
    @Test
    public void returnsItsTask() {
        final Task task = Mockito.mock(Task.class);
        final InvoicedTask invoiced = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            task,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoiced.task(),
            Matchers.is(task)
        );
    }

    /**
     * StoredInvoicedTask can return its value.
     */
    @Test
    public void returnsItsValue() {
        final InvoicedTask task = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            Mockito.mock(Task.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.value(),
            Matchers.equalTo(BigDecimal.valueOf(25000))
        );
    }

    /**
     * StoredInvoicedTask can return the PM commission.
     */
    @Test
    public void returnsCommission() {
        final InvoicedTask task = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            Mockito.mock(Task.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.projectCommission(),
            Matchers.equalTo(BigDecimal.valueOf(50))
        );
    }

    /**
     * StoredInvoicedTask can return the is total amount.
     */
    @Test
    public void returnsTotalAmount() {
        final InvoicedTask task = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            Mockito.mock(Task.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(25050))
        );
    }

    /**
     * StoredInvoicedTask reads the Invoice from the storage.
     */
    @Test
    public void lazyLoadsInvoice() {
        final Invoice invoiceOne = Mockito.mock(Invoice.class);
        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(all.getById(1)).thenReturn(invoiceOne);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);

        final InvoicedTask task = new StoredInvoicedTask(
            12,
            1,
            BigDecimal.valueOf(25000),
            BigDecimal.valueOf(50),
            Mockito.mock(Task.class),
            storage
        );

        MatcherAssert.assertThat(task.invoice(), Matchers.is(invoiceOne));
    }

}
