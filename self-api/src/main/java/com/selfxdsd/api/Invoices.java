package com.selfxdsd.api;

import java.math.BigDecimal;

/**
 * Invoices of a contract.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 * @todo #552:45min. Finish writing custom Self Exception for Invoices
 *  (InvoicesException). Also include the already written InvoiceException
 *  into InvoicesExceptions as InvoicesException.Single.
 */
public interface Invoices extends Iterable<Invoice> {

    /**
     * Get an Invoice by its ID.
     * @param id Invoice's ID.
     * @return Invoice or null if it's not found.
     */
    Invoice getById(final int id);

    /**
     * Create a new Invoice for a Contract.
     * @param contractId Contract Id.
     * @return Invoice.
     */
    Invoice createNewInvoice(final Contract.Id contractId);

    /**
     * Get the active Invoice (the one that is not yet paid).
     * If no active invoice exists (all have been paid), create
     * a new one and return it.
     * @return Invoice.
     */
    Invoice active();

    /**
     * Find all invoices of a contract.
     * @param id Contract's id
     * @return Iterable of invoices.
     */
    Invoices ofContract(final Contract.Id id);

    /**
     * Register an Invoice as paid.
     * @param invoice Paid invoice.
     * @param contributorVat Vat which Self takes from the Contributor.
     * @return True or false, depending on whether the operation succeeded.
     */
    boolean registerAsPaid(
        final Invoice invoice, final BigDecimal contributorVat
    );
}
