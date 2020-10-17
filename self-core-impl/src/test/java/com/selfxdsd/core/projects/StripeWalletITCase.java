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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.RandomPort;
import com.stripe.Stripe;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;

/**
 * Integration tests for {@link StripeWallet} payment.
 * @author criske
 * @version $Id$
 * @since 0.0.28
 * @checkstyle ExecutableStatementCount (1000 lines).
 */
public final class StripeWalletITCase {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final RandomPort resource = new RandomPort();

    /**
     * The StripeWallet successfully paying the Invoice.
     * @throws IOException if something went wrong.
     */
    @Test
    public void payingInvoiceOk() throws IOException {
        try(
            final MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("object", "transfer")
                            .add("id", "tr_1HcR1u2eZvKYlo2CymGXCe0A")
                            .add("created", 1545045758)
                            .add("source_transaction",
                                "ch_1HcR1u2eZvKYlo2CpdCWZnIK")
                            .build()
                            .toString()
                    )
            ).start(this.resource.port())
        ) {
            Stripe.overrideUploadBase(container.home().toString());
            Stripe.overrideApiBase(container.home().toString());
            final LocalDateTime now = LocalDateTime.now();
            final Invoice invoice = Mockito.mock(Invoice.class);
            Mockito.when(invoice.invoiceId()).thenReturn(1);
            Mockito.when(invoice.createdAt()).thenReturn(now);
            Mockito.when(invoice.isPaid()).thenReturn(false);
            Mockito.when(invoice.totalAmount()).thenReturn(BigDecimal.TEN);

            final Contract contract = Mockito.mock(Contract.class);
            final Contributor contributor = Mockito.mock(Contributor.class);
            Mockito.when(contract.contributor()).thenReturn(contributor);
            Mockito.when(invoice.contract()).thenReturn(contract);

            final Storage storage = Mockito.mock(Storage.class);
            final PayoutMethods allPayoutsMethods = Mockito
                .mock(PayoutMethods.class);
            final PayoutMethods payoutsOfContrib = Mockito
                .mock(PayoutMethods.class);
            final PayoutMethod payoutMethod = Mockito.mock(PayoutMethod.class);
            final PaymentMethods allPaymentMethods = Mockito
                .mock(PaymentMethods.class);
            final PaymentMethods paymentsOfWallet = Mockito
                .mock(PaymentMethods.class);
            final PaymentMethod paymentMethod = Mockito
                .mock(PaymentMethod.class);

            final Invoices invoices = Mockito.mock(Invoices.class);
            Mockito.when(storage.invoices()).thenReturn(invoices);

            final Project project = Mockito.mock(Project.class);
            final Wallets allWallets = Mockito.mock(Wallets.class);
            final Wallets ofProject = Mockito.mock(Wallets.class);

            final Wallet stripe = new StripeWallet(
                storage,
                project,
                BigDecimal.valueOf(1000),
                "123StripeID",
                Boolean.TRUE,
                "stripe_24343"
            );

            Mockito.when(storage.payoutMethods()).thenReturn(allPayoutsMethods);
            Mockito.when(allPayoutsMethods.ofContributor(contributor))
                .thenReturn(payoutsOfContrib);
            Mockito.when(payoutsOfContrib.active()).thenReturn(payoutMethod);
            Mockito.when(payoutMethod.identifier()).thenReturn("ac_123");

            Mockito.when(storage.paymentMethods())
                .thenReturn(allPaymentMethods);
            Mockito.when(allPaymentMethods.ofWallet(stripe))
                .thenReturn(paymentsOfWallet);
            Mockito.when(paymentsOfWallet.active()).thenReturn(paymentMethod);
            Mockito.when(paymentMethod.identifier()).thenReturn("pm_123");

            Mockito.when(storage.wallets()).thenReturn(allWallets);
            Mockito.when(allWallets.ofProject(project)).thenReturn(ofProject);

            stripe.pay(invoice);

            Mockito.verify(invoices, Mockito.times(1))
                .registerAsPaid(Mockito.any(Invoice.class));
            Mockito.verify(ofProject, Mockito.times(1))
                .updateCash(stripe, BigDecimal.valueOf(990));
        }
    }

}
