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

import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodListParams;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Stripe payment method.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.35
 */
public final class StripePaymentMethod extends StoredPaymentMethod {

    /**
     * Ctor.
     * @param storage Storage.
     * @param identifier Identifier.
     * @param wallet Wallet.
     * @param active Is the payment method active or not.
     */
    public StripePaymentMethod(
        final Storage storage,
        final String identifier,
        final Wallet wallet,
        final boolean active
    ) {
        super(storage, identifier, wallet, active);
    }

    @Override
    public JsonObject json() {
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final Iterable<PaymentMethod> paymentMethods = PaymentMethod.list(
                PaymentMethodListParams.builder()
                    .setCustomer(this.wallet().identifier())
                    .setType(PaymentMethodListParams.Type.CARD)
                    .build()
            ).autoPagingIterable();
            for(final PaymentMethod method : paymentMethods) {
                if(this.identifier().equalsIgnoreCase(method.getId())){
                    return Json.createReader(
                        new StringReader(
                            method.toJson()
                        )
                    ).readObject();
                }
            }
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "StripeException when trying to fetch PaymentMethod "
                + this.identifier() + " of Customer/Wallet "
                + this.wallet().identifier(),
                ex
            );
        }
        throw new IllegalStateException(
            "PaymentMethod " + this.identifier() + " of Customer/Wallet "
            + this.wallet().identifier() + " not found in Stripe!"
        );
    }
}
