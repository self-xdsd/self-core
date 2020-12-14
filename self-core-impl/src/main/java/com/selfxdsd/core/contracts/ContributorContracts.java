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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.exceptions.ContractsException;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Contracts belonging to a Contributor. Pay attention:
 * this class <b>just represents</b> the contracts.
 * The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorContracts implements Contracts {

    /**
     * The Contributor.
     */
    private final Contributor contributor;

    /**
     * The contributor's contracts.
     */
    private final Supplier<Stream<Contract>> contracts;

    /**
     * Self storage, to save new contracts.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param contributor Contributor.
     * @param contracts Contributor's contracts.
     * @param storage Self's storage, to save new contracts.
     */
    public ContributorContracts(
        final Contributor contributor,
        final Supplier<Stream<Contract>> contracts,
        final Storage storage
    ) {
        this.contributor = contributor;
        this.contracts = contracts;
        this.storage = storage;
    }

    @Override
    public Contracts ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        final Supplier<Stream<Contract>> ofProject = () -> this.contracts
            .get()
            .filter(
                contract -> {
                    final Project project = contract.project();
                    //LineLength (1 line)
                    return project.repoFullName().equalsIgnoreCase(repoFullName)
                        && project.provider().equalsIgnoreCase(repoProvider);
                }
            );
        return new ProjectContracts(
            repoFullName, repoProvider, ofProject, this.storage
        );
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        if (this.isContributor(contributor)) {
            return this;
        }
        throw new ContractsException.OfContributor.List(this.contributor);
    }

    @Override
    public Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        if (!this.isContributor(contributorUsername, provider)) {
            throw new ContractsException.OfContributor.Add(this.contributor);
        }
        return this.storage.contracts().addContract(
            repoFullName,
            this.contributor.username(),
            this.contributor.provider(),
            hourlyRate,
            role
        );
    }

    @Override
    public Contract findById(final Contract.Id id) {
        return this.contracts.get()
            .filter(c -> new Contract.Id(c.project().repoFullName(),
                c.contributor().username(),
                c.project().provider(),
                c.role()).equals(id)).findFirst()
            .orElse(null);
    }

    @Override
    public Contract update(
        final Contract contract,
        final BigDecimal hourlyRate
    ) {
        if (!this.belongsToContributor(contract)) {
            throw new ContractsException.OfContributor.Update(
                this.contributor
            );
        }
        return this.storage.contracts().update(contract, hourlyRate);
    }

    @Override
    public Contract markForRemoval(
        final Contract contract,
        final LocalDateTime time
    ) {
        if (!this.belongsToContributor(contract)) {
            throw new ContractsException.OfContributor.Delete(
                this.contributor
            );
        }
        return this.storage.contracts()
            .markForRemoval(contract, time);
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.get().iterator();
    }

    @Override
    public void remove(final Contract contract) {
        if (!this.belongsToContributor(contract)) {
            throw new ContractsException.OfContributor.Delete(
                this.contributor
            );
        }
        this.storage.contracts().remove(contract);
    }

    /**
     * Check if contract belongs to the contributor.
     * @param contract A contract
     * @return True if belongs
     */
    private boolean belongsToContributor(final Contract contract) {
        final String username = contract.contractId().getContributorUsername();
        final String provider = contract.contractId().getProvider();
        return isContributor(username, provider);
    }

    /**
     * Check contributors username & provider.
     * @param username A username to check.
     * @param provider A provider to check.
     * @return True if match
     */
    private boolean isContributor(
        final String username,
        final String provider
    ) {
        return this.contributor.username().equalsIgnoreCase(username)
            && this.contributor.provider().equalsIgnoreCase(provider);
    }

    /**
     * Check if its the same contributor.
     * @param contributor A contributor to check.
     * @return True if they match
     */
    private boolean isContributor(final Contributor contributor) {
        return this.isContributor(
            contributor.username(),
            contributor.provider()
        );
    }
}
