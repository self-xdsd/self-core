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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.InvoiceAlreadyPaidExeption;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link com.selfxdsd.api.Wallet.Missing}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.15
 */
public final class MissingWalletTestCase {

    /**
     * The Missing Wallet returns its cash.
     */
    @Test
    public void returnsCash() {
        final Wallet wallet = new Wallet.Missing(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(100_000_000),
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            wallet.cash(),
            Matchers.equalTo(BigDecimal.valueOf(100_000_000))
        );
    }

    /**
     * The Missing Wallet can return its debt.
     */
    @Test
    public void returnsDebt() {
        final List<Contract> list = new ArrayList<>();
        list.add(this.mockContract(BigDecimal.valueOf(1500)));
        list.add(this.mockContract(BigDecimal.valueOf(2000)));
        list.add(this.mockContract(BigDecimal.valueOf(3000)));
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(list.iterator());

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final Wallet wallet = new Wallet.Missing(
            project,
            BigDecimal.valueOf(100_000_000),
            Boolean.TRUE
        );

        MatcherAssert.assertThat(
            wallet.debt(),
            Matchers.equalTo(BigDecimal.valueOf(6500))
        );
    }

    /**
     * The Missing Wallet can return its available cash..
     */
    @Test
    public void returnsAvailableCash() {
        final List<Contract> list = new ArrayList<>();
        list.add(this.mockContract(BigDecimal.valueOf(1500)));
        list.add(this.mockContract(BigDecimal.valueOf(2000)));
        list.add(this.mockContract(BigDecimal.valueOf(3000)));
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(list.iterator());

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final Wallet wallet = new Wallet.Missing(
            project,
            BigDecimal.valueOf(10000),
            Boolean.TRUE
        );

        MatcherAssert.assertThat(
            wallet.available(),
            Matchers.equalTo(BigDecimal.valueOf(3500))
        );
    }


    /**
     * The Missing wallet can pay an Invoice.
     */
    @Test
    public void paysInvoice() {
        final Wallet wallet = new Wallet.Missing(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(100_000_000),
            Boolean.TRUE
        );
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(Boolean.FALSE);

        final Invoice paid = wallet.pay(invoice);

        MatcherAssert.assertThat(paid.isPaid(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(paid.paymentTime(), Matchers.notNullValue());
        MatcherAssert.assertThat(
            paid.transactionId(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.startsWith("fk-")
            )
        );
    }

    /**
     * The Missing wallet should throw an exception if we try to pay
     * an Invoice which is already paid.
     */
    @Test(expected = InvoiceAlreadyPaidExeption.class)
    public void complainsDoublePayment() {
        final Wallet wallet = new Wallet.Missing(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(100_000_000),
            Boolean.TRUE
        );
        final Invoice paid = Mockito.mock(Invoice.class);
        Mockito.when(paid.isPaid()).thenReturn(Boolean.TRUE);
        wallet.pay(paid);
    }

    /**
     * Mock a Contract.
     * @param value Value of the contract.
     * @return Contract.
     */
    private Contract mockContract(final BigDecimal value) {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.value()).thenReturn(value);
        return contract;
    }
}
