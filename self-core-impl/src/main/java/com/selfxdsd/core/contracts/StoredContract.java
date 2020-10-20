/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.contracts;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;

/**
 * A Contract stored in self.
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredContract implements Contract {

    /**
     * Contract id.
     */
    private final Contract.Id id;

    /**
     * Project of this Contract.
     */
    private final Project project;

    /**
     * Contributor of this Contract.
     */
    private final Contributor contributor;

    /**
     * Project ID.
     */
    private final BigDecimal hourlyRate;

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param id The contract's ID.
     * @param hourlyRate The hourly rate.
     * @param storage The self's storage
     */
    public StoredContract(
        final Contract.Id id,
        final BigDecimal hourlyRate,
        final Storage storage
    ) {
        this.id = id;
        this.hourlyRate = hourlyRate;
        this.storage = storage;
        this.project = null;
        this.contributor = null;
    }


    /**
     * Constructor.
     * @param project The contract's project.
     * @param contributor The contract's contributor.
     * @param hourlyRate The hourly rate.
     * @param role The role.
     * @param storage The self's storage
     */
    public StoredContract(
        final Project project,
        final Contributor contributor,
        final BigDecimal hourlyRate,
        final String role,
        final Storage storage
    ) {
        this.project = project;
        this.contributor = contributor;
        this.hourlyRate = hourlyRate;
        this.storage = storage;
        this.id = new Contract.Id(
            project.repoFullName(),
            contributor.username(),
            project.provider(),
            role
        );
    }

    @Override
    public Id contractId() {
        return this.id;
    }

    /**
     * The Project.
     *
     * @return Project.
     */
    @Override
    public Project project() {
        final Project proj;
        if(this.project == null) {
            proj = this.storage.projects().getProjectById(
                this.id.getRepoFullName(), this.id.getProvider()
            );
        } else {
            proj = this.project;
        }
        return proj;
    }

    /**
     * The Contributor.
     *
     * @return Contributor.
     */
    @Override
    public Contributor contributor() {
        final Contributor cont;
        if(this.contributor == null) {
            cont = this.storage.contributors().getById(
                this.id.getContributorUsername(), this.id.getProvider()
            );
        } else {
            cont = this.contributor;
        }
        return cont;
    }

    /**
     * The Contributor's hourly rate in cents.
     *
     * @return BigDecimal.
     */
    @Override
    public BigDecimal hourlyRate() {
        return this.hourlyRate;
    }

    /**
     * The Contributor's role (DEV, QA, ARCH etc).
     *
     * @return String.
     */
    @Override
    public String role() {
        return this.id.getRole();
    }

    /**
     * Invoices for this contract, active or inactive.
     * <br>
     * Note that a contract must have at most one active Invoice.
     * @return Iterable of Invoice.
     */
    @Override
    public Invoices invoices() {
        return storage.invoices().ofContract(this.id);
    }

    /**
     * The tasks assigned to the Contract.
     * @return Tasks.
     */
    @Override
    public Tasks tasks() {
        return storage.tasks().ofContract(this.id);
    }

    @Override
    public BigDecimal value() {
        BigDecimal total = BigDecimal.valueOf(0);
        final BigDecimal commission = this.project()
            .projectManager()
            .commission();
        for(final Task task : this.tasks()) {
            total = total
                .add(task.value())
                .add(commission);
        }
        total = total.add(this.invoices().active().totalAmount());
        return total;
    }

    @Override
    public Contract update(final BigDecimal hourlyRate) {
        return this.storage.contracts().update(this, hourlyRate);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Contract)) {
            return false;
        }
        final Contract other = (Contract) obj;
        final Contract.Id otherId = new Contract.Id(
            other.project().repoFullName(),
            other.contributor().username(),
            other.project().provider(),
            other.role()
        );
        return this.id.equals(otherId);
    }
}
