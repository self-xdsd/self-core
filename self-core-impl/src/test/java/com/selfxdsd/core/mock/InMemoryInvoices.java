package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;

import java.time.Duration;
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
     * All contract invoices.
     */
    private final Map<Contract.Id, List<Invoice>> invoices = new HashMap<>();

    /**
     * Invoice Tasks. Key is the invoice id;
     */
    private final Map<Integer, List<InvoiceTask>> invoiceTasks =
        new HashMap<>();

    /**
     * Active contract invoice id.
     */
    private final Map<Contract.Id, Integer> activeIds = new HashMap<>();

    /**
     * Payed contract invoices ids.
     */
    private final Map<Contract.Id, List<Integer>> payedIds = new HashMap<>();

    /**
     * Invoice id generator.
     */
    private int idGenerator;

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
        return new ContractInvoices(id, storage);
    }

    @Override
    public InvoiceTask add(final Task task, final Duration timeSpent) {
        if(task.assignee() == null){
            throw new IllegalStateException("Task has no assignee.");
        }
        //construct the contract id from task.
        final String repoFullName = task.project().repo().fullName();
        final String username = task.assignee().username();
        final String provider = task.project().provider();
        final String role = task.role();
        final Contract.Id contractId = new Contract.Id(
            repoFullName,
            username,
            provider,
            role
        );
        //get contract invoices.
        List<Invoice> contractInvoices = invoices
            .computeIfAbsent(contractId, k -> new ArrayList<>());
        //get the active invoice id for the contract, if not create
        //a new invoice.
        Integer activeInvoiceId = activeIds.get(contractId);
        if (activeInvoiceId == null) {
            activeInvoiceId = ++this.idGenerator;
            contractInvoices.add(new StoredInvoice(activeInvoiceId,
                this.storage, contractId));
            activeIds.put(contractId, activeInvoiceId);
        }
        //create the invoice task.
        final int taskInvoiceId = activeInvoiceId;
        final InvoiceTask invoiceTask = new InvoiceTask() {
            @Override
            public int invoiceId() {
                return taskInvoiceId;
            }
            @Override
            public Duration timeSpent() {
                return timeSpent;
            }
            @Override
            public Task task() {
                return task;
            }
        };
        //add the task to invoice.
        invoiceTasks
            .computeIfAbsent(activeInvoiceId, k -> new ArrayList<>())
            .add(invoiceTask);

        return invoiceTask;
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
    public List<InvoiceTask> tasks(final int id) {
        return invoiceTasks.getOrDefault(id, List.of());
    }

    @Override
    public Iterator<Invoice> iterator() {
        //invoices across all contracts
        return invoices
            .values()
            .stream()
            .flatMap(Collection::stream)
            .iterator();
    }

    /**
     * Simulates "payment" of an invoice.
     * Used here to aid better representation of
     * {@link InMemoryInvoices} state (as in "deactivating" the invoice).
     *
     * @param id Invoice id.
     */
    public void pay(final int id) {
        final Map.Entry<Contract.Id, Integer> foundActiveEntry = activeIds
            .entrySet()
            .stream()
            .filter(e -> e.getValue() == id)
            .findFirst()
            .orElse(null);
        if (foundActiveEntry == null) {
            throw new IllegalStateException("There is no active invoice "
                + " with id " + id);
        }
        final Contract.Id contractId = foundActiveEntry.getKey();
        final Integer invoiceId = foundActiveEntry.getValue();
        payedIds.computeIfAbsent(contractId, k -> new ArrayList<>())
            .add(invoiceId);
        activeIds.remove(contractId);
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
        public InvoiceTask add(final Task task, final Duration timeSpent) {
            if(task.assignee() == null){
                throw new IllegalStateException("Task has no assignee.");
            }
            //construct the contract id from task.
            final String repoFullName = task.project().repo().fullName();
            final String username = task.assignee().username();
            final String provider = task.project().provider();
            final String role = task.role();
            final Contract.Id taskContractId = new Contract.Id(
                repoFullName,
                username,
                provider,
                role
            );
            if(!taskContractId.equals(this.contractId)){
                throw new IllegalStateException("Task is not part of"
                   + " the contract.");
            }
            return storage.invoices().add(task, timeSpent);
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
        public List<InvoiceTask> tasks(final int id) {
            checkInvoiceOfContract(id);
            return storage.invoices().tasks(id);
        }

        @Override
        public Iterator<Invoice> iterator() {
            return contractInvoices.get().iterator();
        }

        /**
         * Checks if the invoice is part of the current contract.
         * @param id Invoice id.
         * @throws IllegalStateException when invoice is not part
         * of the contract.
         */
        private void checkInvoiceOfContract(final int id) {
            final Invoice invoice = contractInvoices
                .get()
                .filter(i -> i.invoiceId() == id
                    && this.contractId.equals(i.contractId()))
                .findFirst()
                .orElse(null);
            if (invoice == null) {
                throw new IllegalStateException("Invoice with id"
                    + id + " is not part of the contract.");
            }
        }
    }
}
