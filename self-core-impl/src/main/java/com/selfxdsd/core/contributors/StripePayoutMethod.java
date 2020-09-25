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

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

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
     * Is this PayoutMethod active or not?
     */
    private final boolean active;

    /**
     * Ctor.
     * @param contributor Contributor owner.
     * @param identifier Identifier.
     * @param active Active or not active.
     */
    public StripePayoutMethod(
        final Contributor contributor,
        final String identifier,
        final boolean active
    ) {
        this.contributor = contributor;
        this.identifier = identifier;
        this.active = active;
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
    public boolean active() {
        return this.active;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public JsonObject json() {
        final String apiToken = System.getenv("self_stripe_token");
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the stripe.api.token Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final Account account = Account.retrieve(this.identifier);
            return Json.createReader(
                new StringReader(
                    account.getRawJsonObject().toString()
                )
            ).readObject();
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "Stripe threw an exception when trying to fetch the "
                + "Stripe Connect Account of Contributor "
                + this.contributor.username() + "/"
                + this.contributor.provider() + ". ",
                ex
            );
        }
    }
}
