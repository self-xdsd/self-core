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

import javax.json.JsonObject;

/**
 * A Contributor's payout method, how Self is going to
 * send them money.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 */
public interface PayoutMethod {

    /**
     * Contributor to whom this payout method belongs.
     * @return Contributor.
     */
    Contributor contributor();

    /**
     * Type of this payout method.
     * @return String.
     */
    String type();

    /**
     * Identifier.
     * @return String.
     */
    String identifier();

    /**
     * BillingInfo associated with this Payout Method (data from the
     * Stirpe Connected Account).
     * @return BillingInfo.
     */
    BillingInfo billingInfo();

    /**
     * Can this PayoutMethod be used to receive payments? Even if it is created,
     * it might not be usable yet (the Contributor still needs to provide some
     * data to Stripe).
     * @return True of false.
     */
    boolean canReceivePayments();

    /**
     * The whole PayoutMethod in JSON.
     * This usually comes from the API of the payment processor.
     * @return The PayoutMethod in JSON format.
     */
    JsonObject json();

    /**
     * Remove this PayoutMethod.
     * @return True if succeeded, false otherwise.
     */
    boolean remove();

    /**
     * Possible payout methods.
     */
    class Type {

        /**
         * Hidden ctor.
         */
        private Type(){ }

        /**
         * Stripe.
         */
        public static final String STRIPE = "STRIPE";

        /**
         * Paypal.
         */
        public static final String PAYPAL = "PayPal";
    }
}
