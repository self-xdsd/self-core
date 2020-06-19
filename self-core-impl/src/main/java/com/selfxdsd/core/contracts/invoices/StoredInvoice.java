package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A Invoice stored in self.
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
     * Contract's id of this invoice.
     */
    private final Contract.Id contractId;

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
     * @param contractId Contract's id of this invoice.
     * @param createdAt Invoice creation time.
     * @param storage Self storage context.
     */
    public StoredInvoice(
        final int id,
        final Contract.Id contractId,
        final LocalDateTime createdAt,
        final Storage storage
    ) {
        this(id, contractId, createdAt, null, null, storage);
    }

    /**
     * Ctor.
     * @param id Invoice id.
     * @param contractId Contract's id of this invoice
     * @param createdAt Invoice creation time.
     * @param paymentTime Time when this Invoice has been paid.
     * @param transactionId The payment's transaction ID.
     * @param storage Self storage context.
     */
    public StoredInvoice(
        final int id,
        final Contract.Id contractId,
        final LocalDateTime createdAt,
        final LocalDateTime paymentTime,
        final String transactionId,
        final Storage storage
    ) {
        this.id = id;
        this.contractId = contractId;
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
    public Contract.Id contractId() {
        return this.contractId;
    }

    @Override
    public LocalDateTime createdAt() {
        return this.createdAt;
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
