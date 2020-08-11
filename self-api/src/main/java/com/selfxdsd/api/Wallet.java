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

import com.selfxdsd.api.exceptions.InvoiceAlreadyPaidExeption;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A project's wallet.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.15
 */
public interface Wallet {

    /**
     * Available cash.
     * @return BigDecimal.
     */
    BigDecimal available();

    /**
     * Pay an invoice.
     * @param invoice The Invoice to be paid.
     * @return The paid Invoice containing the payment time and transaction ID.
     * @throws InvoiceAlreadyPaidExeption If the Invoice is already paid.
     */
    Invoice pay(final Invoice invoice);

    /**
     * Missing wallet. Used when a Project has no wallet set up.
     */
    final class Missing implements Wallet {

        /**
         * Cash in the wallet.
         */
        private final BigDecimal cash = BigDecimal.valueOf(Integer.MAX_VALUE);

        @Override
        public BigDecimal available() {
            return this.cash;
        }

        @Override
        public Invoice pay(final Invoice invoice) {
            if(invoice.isPaid()) {
                throw new InvoiceAlreadyPaidExeption(invoice);
            }
            final LocalDateTime paymentTime = LocalDateTime.now();
            final String transactionId = "fk-" + UUID
                .randomUUID()
                .toString()
                .replace("-", "");
            return new Invoice() {
                @Override
                public int invoiceId() {
                    return invoice.invoiceId();
                }

                @Override
                public InvoicedTask register(final Task task) {
                    throw new IllegalStateException(
                        "Invoice is already paid, can't add a new Task to it!"
                    );
                }

                @Override
                public Contract contract() {
                    return invoice.contract();
                }

                @Override
                public LocalDateTime createdAt() {
                    return invoice.createdAt();
                }

                @Override
                public LocalDateTime paymentTime() {
                    return paymentTime;
                }

                @Override
                public String transactionId() {
                    return transactionId;
                }

                @Override
                public InvoicedTasks tasks() {
                    return invoice.tasks();
                }

                @Override
                public BigDecimal totalAmount() {
                    return invoice.totalAmount();
                }

                @Override
                public boolean isPaid() {
                    return true;
                }
            };
        }
    }
}
