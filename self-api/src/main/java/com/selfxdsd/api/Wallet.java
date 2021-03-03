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

import com.stripe.model.SetupIntent;

import java.math.BigDecimal;

/**
 * A project's wallet.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.15
 */
public interface Wallet {

    /**
     * Total cash in the wallet.
     * @return BigDecimal.
     */
    BigDecimal cash();

    /**
     * Available cash after substracting the debt.
     * @return BigDecimal.
     */
    default BigDecimal available() {
        return this.cash().subtract(this.debt());
    }

    /**
     * Debt. How much the project still has to pay.
     * @return BigDecimal.
     */
    default BigDecimal debt() {
        BigDecimal debt = BigDecimal.valueOf(0);
        for(final Contract contract : this.project().contracts()) {
            debt = debt.add(contract.value());
        }
        return debt;
    }
    /**
     * Pay an invoice.
     * @param invoice The Invoice to be paid.
     * @return Wallet having cash deducted with Invoice amount.
     *
     * @todo #979:60min Modify this method to create & return a Payment object.
     *  A payment could be successful or failed in both cases all related info
     *  should be in the returned Payment object.
     */
    Wallet pay(final Invoice invoice);

    /**
     * Type of this wallet.
     * @return String type.
     */
    String type();

    /**
     * Is this wallet active or not?
     * @return Boolean.
     */
    boolean active();

    /**
     * Project to which this Wallet belongs.
     * @return Project.
     */
    Project project();

    /**
     * Updates the total cash limit.
     * @param cash New total cash limit.
     * @return Wallet with new cash limit.
     */
    Wallet updateCash(BigDecimal cash);

    /**
     * Create a Stripe SetupIntent in order to create a PaymentMethod
     * using the client secret and Stripe UI widgets.<br><br>
     * For non-Stripe Wallet implementations, this method should throw
     * UnsupportedOperationException.
     * @see <a href='https://stripe.com/docs/payments/save-and-reuse'>docs</a>
     * @return SetupIntent.
     */
    SetupIntent paymentMethodSetupIntent();

    /**
     * Payment methods of this Wallet.
     * @return PaymentMethods
     */
    PaymentMethods paymentMethods();

    /**
     * Wallet's identifier.
     * @return String.
     */
    String identifier();

    /**
     * Billing info associated with this wallet.
     * @return BillingInfo.
     */
    BillingInfo billingInfo();

    /**
     * Remove this Wallet.
     * @return True if removed, false otherwise.
     */
    boolean remove();
    /**
     * Possible wallet types.
     */
    class Type {

        /**
         * Hidden ctor.
         */
        private Type(){ }

        /**
         * The fake wallet.
         */
        public static final String FAKE = "FAKE";

        /**
         * The Stripe wallet.
         */
        public static final String STRIPE = "STRIPE";
    }
}
