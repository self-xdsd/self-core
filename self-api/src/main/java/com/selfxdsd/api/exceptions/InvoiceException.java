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
package com.selfxdsd.api.exceptions;

import com.selfxdsd.api.Invoice;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for Invoice self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.15
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class InvoiceException extends SelfException {

    /**
     * Invoice.
     */
    private final Invoice invoice;

    /**
     * Ctor.
     * @param invoice Invoice in question.
     */
    public InvoiceException(final Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public JsonObject json() {
        return Json.createPatchBuilder()
            .add("id", this.invoice.invoiceId())
            .build()
            .apply(super.json());
    }

    @Override
    public String getSelfMessage() {
        return  "Invoice #" + this.invoice.invoiceId();
    }

    /**
     * Already paid Invoice Self exception.
     */
    public static final class AlreadyPaid extends InvoiceException {

        /**
         * Ctor.
         *
         * @param invoice Invoice in question.
         */
        public AlreadyPaid(final Invoice invoice) {
            super(invoice);
        }

        @Override
        public String getSelfMessage() {
            return super.getSelfMessage() + " is already paid, "
                + "can't pay it twice.";
        }
    }

}
