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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Invoices;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link ContractInvoices}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class ContractInvoicesTestCase {

    /**
     * ContractInvoices can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Invoices invoices = new ContractInvoices(
            Mockito.mock(Contract.Id.class),
            () -> {
                final List<Invoice> list = new ArrayList<>();
                list.add(Mockito.mock(Invoice.class));
                list.add(Mockito.mock(Invoice.class));
                list.add(Mockito.mock(Invoice.class));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(invoices, Matchers.iterableWithSize(3));
    }

    /**
     * Method getById returns a found Invoice.
     */
    @Test
    public void getByIdReturnsInvoice() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.invoiceId()).thenReturn(1);
        final Invoices invoices = new ContractInvoices(
            Mockito.mock(Contract.Id.class),
            () -> {
                final List<Invoice> list = new ArrayList<>();
                list.add(invoice);
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        final Invoice found = invoices.getById(1);
        MatcherAssert.assertThat(found, Matchers.is(invoice));
    }

    /**
     * Method getById returns a null if the invoice is not found.
     */
    @Test
    public void getByIdReturnsNull() {
        final Invoices invoices = new ContractInvoices(
            Mockito.mock(Contract.Id.class),
            () -> new ArrayList<Invoice>().stream(),
            Mockito.mock(Storage.class)
        );
        final Invoice found = invoices.getById(1);
        MatcherAssert.assertThat(found, Matchers.nullValue());
    }

    /**
     * Method ofContract returns self if the id matches.
     */
    @Test
    public void ofContractReturnsSelf() {
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoices.ofContract(contractId),
            Matchers.is(invoices)
        );
    }

    /**
     * Method ofContract complains if a different contract id is specified.
     */
    @Test (expected = IllegalStateException.class)
    public void ofContractComplainsOnDifferentId() {
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Contract.Id other = new Contract.Id(
            "john/test2",
            "vlad",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            Mockito.mock(Storage.class)
        );
        invoices.ofContract(other);
    }

    /**
     * Method createNewInvoice creates a new invoice if the Contract Id matches.
     */
    @Test
    public void createsNewInvoice() {
        final Invoice created = Mockito.mock(Invoice.class);
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(all.createNewInvoice(contractId)).thenReturn(created);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);

        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            storage
        );

        MatcherAssert.assertThat(
            invoices.createNewInvoice(contractId),
            Matchers.is(created)
        );
    }

    /**
     * Method createNewInvoice complains if a different contract id
     * is specified.
     */
    @Test (expected = IllegalStateException.class)
    public void createNewInvoiceComplainsOnDifferentId() {
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Contract.Id other = new Contract.Id(
            "john/test2",
            "vlad",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            Mockito.mock(Storage.class)
        );
        invoices.createNewInvoice(other);
    }

    /**
     * Method registerPaidInvoice works.
     */
    @Test
    public void registersPaidInvoice() {
        final BigDecimal contributorVat = BigDecimal.valueOf(3);
        final BigDecimal eurToRon = BigDecimal.valueOf(487);

        final Invoice invoice = Mockito.mock(Invoice.class);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(invoice.contract()).thenReturn(contract);

        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        Mockito.when(contract.contractId()).thenReturn(contractId);

        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(
            all.registerAsPaid(invoice, contributorVat, eurToRon)
        ).thenReturn(Boolean.TRUE);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);

        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            storage
        );

        MatcherAssert.assertThat(
            invoices.registerAsPaid(invoice, contributorVat, eurToRon),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Method registerPaidInvoice complains if the given Invoice belongs
     * to another contract.
     */
    @Test (expected = IllegalStateException.class)
    public void registerPaidInvoiceComplainsOnDifferentContract() {
        final BigDecimal contributorVat = BigDecimal.valueOf(3);
        final BigDecimal eurToRon = BigDecimal.valueOf(487);

        final Invoice invoice = Mockito.mock(Invoice.class);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(invoice.contract()).thenReturn(contract);
        final Contract.Id other = new Contract.Id(
            "john/test",
            "vlad",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        Mockito.when(contract.contractId()).thenReturn(other);

        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );

        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(
            all.registerAsPaid(invoice, contributorVat, eurToRon)
        ).thenReturn(Boolean.TRUE);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);

        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> new ArrayList<Invoice>().stream(),
            storage
        );

        MatcherAssert.assertThat(
            invoices.registerAsPaid(invoice, contributorVat, eurToRon),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Method active returns the single unpaid Invoice.
     */
    @Test
    public void activeReturnsTheUnpaidInvoice() {
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> {
                final List<Invoice> list = new ArrayList<>();
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.FALSE));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        final Invoice active = invoices.active();
        MatcherAssert.assertThat(active.isPaid(), Matchers.is(Boolean.FALSE));
    }

    /**
     * Method active returns the oldest unpaid invoice, if
     * more unpaid invoices are found.
     */
    @Test
    public void activeReturnsOldestUnpaidInvoice() {
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoice oldest = this.mockInvoice(contractId, Boolean.FALSE);
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> {
                final List<Invoice> list = new ArrayList<>();
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(oldest);
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.FALSE));
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.FALSE));
                return list.stream();
            },
            Mockito.mock(Storage.class)
        );
        final Invoice active = invoices.active();
        MatcherAssert.assertThat(active, Matchers.is(oldest));
    }

    /**
     * Method active creates and returns a new Invoice if all
     * existing invoices are paid (not active).
     */
    @Test
    public void activeCreatesAndReturnsInvoice() {
        final Invoice created = Mockito.mock(Invoice.class);
        final Contract.Id contractId = new Contract.Id(
            "john/test",
            "mihai",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(all.createNewInvoice(contractId)).thenReturn(created);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);
        final Invoices invoices = new ContractInvoices(
            contractId,
            () -> {
                final List<Invoice> list = new ArrayList<>();
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                list.add(this.mockInvoice(contractId, Boolean.TRUE));
                return list.stream();
            },
            storage
        );
        final Invoice active = invoices.active();
        MatcherAssert.assertThat(active, Matchers.is(created));
    }

    /**
     * Mock an invoice for test.
     * @param contractId ContractId.
     * @param isPaid Is the invoice paid or not?
     * @return Invoice.
     */
    private Invoice mockInvoice(
        final Contract.Id contractId,
        final boolean isPaid
    ){
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(contractId);
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.isPaid()).thenReturn(isPaid);
        Mockito.when(invoice.createdAt()).thenReturn(LocalDateTime.now());
        return invoice;
    }
}
