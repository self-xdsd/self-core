package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.InvoiceTask;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.util.List;

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
     * Self storage context.
     */
    private final Storage storage;

    /**
     * Contract's id of this invoice.
     */
    private final Contract.Id contractId;

    /**
     * Ctor.
     * @param id Invoice id.
     * @param storage Self storage context.
     * @param contractId Contract's id of this invoice
     */
    public StoredInvoice(
        final int id,
        final Storage storage,
        final Contract.Id contractId) {
        this.id = id;
        this.storage = storage;
        this.contractId = contractId;
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
    public boolean isActive() {
        return this.storage.invoices().ofContract(this.contractId)
            .isActive(this.id);
    }

    @Override
    public BigDecimal totalAmount() {
        final Contract contract = this.storage.contracts().findById(contractId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(contract != null){
            final BigDecimal rate = contract.hourlyRate();
            totalAmount = this.tasks().stream()
                .reduce(BigDecimal.ZERO,
                    (acc, curr)-> acc.add(rate.multiply(BigDecimal
                        .valueOf(curr.timeSpent().toHours()))),
                    (acc, curr)-> acc);
        }
        return totalAmount;
    }

    @Override
    public List<InvoiceTask> tasks() {
        return this.storage.invoices().ofContract(this.contractId)
            .tasks(this.id);
    }
}
