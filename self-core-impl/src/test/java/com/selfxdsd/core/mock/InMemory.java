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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonValue;

/**
 * In Memory storage used for testing.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 *
 * todo #979:60min Implement InMemoryPayments & InMemory.payments() method.
 */
public final class InMemory implements Storage {

    /**
     * In-memory users.
     */
    private Users users = new InMemoryUsers(this,
        new MockJsonResources(r -> new MockJsonResources
            .MockResource(404, JsonValue.NULL)));
    /**
     * In-memory project-managers.
     */
    private ProjectManagers projectManagers = new InMemoryProjectManagers(this);

    /**
     * In-memory projects.
     */
    private Projects projects = new InMemoryProjects(this);

    /**
     * In-memory wallets.
     */
    private Wallets wallets = new InMemoryWallets(this);

    /**
     * In-memory contracts.
     */
    private Contracts contracts = new InMemoryContracts(this);

    /**
     * In-memory contributors.
     */
    private Contributors contributors = new InMemoryContributors(this);

    /**
     * In-memory tasks.
     */
    private Tasks tasks = new InMemoryTasks(this);

    /**
     * In-memory resignations.
     */
    private Resignations resignations = new InMemoryResignations(this);

    /**
     * In-memory invoices.
     */
    private Invoices invoices = new InMemoryInvoices(this);

    /**
     * In-memory invoiced tasks.
     */
    private InvoicedTasks invoicedTasks = new InMemoryInvoicedTasks(this);

    /**
     * In-memory payout methods.
     */
    private PayoutMethods payoutMethods = new InMemoryPayoutMethods(this);

    @Override
    public Users users() {
        return this.users;
    }

    @Override
    public ProjectManagers projectManagers() {
        return this.projectManagers;
    }

    @Override
    public Projects projects() {
        return this.projects;
    }

    @Override
    public Wallets wallets() {
        return this.wallets;
    }

    @Override
    public Contracts contracts() {
        return this.contracts;
    }

    @Override
    public Invoices invoices() {
        return this.invoices;
    }

    @Override
    public InvoicedTasks invoicedTasks() {
        return this.invoicedTasks;
    }

    @Override
    public Contributors contributors() {
        return this.contributors;
    }

    @Override
    public Tasks tasks() {
        return this.tasks;
    }

    @Override
    public Resignations resignations() {
        return this.resignations;
    }

    @Override
    public PayoutMethods payoutMethods() {
        return this.payoutMethods;
    }

    @Override
    public PaymentMethods paymentMethods() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public PlatformInvoices platformInvoices() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ApiTokens apiTokens() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Payments payments() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Nothing to close, this is an in-memory storage.
     */
    @Override
    public void close() { }
}
