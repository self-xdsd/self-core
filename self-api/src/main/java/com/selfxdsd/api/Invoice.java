package com.selfxdsd.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents all the completed tasks by a contributor in a contract.
 * An invoice is active until payment is done.
 * A contract has at most one active invoice.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 * @todo #803:60min Add methods String::from() and String::to(). StoredInvoice
 *  should accept these values in the constructor. If these values are null,
 *  they should be read from Project.billingInfo and Contributor.billingInfo.
 *  The Project and the Contributor are accessible via the Contract.
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
     * @param commission PM's commission for the invoiced Task.
     * @return InvoicedTask.
     */
    InvoicedTask register(final Task task, final BigDecimal commission);

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
     * Timestamp of the payment.
     * @return LocalDateTime or null if it's not paid.
     */
    LocalDateTime paymentTime();

    /**
     * Returns the transaction ID of the payment.
     * @return String or null if it's not paid.
     */
    String transactionId();

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
     * The value of the invoiced tasks, without the PM's commission.
     * @return BigDecimal.
     */
    BigDecimal amount();

    /**
     * Value of the commission.
     * @return BigDecimal.
     */
    BigDecimal commission();

    /**
     * An invoice is active until payment is done.
     * @return Boolean
     */
    boolean isPaid();
}
