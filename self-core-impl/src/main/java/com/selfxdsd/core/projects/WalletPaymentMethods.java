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

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * PaymentMethods for a Wallet.
 * @author criske
 * @version $Id$
 * @since 0.0.28
 */
public final class WalletPaymentMethods implements PaymentMethods {

    /**
     * The wallet in question.
     */
    private final Wallet wallet;

    /**
     * Payment methods for this Wallet.
     */
    private final Supplier<Stream<PaymentMethod>> paymentMethods;

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param wallet The wallet in question.
     * @param paymentMethods Payment methods for this Wallet.
     * @param storage Storage.
     */
    public WalletPaymentMethods(final Wallet wallet,
                                final
                                Supplier<Stream<PaymentMethod>> paymentMethods,
                                final Storage storage) {
        this.wallet = wallet;
        this.paymentMethods = paymentMethods;
        this.storage = storage;
    }

    @Override
    public PaymentMethod register(final Wallet wallet,
                                  final String identifier) {
        if(!this.wallet.equals(wallet)){
            throw new PaymentMethodsException("Can't register this payment "
                + "method because it will be part of different wallet.");
        }
        return this.storage.paymentMethods().register(wallet, identifier);
    }

    @Override
    public boolean remove(final PaymentMethod paymentMethod) {
        if(!paymentMethod.wallet().equals(this.wallet)){
            throw new PaymentMethodsException("Can't remove this payment "
                + "method because it's part of different wallet.");
        }
        return paymentMethod.remove();
    }

    @Override
    public PaymentMethods ofWallet(final Wallet wallet) {
        if(!this.wallet.equals(wallet)){
            throw new PaymentMethodsException("These are payment methods for"
                + wallet.toString() + ", can't call ofWallet() "
                + "for other wallet.");
        }
        return this;
    }

    @Override
    public PaymentMethod active() {
        return this.paymentMethods.get()
            .filter(PaymentMethod::active)
            .findFirst()
            .orElse(null);
    }

    @Override
    public PaymentMethod activate(final PaymentMethod paymentMethod) {
        if(!paymentMethod.wallet().equals(this.wallet)){
            throw new PaymentMethodsException("Can't activate this payment "
                + "method because it's part of different wallet.");
        }
        return this.storage.paymentMethods().activate(paymentMethod);
    }

    @Override
    public Iterator<PaymentMethod> iterator() {
        return this.paymentMethods.get().iterator();
    }
}
