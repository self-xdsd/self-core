package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * An Invoice stored in self.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 * @todo #400:30min Addapt method register to also accept the
 *  PMs commission as BigDecimal.
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
     * Time when this Invoice has been paid.
     */
    private final LocalDateTime paymentTime;

    /**
     * The payment's transaction ID.
     */
    private final String transactionId;

    /**
     * Self storage context.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param id Invoice id.
     * @param contract Contract.
     * @param createdAt Invoice creation time.
     * @param storage Self storage context.
     */
    public StoredInvoice(
        final int id,
        final Contract contract,
        final LocalDateTime createdAt,
        final Storage storage
    ) {
        this(id, contract, createdAt, null, null, storage);
    }

    /**
     * Ctor.
     * @param id Invoice id.
     * @param contract Contract.
     * @param createdAt Invoice creation time.
     * @param paymentTime Time when this Invoice has been paid.
     * @param transactionId The payment's transaction ID.
     * @param storage Self storage context.
     */
    public StoredInvoice(
        final int id,
        final Contract contract,
        final LocalDateTime createdAt,
        final LocalDateTime paymentTime,
        final String transactionId,
        final Storage storage
    ) {
        this.id = id;
        this.contract = contract;
        this.createdAt = createdAt;
        this.paymentTime = paymentTime;
        this.transactionId = transactionId;
        this.storage = storage;
    }

    @Override
    public int invoiceId() {
        return this.id;
    }

    @Override
    public InvoicedTask register(
        final Task task,
        final BigDecimal commission
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
                this, task, commission
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
    public LocalDateTime paymentTime() {
        return this.paymentTime;
    }

    @Override
    public String transactionId() {
        return this.transactionId;
    }

    @Override
    public InvoicedTasks tasks() {
        return this.storage.invoicedTasks().ofInvoice(this);
    }

    @Override
    public boolean isPaid() {
        return this.paymentTime != null && this.transactionId != null;
    }

    @Override
    public BigDecimal totalAmount() {
        throw new UnsupportedOperationException("Not yet implemented.");
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
}
