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
package com.selfxdsd.core;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Invoices;
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit tests for {@link StoredPlatformInvoice}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.50
 */
public final class StoredPlatformInvoiceTestCase {

    /**
     * StoredPlatformInvoice can return its id.
     */
    @Test
    public void hasId() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.id(),
            Matchers.equalTo(1)
        );
    }

    /**
     * StoredPlatformInvoice can return its creation time.
     */
    @Test
    public void hasCreationTime() {
        final LocalDateTime createdAt = LocalDateTime.now();
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            createdAt,
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.createdAt(),
            Matchers.equalTo(createdAt)
        );
    }

    /**
     * StoredPlatformInvoice can return its billedTo.
     */
    @Test
    public void hasBilledTo() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedTo(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * StoredPlatformInvoice can return its commission.
     */
    @Test
    public void hasCommission() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.commission(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
    }

    /**
     * StoredPlatformInvoice can return its vat.
     */
    @Test
    public void hasVat() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.vat(),
            Matchers.equalTo(BigDecimal.valueOf(19))
        );
    }

    /**
     * StoredPlatformInvoice can return its transaction ID.
     */
    @Test
    public void hasTransactionId() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.transactionId(),
            Matchers.equalTo("transactionId123")
        );
    }

    /**
     * StoredPlatformInvoice can return its payment time.
     */
    @Test
    public void hasPaymentTime() {
        final LocalDateTime paidAt = LocalDateTime.now();
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            paidAt,
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.paymentTime(),
            Matchers.equalTo(paidAt)
        );
    }

    /**
     * StoredPlatformInvoice can return its corresponding invoice.
     */
    @Test
    public void returnsInvoice() {
        final Invoice found = Mockito.mock(Invoice.class);
        final Invoices all = Mockito.mock(Invoices.class);
        Mockito.when(all.getById(10)).thenReturn(found);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.invoices()).thenReturn(all);

        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            storage
        );
        MatcherAssert.assertThat(
            invoice.invoice(),
            Matchers.is(found)
        );
    }

    /**
     * StoredPlatformInvoice can return its serial number.
     */
    @Test
    public void returnsSerialNumber() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1234,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.serialNumber(),
            Matchers.equalTo("SLF0001234")
        );
    }

    /**
     * StoredPlatformInvoice can return its billedBy.
     */
    @Test
    public void returnsBilledBy() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1234,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedBy(),
            Matchers.equalTo(
                new StringBuilder()
                    .append("SC Extremely Distributed Technologies SRL\n\n")
                    .append("Transilvaniei St. 18, bl. U2, ap. 111\n")
                    .append("Oradea, Romania\n\n")
                    .append("Nr. ORC/Reg. Number: J05/197/2021\n")
                    .append("Cod TVA/VAT Code: RO43621869\n")
                    .append("EUID: ROONRC.J05/197/2021")
                    .toString()
            )
        );
    }

    /**
     * StoredPlatformInvoice can return its total amount.
     */
    @Test
    public void returnsTotalAmount() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1234,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(119))
        );
    }

    /**
     * StoredPlatformInvoice can return its total amount when there is no VAT.
     */
    @Test
    public void returnsTotalAmountNoVat() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1234,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(0),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
    }

    /**
     * StoredPlatformInvoice can return its total amount with reverse (negative)
     * VAT. In this scenario, the VAT should not be added to the total amount.
     */
    @Test
    public void returnsTotalAmountReverseVat() {
        final PlatformInvoice invoice = new StoredPlatformInvoice(
            1234,
            LocalDateTime.now(),
            "mihai",
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(-19),
            "transactionId123",
            LocalDateTime.now(),
            10,
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
    }
}
