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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This is the invoice emitted by Self XDSD (the Platform) to the
 * Contributor, for the commission.
 * @see <a href="https://docs.self-xdsd.com/wallets.html#invoicing">Docs</a>
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.50
 */
public interface PlatformInvoice {

    /**
     * The internal id of the PlatformInvoice.
     * @checkstyle MethodName (5 lines)
     * @return Integer.
     */
    int id();

    /**
     * Serial number of the PlatformInvoice. This will be
     * the identification id printed out on paper.
     * @return String.
     */
    default String serialNumber() {
        String serialNumber = String.valueOf(this.id());
        while(serialNumber.length() < 7) {
            serialNumber = "0" + serialNumber;
        }
        return "SLF" + serialNumber;
    }

    /**
     * Returns the creation time.
     * @return LocalDateTime, never null.
     */
    LocalDateTime createdAt();

    /**
     * Billed by. This should always contain the billing info
     * of the company behind Self XDSD.
     * @checkstyle LineLength (20 lines).
     * @return String
     */
    default String billedBy() {
        return new StringBuilder()
            .append("SC Extremely Distributed Technologies SRL\n\n")
            .append("Transilvaniei St. 18, bl. U2, ap. 111\n")
            .append("Oradea, Romania\n\n")
            .append("Nr. ORC/Reg. Number: J05/197/2021\n")
            .append("Cod TVA/VAT Code: RO43621869\n")
            .append("EUID: ROONRC.J05/197/2021")
            .toString();
    }

    /**
     * The Contributor's billing info.
     * @return String.
     */
    String billedTo();

    /**
     * Self's commission, in cents.
     * @return BigDecimal.
     */
    BigDecimal commission();

    /**
     * Applied VAT (Value Added Tax).
     * @return BigDecimal.
     */
    BigDecimal vat();

    /**
     * Total amount (commission + vat).
     * @return BigDecimal.
     */
    default BigDecimal totalAmount() {
        return this.commission().add(this.vat());
    }

    /**
     * Corresponding invoice (the invoice emitted
     * by the Contributor to the Project). This invoice
     * can be missing, if the Contributor's contract is removed.
     * @return Invoice or null if it's missing.
     */
    Invoice invoice();

    /**
     * Transaction ID (id of the payment).
     * @return String, never null.
     */
    String transactionId();

    /**
     * Timestamp of the payment.
     * @return LocalDateTime, never null.
     */
    LocalDateTime paymentTime();

    /**
     * Turn this PlatformInvoice into PDF.
     * @param outputStream Stream where the PDF will be written.
     * @throws IOException If something goes wrong.
     */
    void toPdf(final OutputStream outputStream) throws IOException;
}
