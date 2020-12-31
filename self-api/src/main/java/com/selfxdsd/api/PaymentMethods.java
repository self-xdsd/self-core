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
package com.selfxdsd.api;

/**
 * Payment methods of a {@link Wallet}.
 * @author criske
 * @version $Id$
 * @since 0.0.26
 */
public interface PaymentMethods extends Iterable<PaymentMethod> {

    /**
     * Register a new PaymentMethod for a Wallet. It will be
     * inactive by default.
     * @param wallet Wallet in question.
     * @param identifier Identifier of the payout method.
     * @return PaymentMethod.
     */
    PaymentMethod register(final Wallet wallet, final String identifier);

    /**
     * Removes a payment method from Wallet.
     * @param paymentMethod PaymentMethod in question.
     * @return True or false if removed.
     */
    boolean remove(final PaymentMethod paymentMethod);

    /**
     * Get a Wallet's PaymentMethods.
     * @param wallet Wallet.
     * @return PayoutMethods.
     */
    PaymentMethods ofWallet(final Wallet wallet);

    /**
     * Get the active PaymentMethod of current Wallet.
     * @return PaymentMethod or null if there isn't any.
     */
    PaymentMethod active();

    /**
     * Activate the given PaymentMethod. All the other payment methods
     * of the same Wallet should be deactivated (only one active
     * PaymentMethod per Wallet is allowed).
     * @param paymentMethod PaymentMethod to be activated.
     * @return Activated payment method.
     */
    PaymentMethod activate(final PaymentMethod paymentMethod);

}
