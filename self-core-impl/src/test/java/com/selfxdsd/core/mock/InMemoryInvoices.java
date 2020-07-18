package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.ContractInvoices;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * In-Memory Invoices for testing purposes.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public final class InMemoryInvoices implements Invoices {

    /**
     * Invoice id generator.
     */
    private int idGenerator;

    /**
     * All contract invoices.
     */
    private final Map<Integer, Invoice> invoices = new HashMap<>();

    /**
     * Storage context.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param storage Storage
     */
    public InMemoryInvoices(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Invoices ofContract(final Contract.Id id) {
        return new ContractInvoices(
            id,
            () -> StreamSupport
                .stream(storage.invoices().spliterator(), false)
                .filter(i -> i.contract().contractId().equals(id)),
            this.storage
        );
    }

    @Override
    public Invoice getById(final int id) {
        return this.invoices.get(id);
    }

    @Override
    public Invoice createNewInvoice(final Contract.Id contractId) {
        final Invoice created = new StoredInvoice(
            this.idGenerator++, this.storage.contracts().findById(contractId),
            LocalDateTime.now(), this.storage
        );
        this.invoices.put(created.invoiceId(), created);
        return created;
    }

    @Override
    public Invoice active() {
        throw new UnsupportedOperationException(
            "It's not possible to get an active Invoice here. "
          + "Call Invoices.ofContract(...) first."
        );
    }

    @Override
    public Iterator<Invoice> iterator() {
        throw new UnsupportedOperationException(
            "It's not possible to see all the invoices in Self. "
          + "Add a filter first (e.g. Invoices.ofContract(...)."
        );
    }
}
