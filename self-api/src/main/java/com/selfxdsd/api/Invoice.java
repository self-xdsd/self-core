package com.selfxdsd.api;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Invoice emitted by the Contributor to the Project.
 * Represents all the completed tasks by a contributor in a contract.
 * An invoice is active until payment is done.
 * A contract has at most one active invoice.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public interface Invoice {

    /**
     * The id of the invoice.
     * @return Integer
     */
    int invoiceId();

    /**
     * Register a Task on this Invoice.
     * @param task Task to be registered.
     * @param projectCommission Commission taken from the Project.
     * @return InvoicedTask.
     */
    InvoicedTask register(final Task task, final BigDecimal projectCommission);

    /**
     * The contract.
     * @return Contract
     */
    Contract contract();

    /**
     * Returns the Invoice's creation time.
     * @return LocalDateTime, never null.
     */
    LocalDateTime createdAt();

    /**
     * Latest Payment performed for this Invoice. If the Payment is successful,
     * the Invoice is considered paid.
     * @return Payment or null if no Payment as been performed yet.
     */
    Payment latest();

    /**
     * Who emitted the Invoice?
     * @return String.
     */
    String billedBy();

    /**
     * For whom this invoice is for? (who should pay it).
     * @return String.
     */
    String billedTo();

    /**
     * Country of the Contributor (who billed this Invoice).
     * @return String.
     */
    String billedByCountry();

    /**
     * Country of the Client (who received this Invoice).
     * Country of the Client (who received this Invoice).
     * @return String.
     */
    String billedToCountry();

    /**
     * Tasked invoiced here.
     * @return InvoicedTasks.
     */
    InvoicedTasks tasks();

    /**
     * Total amount of the invoice, value plus commission.
     * @return BigDecimal.
     */
    BigDecimal totalAmount();

    /**
     * The value of the invoiced tasks, without the PM's commission (what
     * the Contributor will get).
     * @return BigDecimal.
     */
    BigDecimal amount();

    /**
     * Value of the PM's commission.
     * @return BigDecimal.
     */
    BigDecimal commission();

    /**
     * EUR to RON exchange rage (e.g. if 487, then it means 1 EUR = 4,87 RON).
     * @return BigDecimal.
     */
    BigDecimal eurToRon();

    /**
     * An invoice is active until payment is done.
     * @return Boolean
     */
    boolean isPaid();

    /**
     * Get the corresponding platform invoice (it exists only if this
     * Invoice has been paid with a real wallet).
     * @return PlatformInvoice or null if it doesn't exist.
     */
    PlatformInvoice platformInvoice();

    /**
     * The payments performed for this Invoice. An Invoice may have many
     * payments out of which 1 is successful and n-1 are failed for some reason.
     * @return Payments.
     */
    Payments payments();

    /**
     * Turn this invoice to a PDF file.
     * @param out Output
     * @throws IOException If any I/O problems.
     */
    void toPdf(OutputStream out) throws IOException;
}
