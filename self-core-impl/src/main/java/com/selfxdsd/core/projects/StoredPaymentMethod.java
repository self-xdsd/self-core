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
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.storage.Storage;

import java.util.Objects;

/**
 * A Wallet PaymentMethod stored in Self.
 * @author criske
 * @version $Id$
 * @since 0.0.26
 */
public abstract class StoredPaymentMethod implements PaymentMethod {

    /**
     * Self storage.
     */
    private final Storage storage;

    /**
     * Identifier.
     */
    private final String identifier;

    /**
     * Wallet.
     */
    private final Wallet wallet;


    /**
     * Is the payment method active or not.
     */
    private final boolean active;

    /**
     * Ctor.
     * @param storage Storage.
     * @param identifier Identifier.
     * @param wallet Wallet.
     * @param active Is the payment method active or not.
     */
    public StoredPaymentMethod(final Storage storage,
                               final String identifier,
                               final Wallet wallet,
                               final boolean active) {
        this.storage = storage;
        this.identifier = identifier;
        this.wallet = wallet;
        this.active = active;
    }

    @Override
    public final String identifier() {
        return this.identifier;
    }

    @Override
    public final Wallet wallet() {
        return this.wallet;
    }

    @Override
    public final boolean active() {
        return this.active;
    }

    @Override
    public final PaymentMethod activate() {
        return this.storage.paymentMethods().ofWallet(wallet)
            .activate(this);
    }

    @Override
    public final PaymentMethod deactivate() {
        return this.storage.paymentMethods().deactivate(this);
    }

    @Override
    public final boolean equals(final Object obj) {
        if(this == obj){
            return true;
        }
        if (!(obj instanceof PaymentMethod)) {
            return false;
        }
        final PaymentMethod other = (PaymentMethod) obj;
        return this.identifier.equals(other.identifier())
            && this.wallet.equals(other.wallet());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.identifier, this.wallet);
    }
}
