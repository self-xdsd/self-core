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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;

import java.util.Objects;

/**
 * A Contributor stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #192:30min Method tasks() here should return the tasks with
 *  respect ot the encapsulated Contracts. If the Contracts exist (!= null),
 *  then the method should only return the Tasks from these contracts.
 *  Otherwise, it should return all the Tasks of the contributor, as it
 *  does now.
 */
public final class StoredContributor implements Contributor {

    /**
     * Username.
     */
    private final String username;

    /**
     * Provider.
     */
    private final String provider;

    /**
     * This contributor's Contracts. If they are missing,
     * they will be read from the storage.
     */
    private final Contracts contracts;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param username Username.
     * @param provider Provider.
     * @param storage Storage.
     */
    public StoredContributor(
        final String username,
        final String provider,
        final Storage storage
    ) {
        this(username, provider, null, storage);
    }

    /**
     * Constructor. Use this when you want to load
     * the Contributor's Contracts eagerly.
     * @param username Username.
     * @param provider Provider.
     * @param contracts Contributor's Contracts.
     * @param storage Storage.
     */
    public StoredContributor(
        final String username,
        final String provider,
        final Contracts contracts,
        final Storage storage
    ) {
        this.username = username;
        this.provider = provider;
        this.contracts = contracts;
        this.storage = storage;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String provider() {
        return this.provider;
    }

    @Override
    public Contracts contracts() {
        final Contracts assigned;
        if(this.contracts == null) {
            assigned = this.storage.contracts().ofContributor(this);
        } else {
            assigned = this.contracts;
        }
        return assigned;
    }

    @Override
    public PayoutMethods payoutMethods() {
        return this.storage.payoutMethods().ofContributor(this);
    }

    @Override
    public Contract contract(
        final String repoFullName,
        final String provider,
        final String role
    ) {
        final Contract.Id searched = new Contract.Id(
            repoFullName,
            this.username,
            provider,
            role
        );
        Contract found = null;
        for(final Contract contract : this.contracts()) {
            final Contract.Id contractId = contract.contractId();
            if(contractId.equals(searched)) {
                found = contract;
                break;
            }
        }
        return found;
    }

    @Override
    public Tasks tasks() {
        return this.storage.tasks().ofContributor(this.username, this.provider);
    }

    @Override
    public PayoutMethod createStripeAccount() {
        final PayoutMethods methods = this.payoutMethods();
        for(final PayoutMethod method : methods) {
            if(method.type().equals(PayoutMethod.Type.STRIPE)) {
                throw new IllegalStateException(
                    "Contributor " + this.username + " at " + this.provider
                    + " already has a Stripe Connect Account."
                );
            }
        }
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the self_stripe_token Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final Account account = Account.create(
                AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .build()
            );
            return methods.register(
                this,
                PayoutMethod.Type.STRIPE,
                account.getId()
            );
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "Stripe threw an exception when trying to create SCA for "
                + "Contributor " + this.username + "/" + this.provider + ". ",
                ex
            );
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.provider);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Contributor)) {
            return false;
        }
        final Contributor other = (Contributor) obj;
        return this.username.equals(other.username())
            && this.provider.equals(other.provider());
    }
}
