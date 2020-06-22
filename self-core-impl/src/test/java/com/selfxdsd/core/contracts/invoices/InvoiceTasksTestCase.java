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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link InvoiceTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class InvoiceTasksTestCase {

    /**
     * InvoiceTasks can be iterated.
     */
    @Test
    public void canBeIterated() {
        final InvoicedTasks tasks = new InvoiceTasks(
            1,
            () -> {
                final List<InvoicedTask> list = new ArrayList<>();
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(3));
    }

    /**
     * InvoiceTasks.ofInvoice returns self when the ID matches.
     */
    @Test
    public void ofInvoiceReturnsSelf() {
        final InvoicedTasks tasks = new InvoiceTasks(
            1,
            () -> {
                final List<InvoicedTask> list = new ArrayList<>();
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.ofInvoice(1), Matchers.is(tasks));
    }

    /**
     * InvoiceTasks.ofInvoice complains if the ID of a different
     * Invoice is given as input.
     */
    @Test(expected = IllegalStateException.class)
    public void ofInvoiceComplainsOnDifferentId() {
        final InvoicedTasks tasks = new InvoiceTasks(
            1,
            () -> {
                final List<InvoicedTask> list = new ArrayList<>();
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        tasks.ofInvoice(2);
    }

    /**
     * InvoiceTasks.register complains if the ID of a different Invoice
     * is specified.
     */
    @Test (expected = IllegalStateException.class)
    public void registerComplainsOnDifferentId() {
        final InvoicedTasks tasks = new InvoiceTasks(
            1,
            () -> {
                final List<InvoicedTask> list = new ArrayList<>();
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        tasks.register(2, Mockito.mock(Task.class));
    }

    /**
     * InvoiceTasks.register works if the specified Invoice ID matches.
     */
    @Test
    public void registersFinishedTask() {
        final InvoicedTask registered = Mockito.mock(InvoicedTask.class);
        final Task finished = Mockito.mock(Task.class);
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.register(1, finished)).thenReturn(registered);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoicedTasks()).thenReturn(all);

        final InvoicedTasks tasks = new InvoiceTasks(
            1,
            () -> {
                final List<InvoicedTask> list = new ArrayList<>();
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                list.add(Mockito.mock(InvoicedTask.class));
                return list.stream();
            },
            storage
        );
        MatcherAssert.assertThat(
            tasks.register(1, finished),
            Matchers.is(registered)
        );
    }
}
