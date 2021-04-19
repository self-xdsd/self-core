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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.BillingInfo;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.selfxdsd.core.projects.AccountBillingInfo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A Contributor's Stripe PayoutMethod.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 */
public final class StripePayoutMethod implements PayoutMethod {

    /**
     * Contributor who has this PayoutMethod.
     */
    private final Contributor contributor;

    /**
     * Identifier.
     */
    private final String identifier;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Stripe Connected Account. Make sure to read it from the API only once
     * and cache the result.
     * @todo #1097:60min Initialize this Supplier in the Constructor, so we
     *  can write proper unit tests for the methods which are using it.
     */
    private final Supplier<Account> connectedAccount = new Supplier<>() {

        /**
         * Cached account.
         */
        private Account account;

        @Override
        public Account get() {
            if(this.account == null) {
                final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
                if(apiToken == null || apiToken.trim().isEmpty()) {
                    throw new IllegalStateException(
                        "[StripePayoutMethod] Please specify the "
                        + Env.STRIPE_API_TOKEN
                        + " Environment Variable!"
                    );
                }
                Stripe.apiKey = apiToken;
                try {
                    this.account = Account.retrieve(
                        StripePayoutMethod.this.identifier
                    );
                } catch (final StripeException ex) {
                    throw new IllegalStateException(
                        "Stripe threw an exception when trying to fetch the "
                        + "Stripe Connect Account of Contributor "
                        + StripePayoutMethod.this.contributor.username() + "/"
                        + StripePayoutMethod.this.contributor.provider() + ". ",
                        ex
                    );
                }
            }
            return this.account;
        }
    };

    /**
     * Ctor.
     * @param contributor Contributor owner.
     * @param identifier Identifier.
     * @param storage Storage.
     */
    public StripePayoutMethod(
        final Contributor contributor,
        final String identifier,
        final Storage storage
    ) {
        this.contributor = contributor;
        this.identifier = identifier;
        this.storage = storage;
    }

    @Override
    public Contributor contributor() {
        return this.contributor;
    }

    @Override
    public String type() {
        return Type.STRIPE;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public BillingInfo billingInfo() {
        return new AccountBillingInfo(this.connectedAccount.get());
    }

    @Override
    public boolean canReceivePayments() {
        final boolean canReceivePayments;
        final Account account = this.connectedAccount.get();
        final Account.Capabilities capabilities = account.getCapabilities();
        if(capabilities != null) {
            final String transfers = capabilities.getTransfers();
            final String cardPayments = capabilities.getCardPayments();
            if("active".equalsIgnoreCase(transfers)
                && "active".equalsIgnoreCase(cardPayments)) {
                canReceivePayments = true;
            } else {
                canReceivePayments = false;
            }
        } else {
            canReceivePayments = false;
        }
        return canReceivePayments;
    }

    @Override
    public JsonObject json() {
        return Json.createReader(
            new StringReader(
                this.connectedAccount.get().getRawJsonObject().toString()
            )
        ).readObject();
    }

    @Override
    public boolean remove() {
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "[REMOVE_PAYOUT_METHOD] Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            Account.retrieve(this.identifier).delete();
            return this.storage.payoutMethods().remove(this);
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "Stripe threw an exception when trying to delete the "
                + "Stripe Connect Account of Contributor "
                + this.contributor.username() + "/"
                + this.contributor.provider() + ". ",
                ex
            );
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.contributor, this.identifier);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PayoutMethod)) {
            return false;
        }
        final PayoutMethod other = (PayoutMethod) obj;
        return this.contributor.equals(other.contributor())
            && this.identifier.equalsIgnoreCase(other.identifier());
    }
}
