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
import com.selfxdsd.api.exceptions.InvoiceException;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.stripe.model.SetupIntent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link PreCheckPayments}.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
public final class PreCheckPaymentsTestCase {

    /**
     * PreCheckPayments.pay(...) delegates to original when all pre-check
     * payment conditions are met.
     */
    @Test
    public void paysInvoiceSuccessfully() {
        final Wallet original = Mockito.mock(Wallet.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Project project = Mockito.mock(Project.class);
        final Contract contract = Mockito.mock(Contract.class);

        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal
            .valueOf(200 * 100));
        Mockito.when(original.cash()).thenReturn(BigDecimal
            .valueOf(300 * 100));
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.contract().project()).thenReturn(project);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(original.project()).thenReturn(project);

        new PreCheckPayments(original).pay(invoice);

        Mockito.verify(original, Mockito.times(1)).pay(invoice);
    }

    /**
     * PreCheckPayments.pay(...) throws if the Invoice is not part of project
     * contract.
     */
    @Test(expected = InvoiceException.NotPartOfProjectContract.class)
    public void complainsIfInvoiceIsNotPartOfProjectContract() {
        final Wallet original = Mockito.mock(Wallet.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Contract contract = Mockito.mock(Contract.class);

        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.contract())
            .thenReturn(Mockito.mock(Contract.class));
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(original.project())
            .thenReturn(Mockito.mock(Project.class));

        new PreCheckPayments(original).pay(invoice);
    }

    /**
     * PreCheckPayments.pay(...) throws if the Invoice is already paid.
     */
    @Test(expected = InvoiceException.AlreadyPaid.class)
    public void complainsIfInvoiceIsAlreadyPaid(){
        final Wallet original = Mockito.mock(Wallet.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Project project = Mockito.mock(Project.class);
        final Contract contract = Mockito.mock(Contract.class);

        Mockito.when(invoice.isPaid()).thenReturn(true);
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.contract().project()).thenReturn(project);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(original.project()).thenReturn(project);

        new PreCheckPayments(original).pay(invoice);
    }

    /**
     * PreCheckPayments.pay(...) throws if the Invoice total amount is lower
     * than 108 euro.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfInvoiceTotalAmountIsTooLow(){
        final Wallet original = Mockito.mock(Wallet.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Project project = Mockito.mock(Project.class);
        final Contract contract = Mockito.mock(Contract.class);

        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.contract().project()).thenReturn(project);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(original.project()).thenReturn(project);

        new PreCheckPayments(original).pay(invoice);
    }

    /**
     * PreCheckPayments.pay(...) throws if there are no cash available after
     * deduct Invoice total amount from limit.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfNewCashIsNegative() {
        final Wallet original = Mockito.mock(Wallet.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Project project = Mockito.mock(Project.class);
        final Contract contract = Mockito.mock(Contract.class);

        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(original.cash()).thenReturn(BigDecimal.TEN);
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.contract().project()).thenReturn(project);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(original.project()).thenReturn(project);

        new PreCheckPayments(original).pay(invoice);
    }

    /**
     * Delegates the cash() method to the original.
     */
    @Test
    public void delegatesCash() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.cash()).thenReturn(BigDecimal.TEN);
        MatcherAssert.assertThat(
            new PreCheckPayments(original).cash(),
            Matchers.equalTo(BigDecimal.TEN)
        );
        Mockito.verify(original, Mockito.times(1)).cash();
    }

    /**
     * Delegates the type() method to the original.
     */
    @Test
    public void delegatesType() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.type()).thenReturn("STRIPE");
        MatcherAssert.assertThat(
            new PreCheckPayments(original).type(),
            Matchers.equalTo("STRIPE")
        );
        Mockito.verify(original, Mockito.times(1)).type();
    }

    /**
     * Delegates the active() method to the original.
     */
    @Test
    public void delegatesActive() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.active()).thenReturn(Boolean.TRUE);
        MatcherAssert.assertThat(
            new PreCheckPayments(original).active(),
            Matchers.is(Boolean.TRUE)
        );
        Mockito.verify(original, Mockito.times(1)).active();
    }

    /**
     * Delegates the project() method to the original.
     */
    @Test
    public void delegatesProject() {
        final Project project = Mockito.mock(Project.class);
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.project()).thenReturn(project);
        MatcherAssert.assertThat(
            new PreCheckPayments(original).project(),
            Matchers.is(project)
        );
        Mockito.verify(original, Mockito.times(1)).project();
    }

    /**
     * Delegates the updateCash() method to the original.
     */
    @Test
    public void delegatesUpdateCash() {
        final Wallet updated = Mockito.mock(Wallet.class);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.updateCash(BigDecimal.TEN)).thenReturn(updated);

        MatcherAssert.assertThat(
            new PreCheckPayments(original)
                .updateCash(BigDecimal.TEN),
            Matchers.is(updated)
        );
        Mockito.verify(original, Mockito.times(1)).updateCash(BigDecimal.TEN);
    }

    /**
     * Delegates the paymentMethodSetupIntent() method to the original.
     */
    @Test
    public void delegatesPaymentMethodSetupIntent() {
        final SetupIntent intent = new SetupIntent();
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.paymentMethodSetupIntent()).thenReturn(intent);

        MatcherAssert.assertThat(
            new PreCheckPayments(original)
                .paymentMethodSetupIntent(),
            Matchers.is(intent)
        );
        Mockito.verify(original, Mockito.times(1)).paymentMethodSetupIntent();
    }

    /**
     * Delegates the paymentMethods() method to the original.
     */
    @Test
    public void delegatesPaymentMethods() {
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.paymentMethods()).thenReturn(methods);

        MatcherAssert.assertThat(
            new PreCheckPayments(original).paymentMethods(),
            Matchers.is(methods)
        );
        Mockito.verify(original, Mockito.times(1)).paymentMethods();
    }

    /**
     * Delegates the identifier() method to the original.
     */
    @Test
    public void delegatesIdentifier() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.identifier()).thenReturn("wallet123");
        MatcherAssert.assertThat(
            new PreCheckPayments(original).identifier(),
            Matchers.equalTo("wallet123")
        );
        Mockito.verify(original, Mockito.times(1)).identifier();
    }

    /**
     * Delegates the billingInfo() method to the original.
     */
    @Test
    public void delegatesBillingInfo() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.billingInfo()).thenReturn(info);

        MatcherAssert.assertThat(
            new PreCheckPayments(original).billingInfo(),
            Matchers.is(info)
        );
        Mockito.verify(original, Mockito.times(1)).billingInfo();
    }

    /**
     * Delegates the activate() method to the original.
     */
    @Test
    public void delegatesActivate() {
        final Wallet activated = Mockito.mock(Wallet.class);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.activate()).thenReturn(activated);

        MatcherAssert.assertThat(
            new PreCheckPayments(original).activate(),
            Matchers.is(activated)
        );
        Mockito.verify(original, Mockito.times(1)).activate();
    }

    /**
     * Delegates the remove() method to the original.
     */
    @Test
    public void delegatesRemove() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.remove()).thenReturn(Boolean.TRUE);

        MatcherAssert.assertThat(
            new PreCheckPayments(original).remove(),
            Matchers.is(Boolean.TRUE)
        );
        Mockito.verify(original, Mockito.times(1)).remove();
    }
}