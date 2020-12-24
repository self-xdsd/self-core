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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.InvoiceException;
import com.selfxdsd.api.exceptions.PaymentMethodsException;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

/**
 * Unit tests for {@link FakeWallet}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.27
 */
public final class FakeWalletTestCase {

    /**
     * The FakeWallet can return its type.
     */
    @Test
    public void returnsType() {
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            fake.type(),
            Matchers.equalTo(Wallet.Type.FAKE)
        );
    }

    /**
     * The FakeWallet can return its cash limit.
     */
    @Test
    public void returnsCashLimit() {
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            fake.cash(),
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );
    }

    /**
     * The FakeWallet can return its identifier.
     */
    @Test
    public void returnsIdentifier() {
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            fake.identifier(),
            Matchers.equalTo("123FakeID")
        );
    }

    /**
     * The FakeWallet can return its Project.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            fake.project(),
            Matchers.is(project)
        );
    }

    /**
     * The FakeWallet can return its BillingInfo.
     */
    @Test
    public void returnsBillingInfo() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        final BillingInfo billing = fake.billingInfo();
        MatcherAssert.assertThat(
            billing.legalName(),
            Matchers.equalTo("mihai/test")
        );
        MatcherAssert.assertThat(
            billing.address(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
        MatcherAssert.assertThat(
            billing.country(),
            Matchers.isEmptyString()
        );
        MatcherAssert.assertThat(
            billing.city(),
            Matchers.isEmptyString()
        );
        MatcherAssert.assertThat(
            billing.zipcode(),
            Matchers.isEmptyString()
        );
        MatcherAssert.assertThat(
            billing.email(),
            Matchers.isEmptyString()
        );
        MatcherAssert.assertThat(
            billing.other(),
            Matchers.isEmptyString()
        );
        MatcherAssert.assertThat(
            billing.toString(),
            Matchers.equalTo(
                "Project mihai/test at " + Provider.Names.GITHUB + "."
            )
        );
    }

    /**
     * The FakeWallet can return its "active" flag.
     */
    @Test
    public void returnsActiveFlag() {
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            fake.active(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Wallet cash limit can be updated.
     */
    @Test
    public void updatesCash() {
        final Storage storage = Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);

        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(project.wallets()).thenReturn(wallets);
        Mockito.when(wallets.updateCash(Mockito.any(), Mockito.any()))
            .thenAnswer(invocation -> new FakeWallet(
                storage,
                project,
                (BigDecimal) invocation.getArguments()[1],
                "id",
                true
            ));
        final Wallet fake = new FakeWallet(
            storage,
            project,
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );
        final Wallet updated = fake.updateCash(BigDecimal.valueOf(900));
        MatcherAssert.assertThat(updated.cash(), Matchers
            .equalTo(BigDecimal.valueOf(900)));
    }

    /**
     * FakeWallet can return its debt.
     */
    @Test
    public void returnsDebt() {
        final Contract contractA = Mockito.mock(Contract.class);
        Mockito.when(contractA.value()).thenReturn(BigDecimal.valueOf(1500));
        final Contract contractB = Mockito.mock(Contract.class);
        Mockito.when(contractB.value()).thenReturn(BigDecimal.valueOf(2000));
        final Contract contractC = Mockito.mock(Contract.class);
        Mockito.when(contractC.value()).thenReturn(BigDecimal.valueOf(3000));
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(List.of(
            contractA, contractB, contractC
        ).iterator());

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final Wallet wallet = new FakeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.valueOf(100_000_000),
            "fake-123w",
            Boolean.TRUE
        );

        MatcherAssert.assertThat(
            wallet.debt(),
            Matchers.equalTo(BigDecimal.valueOf(6500))
        );
    }

    /**
     * FakeWallet has payment setup method intent unsupported.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void hasPaymentMethodSetupIntentUnsupported() {
        new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        ).paymentMethodSetupIntent();
    }

    /**
     * FakeWallet has payment methods unsupported.
     */
    @Test
    public void hasPaymentMethodsUnsupported() {
        final Wallet fake = new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123FakeID",
            Boolean.TRUE
        );

        final PaymentMethods paymentMethods = fake.paymentMethods();

        Assert.assertThrows(PaymentMethodsException.class,
            () -> paymentMethods.register(null, null));
        Assert.assertThrows(PaymentMethodsException.class,
            () -> paymentMethods.remove(null));
        Assert.assertThrows(PaymentMethodsException.class,
            () -> paymentMethods.ofWallet(null));
        MatcherAssert.assertThat(paymentMethods.active(), Matchers.nullValue());
        Assert.assertThrows(PaymentMethodsException.class,
            () -> paymentMethods.activate(null));
    }

    /**
     * Wallet.pay(...) throws if the Invoice is already paid.
     */
    @Test(expected = InvoiceException.AlreadyPaid.class)
    public void complainsIfInvoiceIsAlreadyPaid() {
        final Project project = Mockito.mock(Project.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(true);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(invoice.contract())
            .thenReturn(contract);

        new FakeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);
    }

    /**
     * Wallet.pay(...) throws if the Invoice is not part of the project
     * contract.
     */
    @Test(expected = InvoiceException.NotPartOfProjectContract.class)
    public void complainsIfInvoiceIsNotPartOfProjectContract() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.contract())
            .thenReturn(Mockito.mock(Contract.class));

        new FakeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);
    }


    /**
     * Wallet.pay(...) throws if there are no cash available after deduct
     * Invoice total amount from limit.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfNewCashIsNegative() {
        final Project project = Mockito.mock(Project.class);

        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(invoice.totalAmount())
            .thenReturn(BigDecimal.valueOf(100));

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(invoice.contract())
            .thenReturn(contract);

        new FakeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);
    }

    /**
     * Wallet.pay(...) pays an Invoice.
     * @checkstyle ExecutableStatementCount (60 lines)
     */
    @Test
    public void canPayInvoice() {
        final Storage storage = Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);

        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(project.wallets()).thenReturn(wallets);
        Mockito.when(wallets.updateCash(Mockito.any(), Mockito.any()))
            .thenAnswer(invocation -> new FakeWallet(
                storage,
                project,
                (BigDecimal) invocation.getArguments()[1],
                "id",
                true
            ));

        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(invoice.invoiceId()).thenReturn(1);

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(invoice.contract())
            .thenReturn(contract);

        final Invoices invoices = Mockito.mock(Invoices.class);
        Mockito.when(storage.invoices()).thenReturn(invoices);
        Mockito.when(invoices.registerAsPaid(Mockito.any(Invoice.class)))
            .thenReturn(true);

        final Wallet updated = new FakeWallet(
            storage,
            project,
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);

        MatcherAssert.assertThat(updated.cash(),
            Matchers.equalTo(BigDecimal.ZERO));

        final ArgumentCaptor<Invoice> paidInvoiceCapture = ArgumentCaptor
            .forClass(Invoice.class);
        Mockito.verify(invoices, Mockito.times(1))
            .registerAsPaid(paidInvoiceCapture.capture());
        final Invoice paidInvoice = paidInvoiceCapture.getValue();

        MatcherAssert.assertThat(paidInvoice.isPaid(), Matchers.is(true));
        MatcherAssert.assertThat(paidInvoice.invoiceId(),
            Matchers.is(invoice.invoiceId()));
        MatcherAssert.assertThat(paidInvoice.paymentTime(),
            Matchers.notNullValue());
        MatcherAssert.assertThat(paidInvoice.transactionId(),
            Matchers.notNullValue());
    }


    /**
     * FakeWallet.pay(...) throws WalletException if something went wrong
     * when paying the invoice.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfSomethingWentWrongWhenPayInvoice() {
        final Project project = Mockito.mock(Project.class);

        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(invoice.invoiceId()).thenReturn(1);

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(invoice.contract())
            .thenReturn(contract);

        final Storage storage = Mockito.mock(Storage.class);
        final Invoices invoices = Mockito.mock(Invoices.class);
        Mockito.when(storage.invoices()).thenReturn(invoices);
        Mockito.when(invoices.registerAsPaid(Mockito.any(Invoice.class)))
            .thenReturn(false);

        new FakeWallet(
            storage,
            project,
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);

    }

}
