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
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.stripe.model.SetupIntent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link RegisterUnsuccessfulPayments}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class RegisterUnsuccessfulPaymentsTestCase {

    /**
     * An Invoice is paid successfully, no failed or errored payments
     * are registered.
     */
    @Test
    public void paysInvoiceSuccessfully() {
        final Payment successful = Mockito.mock(Payment.class);
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.pay(invoice)).thenReturn(successful);

        MatcherAssert.assertThat(
            new RegisterUnsuccessfulPayments(original).pay(invoice),
            Matchers.is(successful)
        );
        Mockito.verify(invoice, Mockito.times(0)).payments();
    }

    /**
     * When paying the Invoice, WalletPaymentException occurs and a FAILED
     * Payment is registered.
     */
    @Test
    public void walletPaymentExceptionOnPayInvoice() {
        final Payment failed = Mockito.mock(Payment.class);
        Mockito.when(failed.status()).thenReturn(Payment.Status.FAILED);

        final Invoice invoice = Mockito.mock(Invoice.class);
        final Payments ofInvoice = Mockito.mock(Payments.class);
        Mockito.when(
            ofInvoice.register(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any()
            )
        ).thenReturn(failed);
        Mockito.when(invoice.payments()).thenReturn(ofInvoice);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.pay(invoice)).thenThrow(
            new WalletPaymentException("Something went wrong.")
        );

        final Payment payment = new RegisterUnsuccessfulPayments(original)
            .pay(invoice);
        MatcherAssert.assertThat(
            payment,
            Matchers.is(failed)
        );
        MatcherAssert.assertThat(
            payment.status(),
            Matchers.equalTo(Payment.Status.FAILED)
        );
        Mockito.verify(invoice, Mockito.times(1)).payments();
        Mockito.verify(ofInvoice, Mockito.times(1)).register(
            Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any()
        );
    }

    /**
     * When paying the Invoice, IllegalStateException occurs and an ERRORED
     * Payment is registered.
     */
    @Test
    public void illegalStateExceptionOnPayInvoice() {
        final Payment errored = Mockito.mock(Payment.class);
        Mockito.when(errored.status()).thenReturn(Payment.Status.ERROR);

        final Invoice invoice = Mockito.mock(Invoice.class);
        final Payments ofInvoice = Mockito.mock(Payments.class);
        Mockito.when(
            ofInvoice.register(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any()
            )
        ).thenReturn(errored);
        Mockito.when(invoice.payments()).thenReturn(ofInvoice);

        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.pay(invoice)).thenThrow(
            new IllegalStateException("Something went wrong.")
        );

        final Payment payment = new RegisterUnsuccessfulPayments(original)
            .pay(invoice);
        MatcherAssert.assertThat(
            payment,
            Matchers.is(errored)
        );
        MatcherAssert.assertThat(
            payment.status(),
            Matchers.equalTo(Payment.Status.ERROR)
        );
        Mockito.verify(invoice, Mockito.times(1)).payments();
        Mockito.verify(ofInvoice, Mockito.times(1)).register(
            Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any()
        );
    }

    /**
     * Delegates the cash() method to the original.
     */
    @Test
    public void delegatesCash() {
        final Wallet original = Mockito.mock(Wallet.class);
        Mockito.when(original.cash()).thenReturn(BigDecimal.TEN);
        MatcherAssert.assertThat(
            new RegisterUnsuccessfulPayments(original).cash(),
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
            new RegisterUnsuccessfulPayments(original).type(),
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
            new RegisterUnsuccessfulPayments(original).active(),
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
            new RegisterUnsuccessfulPayments(original).project(),
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
            new RegisterUnsuccessfulPayments(original)
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
            new RegisterUnsuccessfulPayments(original)
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
            new RegisterUnsuccessfulPayments(original).paymentMethods(),
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
            new RegisterUnsuccessfulPayments(original).identifier(),
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
            new RegisterUnsuccessfulPayments(original).billingInfo(),
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
            new RegisterUnsuccessfulPayments(original).activate(),
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
            new RegisterUnsuccessfulPayments(original).remove(),
            Matchers.is(Boolean.TRUE)
        );
        Mockito.verify(original, Mockito.times(1)).remove();
    }
}
