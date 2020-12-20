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
     * Who emitted this Invoice?
     */
    private final String billedBy;

    /**
     * To whom is this Invoice billed? Who pays?
     */
    private final String billedTo;

    /**
     * Self storage context.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param id Invoice id.
     * @param contract Contract.
     * @param createdAt Invoice creation time.
     * @param paymentTime Time when this Invoice has been paid.
     * @param transactionId The payment's transaction ID.
     * @param billedBy Who emitted the Invoice.
     * @param billedTo Who pays it.
     * @param storage Self storage context.
     */
    public StoredInvoice(
        final int id,
        final Contract contract,
        final LocalDateTime createdAt,
        final LocalDateTime paymentTime,
        final String transactionId,
        final String billedBy,
        final String billedTo,
        final Storage storage
    ) {
        this.id = id;
        this.contract = contract;
        this.createdAt = createdAt;
        this.paymentTime = paymentTime;
        this.transactionId = transactionId;
        this.billedBy = billedBy;
        this.billedTo = billedTo;
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
    public InvoicedTasks tasks() {
        return this.storage.invoicedTasks().ofInvoice(this);
    }

    @Override
    public boolean isPaid() {
        return this.paymentTime != null && this.transactionId != null;
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
        for(final InvoicedTask task : this.tasks()) {
            revenue = revenue.add(task.value());
        }
        return revenue;
    }

    @Override
    public BigDecimal commission() {
        BigDecimal commission = BigDecimal.valueOf(0);
        for(final InvoicedTask task : this.tasks()) {
            commission = commission.add(task.commission());
        }
        return commission;
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
