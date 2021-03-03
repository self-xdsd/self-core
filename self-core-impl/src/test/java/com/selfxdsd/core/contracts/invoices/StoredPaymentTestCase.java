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
import com.selfxdsd.api.Payment;
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.PlatformInvoices;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link StoredPayment}.
 * @author Ali FELLAHI fellahi.ali@gmail.com
 * @version $Id$
 * @since 0.0.67
 */
public final class StoredPaymentTestCase {

    /**
     * Should return an Invoice.
     */
    @Test
    public void hasInvoice() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        MatcherAssert.assertThat(
            new StoredPayment(
                invoice,
                "123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                Mockito.mock(Storage.class)
            ).invoice(),
            Matchers.is(invoice)
        );
    }

    /**
     * Should return a PlatformInvoice if the Payment is successful.
     */
    @Test
    public void returnsPlatformInvoice() {
        final Storage storage = Mockito.mock(Storage.class);
        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        Mockito.when(storage.platformInvoices()).thenReturn(all);
        final PlatformInvoice found = Mockito.mock(PlatformInvoice.class);
        final LocalDateTime paymentTime = LocalDateTime.now();
        Mockito.when(all.getByPayment("123", paymentTime))
            .thenReturn(found);
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                paymentTime,
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                storage
            ).platformInvoice(),
            Matchers.is(found)
        );
    }

    /**
     * Should return null if the Payment is failed.
     */
    @Test
    public void nullPlatformInvoiceForFailedPayment() {
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.FAILED,
                "",
                Mockito.mock(Storage.class)
            ).platformInvoice(),
            Matchers.nullValue()
        );
    }

    /**
     * Should return the transaction id.
     */
    @Test
    public void hasTransactionId() {
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                Mockito.mock(Storage.class)
            ).transactionId(),
            Matchers.is("123")
        );
    }

    /**
     * Return payment time.
     */
    @Test
    public void hasPaymentTime() {
        final LocalDateTime paymentTime = LocalDateTime.now();
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                paymentTime,
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                Mockito.mock(Storage.class)
            ).paymentTime(),
            Matchers.is(paymentTime)
        );
    }

    /**
     * Return payment value.
     */
    @Test
    public void hasValue() {
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                Mockito.mock(Storage.class)
            ).value(),
            Matchers.is(BigDecimal.TEN)
        );
    }

    /**
     * Return payment status.
     */
    @Test
    public void hasStatus() {
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                "",
                Mockito.mock(Storage.class)
            ).status(),
            Matchers.is(Payment.Status.SUCCESSFUL)
        );
    }

    /**
     * Return failure reason.
     */
    @Test
    public void failReason() {
        MatcherAssert.assertThat(
            new StoredPayment(
                Mockito.mock(Invoice.class),
                "",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.FAILED,
                "no funds",
                Mockito.mock(Storage.class)
            ).failReason(),
            Matchers.is("no funds")
        );
    }

    /**
     * Positive equality.
     */
    @Test
    public void areEquals() {
        Invoice invoice = Mockito.mock(Invoice.class);
        LocalDateTime paymentTime = LocalDateTime.now();
        final Payment first = new StoredPayment(
            invoice,
            "123",
            paymentTime,
            BigDecimal.TEN,
            Payment.Status.SUCCESSFUL,
            "",
            Mockito.mock(Storage.class)
        );

        final Payment second = new StoredPayment(
            invoice,
            "123",
            paymentTime,
            BigDecimal.TEN,
            Payment.Status.SUCCESSFUL,
            "",
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            first,
            Matchers.equalTo(second)
        );

        MatcherAssert.assertThat(
            first.hashCode(),
            Matchers.equalTo(second.hashCode())
        );
    }

    /**
     * Negative equality.
     */
    @Test
    public void notEquals() {
        final Payment first = new StoredPayment(
            Mockito.mock(Invoice.class),
            "123",
            LocalDateTime.MAX,
            BigDecimal.TEN,
            Payment.Status.SUCCESSFUL,
            "",
            Mockito.mock(Storage.class)
        );

        final Payment second = new StoredPayment(
            Mockito.mock(Invoice.class),
            "123",
            LocalDateTime.MIN,
            BigDecimal.TEN,
            Payment.Status.SUCCESSFUL,
            "",
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            first,
            Matchers.not(second)
        );

        MatcherAssert.assertThat(
            first.hashCode(),
            Matchers.not(second.hashCode())
        );
    }
}
