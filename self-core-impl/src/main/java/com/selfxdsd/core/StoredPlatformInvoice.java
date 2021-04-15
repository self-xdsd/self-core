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
package com.selfxdsd.core;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.storage.Storage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * PlatformInvoice stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.50
 * @checkstyle TrailingComment (500 lines)
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class StoredPlatformInvoice implements PlatformInvoice {

    /**
     * Internal Id.
     */
    private final int id;

    /**
     * Creation date.
     */
    private final LocalDateTime createdAt;

    /**
     * Billing data of the contributor.
     */
    private final String billedTo;

    /**
     * Invoiced commission.
     */
    private final BigDecimal commission;

    /**
     * Invoiced VAT.
     */
    private final BigDecimal vat;

    /**
     * Transaction ID (id of the payment).
     */
    private final String transactionId;

    /**
     * Payment timestamp.
     */
    private final LocalDateTime paymentTime;

    /**
     * ID of the corresponding Invoice.
     */
    private final int invoiceId;

    /**
     * Euro to RON exchange rage. For instance, if this
     * value is 478, it means 1 EUR = 4,87 RON.
     */
    private final BigDecimal eurToRon;

    /**
     * Self storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param id Internal ID of this PlatformInvoice.
     * @param createdAt Creation time.
     * @param billedTo Billing info of the contributor.
     * @param commission Invoiced commission.
     * @param vat Invoiced VAT.
     * @param transactionId Transaction (payment) ID.
     * @param paymentTime Payment timestamp.
     * @param invoiceId ID of the corresponding Invoice.
     * @param eurToRon Euro to RON exchange rate.
     * @param storage Self Storage.
     */
    public StoredPlatformInvoice(
        final int id,
        final LocalDateTime createdAt,
        final String billedTo,
        final BigDecimal commission,
        final BigDecimal vat,
        final String transactionId,
        final LocalDateTime paymentTime,
        final int invoiceId,
        final BigDecimal eurToRon,
        final Storage storage
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.billedTo = billedTo;
        this.commission = commission;
        this.vat = vat;
        this.transactionId = transactionId;
        this.paymentTime = paymentTime;
        this.invoiceId = invoiceId;
        this.eurToRon = eurToRon;
        this.storage = storage;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    @Override
    public String billedTo() {
        return this.billedTo;
    }

    @Override
    public BigDecimal commission() {
        return this.commission;
    }

    @Override
    public BigDecimal vat() {
        return this.vat;
    }

    @Override
    public Invoice invoice() {
        return this.storage.invoices().getById(this.invoiceId);
    }

    @Override
    public String transactionId() {
        return this.transactionId;
    }

    @Override
    public LocalDateTime paymentTime() {
        return this.paymentTime;
    }

    @Override
    public void toPdf(final OutputStream outputStream) throws IOException {
        final PDDocument doc = PDDocument.load(
            this.getResourceAsFile("platform_invoice_template.pdf")
        );
        final PDDocumentCatalog docCatalog = doc.getDocumentCatalog();
        final PDAcroForm acroForm = docCatalog.getAcroForm();

        acroForm.getField("invoiceId").setValue(this.serialNumber());
        acroForm.getField("createdAt").setValue(
            this.createdAt.toLocalDate().toString()
        );
        acroForm.getField("billedBy").setValue(this.billedBy());
        acroForm.getField("billedTo").setValue(this.billedTo());
        String invoiceIdPrint = "";
        if(this.invoiceId > 0) {
            invoiceIdPrint = " SLFX-" + this.invoiceId;
        }
        acroForm.getField("commission_text").setValue(
            "Commission for Invoice/Comision pt. Factura" + invoiceIdPrint
        );
        acroForm.getField("commission_value").setValue(
            NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(
                    this.commission.divide(BigDecimal.valueOf(100))
                )
            + " / "
            + this.euroToRon(this.commission).toString().replace('.', ',')
            + " RON"
        );
        if(this.vat.compareTo(BigDecimal.valueOf(0)) > 0) {
            acroForm.getField("vat_text").setValue("VAT/TVA (19%)");
            acroForm.getField("vat_value").setValue(
                NumberFormat
                    .getCurrencyInstance(Locale.GERMANY)
                    .format(
                        this.vat.divide(BigDecimal.valueOf(100))
                    )
                + " / "
                + this.euroToRon(this.vat).toString().replace('.', ',')
                + " RON"
            );
        } else if (this.vat.compareTo(BigDecimal.valueOf(0)) < 0) {
            acroForm.getField("vat_text").setValue(
                "VAT reverse charge applies./Taxare inversa TVA."
            );
        }
        acroForm.getField("totalDue").setValue(
            NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(
                    this.totalAmount()
                        .divide(BigDecimal.valueOf(100))
                )
            + " / "
            + this.euroToRon(this.totalAmount()).toString().replace('.', ',')
            + " RON"
        );

        acroForm.getField("exchangeRate").setValue(
            "1 EUR = " + this.eurToRon.divide(
                BigDecimal.valueOf(100),
                2,
                RoundingMode.HALF_UP
            ).toString().replace('.', ',')+ " RON"
        );

        acroForm.getField("paidAt").setValue(
            this.paymentTime.toLocalDate().toString()
        );

        acroForm.flatten();

        doc.addPage(docCatalog.getPages().get(0));
        doc.removePage(1); //remove trailing blank page

        doc.save(outputStream);
        doc.close();
    }

    /**
     * Convert Euro to RON.
     * @param euro Value in EUR.
     * @return Value in RON.
     */
    private BigDecimal euroToRon(final BigDecimal euro) {
        return euro.multiply(this.eurToRon).divide(
            BigDecimal.valueOf(10000),
            2,
            RoundingMode.HALF_UP
        );
    }

    /**
     * Convenience method to get the PDF template resource as a File.
     * @param resourcePath Name of the file.
     * @throws IOException If something goes wrong.
     * @return File.
     */
    private File getResourceAsFile(
        final String resourcePath
    ) throws IOException {
        final InputStream stream = this.getClass().getClassLoader()
            .getResourceAsStream(resourcePath);
        final File tempFile = File.createTempFile(
            String.valueOf(stream.hashCode()), ".tmp"
        );
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}
