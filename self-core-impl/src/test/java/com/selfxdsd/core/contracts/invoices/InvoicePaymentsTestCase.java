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
import com.selfxdsd.api.Payments;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link InvoicePayments}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class InvoicePaymentsTestCase {

    /**
     * Method registerPayment works if the specified Invoice matches.
     */
    @Test
    public void registersPayment() {
        final Payment registered = Mockito.mock(Payment.class);

        final Storage storage = Mockito.mock(Storage.class);
        final Payments all = Mockito.mock(Payments.class);
        Mockito.when(storage.payments()).thenReturn(all);

        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.invoiceId()).thenReturn(1);

        final LocalDateTime timestamp = LocalDateTime.now();
        Mockito.when(
            all.register(
                invoice,
                "transaction123",
                timestamp,
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                ""
            )
        ).thenReturn(registered);

        final Payments payments = new InvoicePayments(
            invoice,
            () -> new ArrayList<Payment>().stream(),
            storage
        );

        MatcherAssert.assertThat(
            payments.register(
                invoice,
                "transaction123",
                timestamp,
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                ""
            ),
            Matchers.is(registered)
        );
        Mockito.verify(storage, Mockito.times(1)).payments();
        Mockito.verify(all, Mockito.times(1)).register(
            invoice,
            "transaction123",
            timestamp,
            BigDecimal.TEN,
            Payment.Status.SUCCESSFUL,
            ""
        );
    }

    /**
     * Method register throws ISE if we try to register a Payment for another
     * Invoice.
     */
    @Test
    public void registerComplainsOnDifferentInvoice() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.invoiceId()).thenReturn(1);
        final Invoice other = Mockito.mock(Invoice.class);
        Mockito.when(other.invoiceId()).thenReturn(2);

        final Payments payments = new InvoicePayments(
            invoice,
            () -> new ArrayList<Payment>().stream(),
            Mockito.mock(Storage.class)
        );

        try {
            payments.register(
                other,
                "transaction123",
                LocalDateTime.now(),
                BigDecimal.TEN,
                Payment.Status.SUCCESSFUL,
                ""
            );
            Assert.fail("IllegalStateException was expected!");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                "These are the Payments of Invoice 1. "
                    + "You cannot register a Payment for another Invoice here."
                )
            );
        }
    }

    /**
     * Method ofInvoice(Invoice) returns self if the given Invoice
     * is the same.
     */
    @Test
    public void ofInvoiceReturnsSelfIfSame() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.invoiceId()).thenReturn(1);
        final Payments payments = new InvoicePayments(
            invoice,
            () -> new ArrayList<Payment>().stream(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            payments.ofInvoice(invoice),
            Matchers.is(payments)
        );
    }

    /**
     * Method ofInvoice(Invoice) throws ISE if we try to query the
     * payments of another Invoice.
     */
    @Test
    public void ofInvoiceComplainsOnDifferentInvoice() {
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.invoiceId()).thenReturn(1);
        final Invoice other = Mockito.mock(Invoice.class);
        Mockito.when(other.invoiceId()).thenReturn(2);
        final Payments payments = new InvoicePayments(
            invoice,
            () -> new ArrayList<Payment>().stream(),
            Mockito.mock(Storage.class)
        );
        try {
            payments.ofInvoice(other);
            Assert.fail("IllegalStateException was expected!");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                    "Already seeing the Payments of Invoice 1. "
                    + "You cannot see the Payments of another Invoice here."
                )
            );
        }
    }

    /**
     * InvoicePayments can be iterated.
     */
    @Test
    public void canBeIterated() {
        final List<Payment> payments = List.of(
            Mockito.mock(Payment.class),
            Mockito.mock(Payment.class),
            Mockito.mock(Payment.class)
        );
        MatcherAssert.assertThat(
            new InvoicePayments(
                Mockito.mock(Invoice.class),
                () -> payments.stream(),
                Mockito.mock(Storage.class)
            ),
            Matchers.iterableWithSize(3)
        );
    }

}
