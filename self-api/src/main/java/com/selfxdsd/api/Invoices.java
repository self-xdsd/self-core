package com.selfxdsd.api;

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
    Invoices ofContract(final Contract.Id id);

    /**
     * Get an Invoice by its ID.
     * @param id Invoice's ID.
     * @return Invoice or null if it's not found.
     */
    Invoice getById(final int id);

}
