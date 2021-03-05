/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Invoices;
import com.selfxdsd.api.Payment;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Invoices belonging to a Contract.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class ContractInvoices implements Invoices {

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
    private final Supplier<Stream<Invoice>> invoices;

    /**
     * Ctor.
     *
     * @param contractId Contract id
     * @param invoices Supplier of the invoices Stream.
     * @param storage Storage.
     */
    public ContractInvoices(
        final Contract.Id contractId,
        final Supplier<Stream<Invoice>> invoices,
        final Storage storage
    ) {
        this.contractId = contractId;
        this.storage = storage;
        this.invoices = invoices;
    }

    @Override
    public Invoices ofContract(final Contract.Id id) {
        if (!this.contractId.equals(id)) {
            throw new IllegalStateException(
                "Already seeing the Invoices of a Contract, "
              + "you cannot see the Invoices of another Contract here."
            );
        }
        return this;
    }

    @Override
    public Payment registerAsPaid(
        final Invoice invoice,
        final BigDecimal contributorVat,
        final BigDecimal eurToRon
    ) {
        if(!invoice.contract().contractId().equals(this.contractId)){
            throw new IllegalStateException(
                "The given Invoice belongs to another contract."
            );
        }
        return this.storage.invoices().registerAsPaid(
            invoice, contributorVat, eurToRon
        );
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
    public Invoice createNewInvoice(final Contract.Id contractId) {
        if (!this.contractId.equals(contractId)) {
            throw new IllegalStateException(
                "Already seeing the Invoices of a Contract, "
              + "you cannot create an Invoice for another Contract here."
            );
        }
        return this.storage.invoices().createNewInvoice(contractId);
    }

    @Override
    public Invoice active() {
        Invoice active = this.invoices.get()
            .filter(invoice -> !invoice.isPaid())
            .sorted(Comparator.comparing(Invoice::createdAt))
            .findFirst()
            .orElse(null);
        if(active == null) {
            active = this.createNewInvoice(this.contractId);
        }
        return active;
    }

    @Override
    public Iterator<Invoice> iterator() {
        return this.invoices.get().iterator();
    }
}
