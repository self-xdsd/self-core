package com.selfxdsd.api;

import java.time.Duration;
import java.util.List;

/**
 * Invoices of a contract.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public interface Invoices extends Iterable<Invoice> {

    /**
     * Find all invoices of a contract.
     * @param id Contract's id
     * @return Iterable of invoices.
     */
    Invoices ofContract(Contract.Id id);

    /**
     * Adds a completed task to the available active contract invoice.
     * If there is no active invoice, a new invoice will be created.
     * <br>
     * Task criteria:
     * <ul>
     *     <li>Must be assigned to contract's contributor.</li>
     *     <li>Must be part of the contract's project tasks.</li>
     *     <li>Must not be previously added to other invoices
     *     in the same contract, or to the active invoice.</li>
     * </ul>
     *
     * @param task Completed task.
     * @param timeSpent Time spent to finish the task.
     * @return Created InvoiceTask
     */
    InvoiceTask add(Task task, Duration timeSpent);

    /**
     * Get an Invoice by its ID.
     * @param id Invoice's ID.
     * @return Invoice or null if it's not found.
     */
    Invoice getById(final int id);

    /**
     * Tasks of an invoice. Since there are few tasks associated to an invoice,
     * a list is enough instead of a generic iterable.
     * @param id Invoice id.
     * @return List of Invoice Tasks.
     */
    List<InvoiceTask> tasks(int id);

}
