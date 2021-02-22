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

import com.selfxdsd.api.PaymentMethod;
import com.selfxdsd.api.PaymentMethods;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.exceptions.PaymentMethodsException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Unit tests for {@link WalletPaymentMethods}.
 * @author criske
 * @version $Id$
 * @since 0.0.28
 */
public final class WalletPaymentMethodsTestCase {

    /**
     * WalletPaymentMethods can be iterated.
     */
    @Test
    public void iterationWorks() {
        final Storage storage = Mockito.mock(Storage.class);
        final Wallet wallet = Mockito.mock(Wallet.class);
        final Iterable<PaymentMethod> pms = () -> new WalletPaymentMethods(
            wallet,
            () -> Stream.of(
                Mockito.mock(PaymentMethod.class),
                Mockito.mock(PaymentMethod.class),
                Mockito.mock(PaymentMethod.class)
            ),
            storage
        ).iterator();

        MatcherAssert.assertThat(pms, Matchers.iterableWithSize(3));
    }

    /**
     * WalletPaymentMethods can register a PaymentMethod.
     */
    @Test
    public void canRegisterPaymentMethod() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            Stream::empty,
            storage
        );

        pms.register(wallet, "pm_1");

        Mockito.verify(all).register(wallet, "pm_1");
    }

    /**
     * WalletPaymentMethods.register(...) throws if register PaymentMethod for
     * other wallet.
     */
    @Test(expected = PaymentMethodsException.class)
    public void complainsIfRegisterPaymentMethodForOtherWallet() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            Stream::empty,
            storage
        );

        pms.register(Mockito.mock(Wallet.class), "pm_1");
    }

    /**
     * WalletPaymentMethods.remove(...) throws if register PaymentMethod is from
     * other wallet.
     */
    @Test
    public void canRemovePaymentMethod() {
        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(wallet);
        Mockito.when(pmeth.remove()).thenReturn(true);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            () -> Stream.of(pmeth),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            pms.remove(pmeth),
            Matchers.is(Boolean.TRUE)
        );

        Mockito.verify(pmeth, Mockito.times(1)).remove();
    }

    /**
     * WalletPaymentMethods can remove a PaymentMethod.
     */
    @Test(expected = PaymentMethodsException.class)
    public void complainsIfRemovePaymentMethodFromOtherWallet() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(wallet);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            () -> Stream.of(pmeth),
            storage
        );
        final PaymentMethod otherPmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(otherPmeth.wallet())
            .thenReturn(Mockito.mock(Wallet.class));

        pms.remove(otherPmeth);
    }

    /**
     * Returns itself when calling ofWallet(...).
     */
    @Test
    public void returnsItself() {
        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(pms.ofWallet(wallet), Matchers.equalTo(pms));
    }

    /**
     * Throws when calling ofWallet(...) for other Wallet.
     */
    @Test(expected = PaymentMethodsException.class)
    public void complainsIfFilteringForOtherWallet() {
        final PaymentMethods pms = new WalletPaymentMethods(
            Mockito.mock(Wallet.class),
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        pms.ofWallet(Mockito.mock(Wallet.class));
    }

    /**
     * Returns active PaymentMethod.
     */
    @Test
    public void returnsActivePaymentMethod() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final PaymentMethod active = Mockito.mock(PaymentMethod.class);
        Mockito.when(active.active()).thenReturn(true);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            () -> Stream.of(
                Mockito.mock(PaymentMethod.class),
                Mockito.mock(PaymentMethod.class),
                Mockito.mock(PaymentMethod.class),
                active
            ),
            storage
        );

        MatcherAssert.assertThat(pms.active(), Matchers.equalTo(active));
    }

    /**
     * Can activate a PaymentMethod.
     */
    @Test
    public void canActivatePaymentMethod() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(wallet);

        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            () -> Stream.of(pmeth),
            storage
        );

        pms.activate(pmeth);

        Mockito.verify(all).activate(pmeth);
    }

    /**
     * Complains when activate PaymentMethod if is attached to other Wallet.
     */
    @Test(expected = PaymentMethodsException.class)
    public void complainsWhenActivatePaymentMethodIfAttachedToOtherWallet() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(Mockito.mock(Wallet.class));

        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            Stream::empty,
            storage
        );

        pms.activate(pmeth);
    }

    /**
     * Can deactivate a PaymentMethod.
     */
    @Test
    public void canDeactivatePaymentMethod() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(wallet);

        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            () -> Stream.of(pmeth),
            storage
        );

        pms.deactivate(pmeth);

        Mockito.verify(all).deactivate(pmeth);
    }

    /**
     * Complains when deactivate PaymentMethod if is attached to other Wallet.
     */
    @Test(expected = PaymentMethodsException.class)
    public void complainsWhenDeactivatePaymentMethodIfAttachedToOtherWallet() {
        final Storage storage = Mockito.mock(Storage.class);
        final PaymentMethods all = Mockito.mock(PaymentMethods.class);
        Mockito.when(storage.paymentMethods()).thenReturn(all);

        final Wallet wallet = Mockito.mock(Wallet.class);
        final PaymentMethod pmeth = Mockito.mock(PaymentMethod.class);
        Mockito.when(pmeth.wallet()).thenReturn(Mockito.mock(Wallet.class));

        final PaymentMethods pms = new WalletPaymentMethods(
            wallet,
            Stream::empty,
            storage
        );

        pms.deactivate(pmeth);
    }
}
