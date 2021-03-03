/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A Payment of an {@link Invoice}.
 * <br>
 * It represent a Payment that could be Successful or Failed.
 * @author Ali FELLAHI (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public interface Payment {

    /**
     * The {@link Invoice} to which this Payment is made.
     * @return Invoice
     */
    Invoice invoice();

    /**
     * The invoice sent to {@link Contributor} in case of successful payment.
     * @return PlatformInvoice or null if the payment failed.
     */
    PlatformInvoice platformInvoice();

    /**
     * The transaction id of the payment.
     * @return String or empty if the payment has FAILED.
     */
    String transactionId();

    /**
     * The timestamp of the Payment.
     * @return LocalDateTime
     */
    LocalDateTime paymentTime();

    /**
     * The value of the Payment.<br>
     * Which is usually the total amount of the Invoice.
     * @return BigDecimal or null if the payment fail.
     */
    BigDecimal value();

    /**
     * The status of the Payment (SUCCESSFUL or FAILED).
     * @return String.
     */
    String status();

    /**
     * The description of the reason why the Payment fail.
     * @return String or empty if the Payment done successfully.
     */
    String failReason();

    /**
     * Possible status of a Payment.<br>
     * A Payment could be SUCCESSFUL or FAILED.
     */
    class Status {
        /**
         * Private ctor.
         */
        private Status(){};

        /**
         * Successful status.
         */
        public static final String SUCCESSFUL = "SUCCESSFUL";

        /**
         * Failed status.
         */
        public static final String FAILED = "FAILED";
    }

}
