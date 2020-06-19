package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * In-Memory Invoices for testing purposes.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.3
 * @todo #181:30min Refactor and test ContractInvoices to be part
 *  of self-impl API.
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
        return new ContractInvoices(id, this.storage);
    }

    @Override
    public Invoice getById(final int id) {
        return this.invoices.get(id);
    }

    @Override
    public Iterator<Invoice> iterator() {
        throw new UnsupportedOperationException(
            "It's not possible to see all the invoices in Self. "
          + "Add a filter first (e.g. Invoices.ofContract(...)."
        );
    }

    /**
     * Invoices of contract.
     */
    private static final class ContractInvoices implements Invoices {
        /**
         * Id contract of this invoices.
         */
        private final Contract.Id contractId;

        /**
         * Storage context.
         */
        private final Storage storage;

        /**
         * Stored invoices for this contract.
         * We use a stream because we don't want to load in
         * memory all the contract invoices.
         * <br>
         * In order to "reuse" them, since streams are one time use only,
         * we wrap the stream in a supplier, .
         */
        private final Supplier<Stream<Invoice>> contractInvoices;

        /**
         * Ctor.
         *
         * @param contractId Contract id
         * @param storage Storage.
         */
        private ContractInvoices(final Contract.Id contractId,
                                 final Storage storage) {
            this.contractId = contractId;
            this.storage = storage;
            this.contractInvoices = () ->StreamSupport
                .stream(storage.invoices().spliterator(), false)
                .filter(i -> i.contractId().equals(contractId));
        }

        @Override
        public Invoices ofContract(final Contract.Id id) {
            Invoices invoices;
            if (this.contractId.equals(id)) {
                invoices = this;
            } else {
                invoices = new ContractInvoices(id, this.storage);
            }
            return invoices;
        }

        @Override
        public Invoice getById(final int id) {
            Invoice found = null;
            for(final Invoice invoice : this) {
                if(invoice.invoiceId() == id) {
                    found = invoice;
                    break;
                }
            }
            return found;
        }

        @Override
        public Iterator<Invoice> iterator() {
            return contractInvoices.get().iterator();
        }
    }
}
