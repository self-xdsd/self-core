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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.selfxdsd.api.PayoutMethods;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contributors.ContributorPayoutMethods;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory PayoutMethods.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 * @todo #535:30min Implement methods register of this class,
 *  once we have an implementation of PayoutMethod (we will start
 *  with the Stripe payment method).
 */
public final class InMemoryPayoutMethods implements PayoutMethods {

    /**
     * PayoutMethods "table".
     * @checkstyle LineLength (3 lines)
     */
    private final Map<PayoutMethodKey, PayoutMethod> payoutMethods;

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     *
     * @param storage Parent storage
     */
    public InMemoryPayoutMethods(final Storage storage) {
        this.storage = storage;
        this.payoutMethods = new HashMap<>();
    }

    @Override
    public PayoutMethod register(
        final Contributor contributor,
        final String type,
        final String identifier
    ) {
        return null;
    }

    @Override
    public PayoutMethods ofContributor(final Contributor contributor) {
        final List<PayoutMethod> ofContributor = this.payoutMethods
            .values()
            .stream()
            .filter(
                method -> method.contributor().equals(contributor)
            ).collect(Collectors.toList());
        return new ContributorPayoutMethods(
            contributor,
            ofContributor,
            this.storage
        );
    }

    @Override
    public PayoutMethod active() {
        throw new UnsupportedOperationException(
            "You cannot get the active PayoutMethod "
            + "out of all PayoutMethods in Self. "
            + "Call #ofProject(...) first."
        );
    }

    @Override
    public PayoutMethod activate(final PayoutMethod payoutMethod) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<PayoutMethod> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all PayoutMethods in Self. "
            + "Call #ofContributor(...) first."
        );
    }

    /**
     * PayoutMethod primary key, formed by the contributor's foreign key and
     * the payment method type.
     */
    private static class PayoutMethodKey {

        /**
         * Contributor's username.
         */
        private final String username;

        /**
         * Contributor provider.
         */
        private final String provider;

        /**
         * Wallet's type.
         */
        private final String type;

        /**
         * Constructor.
         *
         * @param username Contributor's username.
         * @param provider Contributor/Project's provider.
         * @param type Wallet type.
         */
        PayoutMethodKey(
            final String username,
            final String provider,
            final String type
        ) {
            this.username = username;
            this.provider = provider;
            this.type = type;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final PayoutMethodKey key =
                (PayoutMethodKey) object;
            return this.username.equals(key.username)
                && this.provider.equals(key.provider)
                && this.type.equals(key.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.username,
                this.provider,
                this.type
            );
        }
    }
}
