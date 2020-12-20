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
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.stripe.model.SetupIntent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Unit tests for {@link StripePaymentMethod}.
 * @author criske
 * @version $Id$
 * @since 0.0.26
 */
public final class StripePaymentMethodTestCase {


    /**
     * StoredPaymentMethod has identifier, Wallet and active flag.
     */
    @Test
    public void hasIdentifierWalletAndActiveFlag(){
        final Wallet wallet = this.mockWallet(
            Wallet.Type.STRIPE,
            "john/test",
            Provider.Names.GITHUB
        );
        final PaymentMethod paymentMethod = new StripePaymentMethod(
            Mockito.mock(Storage.class),
            "fake_id_132",
            wallet,
            true);
        MatcherAssert.assertThat(paymentMethod.identifier(),
            Matchers.equalTo("fake_id_132"));
        MatcherAssert.assertThat(paymentMethod.wallet(),
            Matchers.equalTo(wallet));
        MatcherAssert.assertThat(paymentMethod.active(),
            Matchers.equalTo(Boolean.TRUE));
    }

    /**
     * StoredPaymentMethod can be activated.
     */
    @Test
    public void canBeActivated(){
        final Wallet wallet = this.mockWallet(
            Wallet.Type.STRIPE,
            "john/test",
            Provider.Names.GITHUB
        );
        final Storage storage = Mockito.mock(Storage.class);

        final PaymentMethods paymentMethods = Mockito
            .mock(PaymentMethods.class);
        final PaymentMethods ofWallet = Mockito
            .mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(paymentMethods);
        Mockito.when(paymentMethods.ofWallet(wallet)).thenReturn(ofWallet);

        final PaymentMethod paymentMethod = new StripePaymentMethod(
            storage,
            "fake_id_132",
            wallet,
            true);

        Mockito.when(ofWallet.activate(paymentMethod))
            .thenReturn(paymentMethod);

        final PaymentMethod activated = paymentMethod.activate();
        MatcherAssert.assertThat(activated, Matchers.equalTo(paymentMethod));
        MatcherAssert.assertThat(activated.hashCode(),
            Matchers.equalTo(paymentMethod.hashCode()));
    }

    /**
     * StoredPaymentMethod respects equals and hash code contracts.
     */
    @Test
    public void respectsEqualsAndHashCodeContracts(){
        final PaymentMethod paymentMethod = new StripePaymentMethod(
            Mockito.mock(Storage.class),
            "fake_id_132",
            this.mockWallet(
                Wallet.Type.STRIPE,
                "john/test",
                Provider.Names.GITHUB
            ),
            true);
        final PaymentMethod paymentMethodSame = new StripePaymentMethod(
            Mockito.mock(Storage.class),
            "fake_id_132",
            this.mockWallet(
                Wallet.Type.STRIPE,
                "john/test",
                Provider.Names.GITHUB
            ),
            false);

        MatcherAssert.assertThat(paymentMethod
            .equals(paymentMethodSame), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(paymentMethod
            .equals(paymentMethod), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(paymentMethod.hashCode(),
            Matchers.equalTo(paymentMethodSame.hashCode()));
        MatcherAssert.assertThat(paymentMethod
            .equals(new Object()), Matchers.is(Boolean.FALSE));
    }

    /**
     * Mocks a Wallet.
     * @param type Type
     * @param repoFullName Repo full name.
     * @param provider Provider.
     * @return Mocked Wallet.
     */
    private Wallet mockWallet(
        final String type,
        final String repoFullName,
        final String provider
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(repoFullName);
        Mockito.when(project.provider()).thenReturn(provider);
        return new Wallet() {

            @Override
            public BigDecimal cash() {
                return BigDecimal.ZERO;
            }

            @Override
            public Wallet pay(final Invoice invoice) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String type() {
                return type;
            }

            @Override
            public boolean active() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Project project() {
                return project;
            }

            @Override
            public Wallet updateCash(final BigDecimal cash) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SetupIntent paymentMethodSetupIntent() {
                throw new UnsupportedOperationException(
                    "This operation is available only for Stripe wallets!"
                );
            }

            @Override
            public PaymentMethods paymentMethods() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String identifier() {
                return "mockWalletIdentifier";
            }

            @Override
            public BillingInfo billingInfo() {
                return Mockito.mock(BillingInfo.class);
            }

            @Override
            public int hashCode() {
                return Objects.hash(type, repoFullName, provider);
            }

            @Override
            public boolean equals(final Object obj) {
                if(!(obj instanceof Wallet)){
                    return false;
                }
                final Wallet other = (Wallet) obj;
                return this.toString().equals(other.toString());
            }

            @Override
            public String toString() {
                return type + repoFullName + provider;
            }
        };
    }

    /**
     * StripePaymentMethod.json() should throw an ISE
     * if the stripe.api.token env variable is not set.
     */
    @Test
    public void jsonComplainsOnMissingApiKey() {
        final PaymentMethod paymentMethod = new StripePaymentMethod(
            Mockito.mock(Storage.class),
            "fake_id_132",
            this.mockWallet(
                Wallet.Type.STRIPE,
                "john/test",
                Provider.Names.GITHUB
            ),
            true
        );
        try {
            paymentMethod.json();
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                "Please specify the "
                    + Env.STRIPE_API_TOKEN
                    + " Environment Variable!"
                )
            );
        }
    }
}
