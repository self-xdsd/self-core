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
import com.selfxdsd.api.exceptions.WalletPaymentException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link StripeWallet}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.27
 * @checkstyle ExecutableStatementCount (1000 lines).
 */
public final class StripeWalletTestCase {

    /**
     * The StripeWallet can return its cash limit.
     */
    @Test
    public void returnsCashLimit() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.cash(),
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );
    }

    /**
     * The StripeWallet can return its identifier.
     */
    @Test
    public void returnsIdentifier() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.identifier(),
            Matchers.equalTo("123StripeID")
        );
    }

    /**
     * The StripeWallet can return its Project.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Storage.class),
            project,
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.project(),
            Matchers.is(project)
        );
    }

    /**
     * The StripeWallet can return its "active" flag.
     */
    @Test
    public void returnsActiveFlag() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.active(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Wallet cash limit can be updated.
     */
    @Test
    public void updatesCash() {
        final Storage storage= Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);
        final Wallet stripe = new StripeWallet(
            storage,
            project,
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        final Wallets wallets = Mockito.mock(Wallets.class);
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(wallets.ofProject(project)).thenReturn(ofProject);
        Mockito.when(storage.wallets()).thenReturn(wallets);
        Mockito.when(ofProject
            .updateCash(Mockito.any(Wallet.class),
                Mockito.any(BigDecimal.class)))
            .then(invocation -> {
                final BigDecimal cash = (BigDecimal) invocation
                    .getArguments()[1];
                return new StripeWallet(storage, project,
                    cash, "123StripeID", Boolean.TRUE);
            });

        final Wallet updated = stripe.updateCash(BigDecimal.valueOf(900));
        MatcherAssert.assertThat(updated.cash(), Matchers
            .equalTo(BigDecimal.valueOf(900)));
    }

    /**
     * Wallet has payment methods.
     */
    @Test
    public void hasPaymentMethods(){
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Wallet stripe = new StripeWallet(
            storage,
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );

        Mockito.when(storage.paymentMethods()).thenReturn(all);
        Mockito.when(all.ofWallet(stripe)).thenReturn(ofWallet);

        MatcherAssert.assertThat(stripe.paymentMethods(),
            Matchers.equalTo(ofWallet));
    }

    /**
     * StripeWallet.billingInfo() complains if the api token is not set.
     */
    @Test (expected = IllegalStateException.class)
    public void billingInfoRequiresToken() {
        new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "identifier",
            Boolean.TRUE
        ).billingInfo();
    }

    /**
     * Wallet.pay(...) throws if the Invoice is already paid.
     */
    @Test(expected = InvoiceException.AlreadyPaid.class)
    public void complainsIfInvoiceIsAlreadyPaid(){
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(
            new Contract.Id(
                "mihai/test",
                "mihai",
                "github",
                "DEV"
            )
        );
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.isPaid()).thenReturn(true);

        new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);
    }

    /**
     * Wallet.pay(...) throws if the Invoice total amount is lower than 108
     * euro.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfInvoiceTotalAmountIsTooLow(){
        final Invoice invoice = Mockito.mock(Invoice.class);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(
            new Contract.Id(
                "mihai/test",
                "mihai",
                "github",
                "DEV"
            )
        );
        Mockito.when(invoice.contract()).thenReturn(contract);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.amount()).thenReturn(BigDecimal.TEN);

        new StripeWallet(
            Mockito.mock(Storage.class),
            Mockito.mock(Project.class),
            BigDecimal.TEN,
            "id",
            true
        ).pay(invoice);
    }

    /**
     * Wallet.pay(...) throws if the is no active payout method.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfThereIsNoActivePayout(){
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.amount()).thenReturn(BigDecimal
            .valueOf(108 * 100));
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal
            .valueOf(108 * 100));

        final Storage storage = Mockito.mock(Storage.class);

        final Contract contract = Mockito.mock(Contract.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(invoice.contract()).thenReturn(contract);

        final PayoutMethods allPayoutsMethods = Mockito
            .mock(PayoutMethods.class);
        final PayoutMethods payoutsOfContrib = Mockito
            .mock(PayoutMethods.class);

        Mockito.when(storage.payoutMethods()).thenReturn(allPayoutsMethods);
        Mockito.when(allPayoutsMethods.ofContributor(contributor))
            .thenReturn(payoutsOfContrib);

        new StripeWallet(
            storage,
            Mockito.mock(Project.class),
            BigDecimal.TEN,
            "id",
            true,
            "stripe_token_123"
        ).pay(invoice);
    }

    /**
     * Wallet.pay(...) throws if the is no active payment method.
     */
    @Test(expected = WalletPaymentException.class)
    public void complainsIfThereIsNoActivePaymentMethod(){
        final Invoice invoice = Mockito.mock(Invoice.class);
        Mockito.when(invoice.isPaid()).thenReturn(false);
        Mockito.when(invoice.amount()).thenReturn(BigDecimal
            .valueOf(108 * 100));
        Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal
            .valueOf(108 * 100));

        final Storage storage = Mockito.mock(Storage.class);

        final Contract contract = Mockito.mock(Contract.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(invoice.contract()).thenReturn(contract);

        final PayoutMethods allPayoutsMethods = Mockito
            .mock(PayoutMethods.class);
        final PayoutMethods payoutsOfContrib = Mockito
            .mock(PayoutMethods.class);
        final PayoutMethod payoutMethod = Mockito.mock(PayoutMethod.class);

        Mockito.when(storage.payoutMethods()).thenReturn(allPayoutsMethods);
        Mockito.when(allPayoutsMethods.ofContributor(contributor))
            .thenReturn(payoutsOfContrib);
        Mockito.when(payoutsOfContrib.active()).thenReturn(payoutMethod);
        Mockito.when(payoutMethod.identifier()).thenReturn("ac_123");

        final Wallet stripe = new StripeWallet(
            storage,
            Mockito.mock(Project.class),
            BigDecimal.TEN,
            "id",
            true,
            "stripe_token_123"
        );
        final PaymentMethods allPaymentMethods = Mockito
            .mock(PaymentMethods.class);
        final PaymentMethods paymentsOfWallet = Mockito
            .mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods())
            .thenReturn(allPaymentMethods);
        Mockito.when(allPaymentMethods.ofWallet(stripe))
            .thenReturn(paymentsOfWallet);

        stripe.pay(invoice);
    }
}
