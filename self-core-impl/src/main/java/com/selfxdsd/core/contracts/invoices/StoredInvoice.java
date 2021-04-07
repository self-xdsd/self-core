package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.projects.XmlBnr;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * An Invoice stored in self.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 * @checkstyle ExecutableStatementCount (500 lines)
 * @checkstyle TrailingComment (500 lines)
 * @checkstyle JavaNCSS (500 lines)
 * @todo #826:60min Modify the PDF template and the code in toPdf()
 *  such that more tasks are written on more pages. At the moment
 *  only 40 tasks are written to the 1-page PDF.
 * @todo #1075:30min Implement and test method contributorCommission() which
 *  will return the total commission taken from the Contributor, with this
 *  Invoice.
 */
public final class StoredInvoice implements Invoice {

    /**
     * Invoice id.
     */
    private final int id;

    /**
     * Contract.
     */
    private final Contract contract;

    /**
     * Creation time.
     */
    private final LocalDateTime createdAt;

    /**
     * Latest Payment performed for this Invoice. If it's successful,
     * then this Invoice is considered paid.
     */
    private final Payment latest;

    /**
     * Who emitted this Invoice?
     */
    private final String billedBy;

    /**
     * To whom is this Invoice billed? Who pays?
     */
    private final String billedTo;

    /**
     * Country of the Contributor (who emitted this Invoice).
     */
    private final String billedByCountry;

    /**
     * Country of the Client (who received this Invoice).
     */
    private final String billedToCountry;

    /**
     * EUR to RON exchange rate (e.g. if 487, it means 1 EUR = 4,87 RON).
     */
    private final BigDecimal eurToRon;

    /**
     * Self storage context.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param id Invoice id.
     * @param contract Contract.
     * @param createdAt Invoice creation time.
     * @param latest Latest Payment performed for this Invoice.
     * @param billedBy Who emitted the Invoice.
     * @param billedTo Who pays it.
     * @param billedByCountry Country of the Contributor.
     * @param billedToCountry Country of the Client.
     * @param eurToRon EUR to RON exchange rate.
     * @param storage Self storage context.
     * @checkstyle ParameterNumber (50 lines)
     */
    public StoredInvoice(
        final int id,
        final Contract contract,
        final LocalDateTime createdAt,
        final Payment latest,
        final String billedBy,
        final String billedTo,
        final String billedByCountry,
        final String billedToCountry,
        final BigDecimal eurToRon,
        final Storage storage
    ) {
        this.id = id;
        this.contract = contract;
        this.createdAt = createdAt;
        this.latest = latest;
        this.billedBy = billedBy;
        this.billedTo = billedTo;
        this.billedByCountry = billedByCountry;
        this.billedToCountry = billedToCountry;
        this.eurToRon = eurToRon;
        this.storage = storage;
    }

    @Override
    public int invoiceId() {
        return this.id;
    }

    @Override
    public InvoicedTask register(
        final Task task,
        final BigDecimal projectCommission,
        final BigDecimal contributorCommission
    ) {
        final Contract.Id taskContract = new Contract.Id(
            task.project().repoFullName(),
            task.assignee().username(),
            task.project().provider(),
            task.role()
        );
        if(!this.contract.contractId().equals(taskContract)) {
            throw new IllegalArgumentException(
                "The given Task does not belong to this Invoice!"
            );
        } else {
            if(this.isPaid()) {
                throw new IllegalStateException(
                    "Invoice is already paid, can't add a new Task to it!"
                );
            }
            return this.storage.invoicedTasks().register(
                this, task, projectCommission
            );
        }
    }

    @Override
    public Contract contract() {
        return this.contract;
    }

    @Override
    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    @Override
    public Payment latest() {
        return this.latest;
    }

    @Override
    public String billedBy() {
        final String billedBy;
        if(this.billedBy != null && !this.billedBy.isEmpty()) {
            billedBy = this.billedBy;
        } else {
            billedBy = this.contract.contributor().billingInfo().toString();
        }
        return billedBy;
    }

    @Override
    public String billedTo() {
        final String billedTo;
        if(this.billedTo != null && !this.billedTo.isEmpty()) {
            billedTo = this.billedTo;
        } else {
            billedTo = this.contract.project().billingInfo().toString();
        }
        return billedTo;
    }

    @Override
    public String billedByCountry() {
        final String billedByCountry;
        if(this.billedByCountry != null && !this.billedByCountry.isEmpty()) {
            billedByCountry = this.billedByCountry;
        } else {
            billedByCountry = this.contract.contributor().billingInfo()
                .country();
        }
        return billedByCountry;
    }

    @Override
    public String billedToCountry() {
        final String billedToCountry;
        if(this.billedToCountry != null && !this.billedToCountry.isEmpty()) {
            billedToCountry = this.billedToCountry;
        } else {
            billedToCountry = this.contract.project().billingInfo().country();
        }
        return billedToCountry;

    }

    @Override
    public InvoicedTasks tasks() {
        return this.storage.invoicedTasks().ofInvoice(this);
    }

    @Override
    public boolean isPaid() {
        return this.latest != null && this.latest.status().equalsIgnoreCase(
            Payment.Status.SUCCESSFUL
        );
    }

    @Override
    public PlatformInvoice platformInvoice() {
        final PlatformInvoice found;
        if(this.isPaid()) {
            final String transactionId = this.latest.transactionId();
            if(transactionId.startsWith("fake_payment_")) {
                found = null;
            } else {
                found = this.storage.platformInvoices().getByPayment(
                    transactionId, this.latest.paymentTime()
                );
            }
        } else {
            found = null;
        }
        return found;
    }

    @Override
    public Payments payments() {
        return this.storage.payments().ofInvoice(this);
    }

    @Override
    public void toPdf(final OutputStream out) throws IOException {
        final String billedByCountry = this.billedByCountry();
        final String billedToCountry = this.billedToCountry();

        final String totalAmountText;
        final String exRateText;
        if("RO".equalsIgnoreCase(billedByCountry)
            && "RO".equalsIgnoreCase(billedToCountry)) {
            final BigDecimal totalAmount = this.totalAmount();
            final BigDecimal exRate = this.eurToRon();
            totalAmountText = NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(totalAmount.divide(BigDecimal.valueOf(100)))
                + " / "
                + this.convertEuroToRon(totalAmount, exRate).toString()
                    .replace('.', ',')
                + " RON";

            exRateText = "BNR Exchange Rate: 1 EUR = " + exRate.divide(
                BigDecimal.valueOf(100),
            2,
                RoundingMode.HALF_UP
            ).toString().replace('.', ',') + " RON";
        } else {
            totalAmountText = NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(
                    this.totalAmount()
                        .divide(BigDecimal.valueOf(100))
                );
            exRateText = "";
        }
        final PDDocument doc = PDDocument.load(
            this.getResourceAsFile("invoice_template.pdf")
        );
        final PDDocumentCatalog docCatalog = doc.getDocumentCatalog();
        final PDAcroForm acroForm = docCatalog.getAcroForm();

        acroForm.getField("invoiceId").setValue("SLFX-" + this.id);
        acroForm.getField("createdAt").setValue(
            this.createdAt.toLocalDate().toString()
        );
        acroForm.getField("billedBy").setValue(this.billedBy());
        acroForm.getField("billedTo").setValue(this.billedTo());
        acroForm.getField("project").setValue(
            this.contract.project().repoFullName()
        );
        acroForm.getField("role").setValue(
            this.contract.role()
        );
        acroForm.getField("hourlyRate").setValue(
            NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(
                    this.contract.hourlyRate()
                        .divide(BigDecimal.valueOf(100))
                )
        );

        acroForm.getField("totalDue").setValue(totalAmountText);
        acroForm.getField("exchangeRate").setValue(exRateText);

        if(this.isPaid()) {
            acroForm.getField("status").setValue("Paid");
        } else {
            acroForm.getField("status").setValue("Active (not paid)");
        }
        final StringBuilder taskIds = new StringBuilder();
        final StringBuilder estimations = new StringBuilder();
        final StringBuilder values = new StringBuilder();
        final StringBuilder commissions = new StringBuilder();

        int count = 0;
        for(final InvoicedTask invoiced : this.tasks()) {
            if(count == 40) {
                taskIds.append("...");
                estimations.append("...");
                values.append("...");
                commissions.append("...");
                break;
            }
            final Task task = invoiced.task();
            taskIds.append(task.issueId()).append("\n");
            estimations.append(task.estimation()).append("\n");
            values.append(invoiced.value().divide(BigDecimal.valueOf(100)))
                .append("\n");
            commissions.append(
                invoiced.projectCommission().divide(BigDecimal.valueOf(100))
            ).append("\n");
            count++;
        }

        acroForm.getField("taskIds").setValue(taskIds.toString());
        acroForm.getField("estimations").setValue(estimations.toString());
        acroForm.getField("values").setValue(values.toString());
        acroForm.getField("commissions").setValue(commissions.toString());

        acroForm.flatten();

        doc.addPage(docCatalog.getPages().get(0));
        doc.removePage(1); //remove trailing blank page

        doc.save(out);
        doc.close();
    }

    @Override
    public BigDecimal totalAmount() {
        BigDecimal total = BigDecimal.valueOf(0);
        for(final InvoicedTask task : this.tasks()) {
            total = total.add(task.totalAmount());
        }
        return total;
    }

    @Override
    public BigDecimal amount() {
        BigDecimal revenue = BigDecimal.valueOf(0);
        for (final InvoicedTask task : this.tasks()) {
            revenue = revenue.add(task.value());
        }
        return revenue;
    }

    @Override
    public BigDecimal projectCommission() {
        BigDecimal commission = BigDecimal.valueOf(0);
        for(final InvoicedTask task : this.tasks()) {
            commission = commission.add(task.projectCommission());
        }
        return commission;
    }

    @Override
    public BigDecimal eurToRon() {
        final BigDecimal eurToRon;
        if(!this.eurToRon.equals(BigDecimal.valueOf(0))) {
            eurToRon = this.eurToRon;
        } else {
            eurToRon = new XmlBnr().euroToRon();
        }
        return eurToRon;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof Invoice
            && this.id == ((Invoice) obj).invoiceId());
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

    /**
     * Convert Euro to RON.
     * @param euro Value in EUR.
     * @param rate EUR to RON Exchange Rate.
     * @return Value in RON.
     */
    private BigDecimal convertEuroToRon(
        final BigDecimal euro,
        final BigDecimal rate
    ) {
        return euro.multiply(rate).divide(
            BigDecimal.valueOf(10000),
            2,
            RoundingMode.HALF_UP
        );
    }
}
