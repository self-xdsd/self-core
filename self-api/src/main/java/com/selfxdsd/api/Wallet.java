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
package com.selfxdsd.api;

import com.selfxdsd.api.exceptions.InvoiceException;
import com.selfxdsd.api.exceptions.PaymentMethodsException;
import com.selfxdsd.api.exceptions.WalletPaymentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Objects;

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
     * @throws InvoiceException.AlreadyPaid If the Invoice is already paid.
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
     * Payment methods of this Wallet.
     * @return PaymentMethods
     */
    PaymentMethods paymentMethods();

    /**
     * Missing wallet. Used when a Project has no wallet set up.
     */
    final class Missing implements Wallet {

        /**
         * Project to which this wallet belongs.
         */
        private final Project project;

        /**
         * Cash in the wallet.
         */
        private final BigDecimal cash;

        /**
         * Type of the wallet.
         */
        private final String type;

        /**
         * Is it active or not?
         */
        private final boolean active;

        /**
         * Wallet ID.
         */
        private final String identifier;

        /**
         * Ctor.
         * @param project Project to which this wallet belongs.
         * @param cash Cash in the wallet.
         * @param active Is it active or not?
         * @param identifier Wallet identifier.
         */
        public Missing(
            final Project project,
            final BigDecimal cash,
            final boolean active,
            final String identifier
        ) {
            this.project = project;
            this.cash = cash;
            this.type = Type.FAKE;
            this.active = active;
            this.identifier = identifier;
        }

        @Override
        public BigDecimal cash() {
            return this.cash;
        }

        @Override
        public Wallet pay(final Invoice invoice) {
            if(invoice.isPaid()) {
                throw new InvoiceException.AlreadyPaid(invoice);
            }
            final BigDecimal newCash = this.cash.subtract(invoice
                .totalAmount());
            if (newCash.longValueExact() < 0L) {
                throw new WalletPaymentException("No cash available in wallet "
                    + "for paying invoice #" + invoice.invoiceId()
                    + ". Please increase the limit from your dashboard with"
                    + " at least " + newCash.abs().divide(BigDecimal
                    .valueOf(1000), RoundingMode.HALF_UP) + "$."
                );
            }
            return new Missing(this.project, newCash, this.active,
                this.identifier);
        }

        @Override
        public String type() {
            return this.type;
        }

        @Override
        public boolean active() {
            return this.active;
        }

        @Override
        public Project project() {
            return this.project;
        }

        @Override
        public Wallet updateCash(final BigDecimal cash) {
            return new Missing(this.project, cash, this.active,
                this.identifier);
        }

        @Override
        public PaymentMethods paymentMethods() {
            return new PaymentMethods() {
                @Override
                public PaymentMethod register(final Wallet wallet,
                                             final String identifier) {
                    throw new PaymentMethodsException("Can't register a "
                        + " payment method for a missing wallet.");
                }

                @Override
                public boolean remove(final PaymentMethod paymentMethod) {
                    throw new PaymentMethodsException("Can't remove a "
                        + " payment method for a missing wallet.");
                }

                @Override
                public PaymentMethods ofWallet(final Wallet wallet) {
                    throw new PaymentMethodsException("A missing wallet "
                        + " has no payment methods.");
                }

                @Override
                public PaymentMethod active() {
                    return null;
                }

                @Override
                public PaymentMethod activate(
                    final PaymentMethod paymentMethod
                ) {
                    throw new PaymentMethodsException("Can't activate "
                        + "a missing wallet payment method.");
                }

                @Override
                public Iterator<PaymentMethod> iterator() {
                    throw new PaymentMethodsException("Can't iterate over "
                        + "a missing wallet payment methods.");
                }
            };
        }

        @Override
        public boolean equals(final Object other) {
            boolean equals;
            if (this == other) {
                equals = true;
            } else {
                if (other == null || getClass() != other.getClass()) {
                    equals = false;
                } else {
                    final Missing missing = (Missing) other;
                    equals = this.project.equals(missing.project)
                        && this.type.equals(missing.type);
                }
            }
            return equals;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.project, this.type);
        }
    }

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
