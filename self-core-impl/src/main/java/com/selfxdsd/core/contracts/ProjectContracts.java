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
import com.selfxdsd.api.exceptions.ContractsException;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contracts belonging to a Project. Pay attention:
 * this class <b>just represents</b> the contracts.
 * The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectContracts implements Contracts {

    /**
     * Full name of the Repo represented by the Project.
     */
    private final String repoFullName;

    /**
     * Provider of the Repo represented by the Project.
     */
    private final String provider;

    /**
     * The project's contracts.
     */
    private final Supplier<Stream<Contract>> contracts;

    /**
     * Self storage, to save new contracts.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param repoFullName Full name of the Repo represented by the Project.
     * @param provider Provider of the Repo represented by the Project.
     * @param contracts Project's contracts.
     * @param storage Self's storage, to save new contracts.
     */
    public ProjectContracts(
        final String repoFullName,
        final String provider,
        final Supplier<Stream<Contract>> contracts,
        final Storage storage
    ) {
        this.repoFullName = repoFullName;
        this.provider = provider;
        this.contracts = contracts;
        this.storage = storage;
    }

    @Override
    public Contracts ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        if(this.repoFullName.equalsIgnoreCase(repoFullName)
            && this.provider.equalsIgnoreCase(repoProvider)
        ) {
            return this;
        }
        throw new ContractsException.OfProject.List(this.repoFullName,
            this.provider);
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        final List<Contract> ofContributor = this.contracts
            .get()
            .filter(contract -> contract
                .contributor()
                .username()
                .equalsIgnoreCase(contributor.username())
                && contract
                .contributor()
                .provider()
                .equalsIgnoreCase(contributor.provider()))
            .collect(Collectors.toList());
        return new ContributorContracts(
            contributor, ofContributor::stream, this.storage
        );
    }

    @Override
    public Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        if(!this.repoFullName.equalsIgnoreCase(repoFullName)
            || !this.provider.equalsIgnoreCase(provider)
        ) {
            throw new ContractsException.OfProject.Add(this.repoFullName,
                this.provider);
        } else {
            final Contract registered = this.storage.contracts().addContract(
                this.repoFullName,
                contributorUsername,
                this.provider,
                hourlyRate,
                role
            );
            return registered;
        }
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
        final Contract.Id cid = contract.contractId();
        if(!this.repoFullName.equalsIgnoreCase(cid.getRepoFullName())
            || !this.provider.equalsIgnoreCase(cid.getProvider())
        ) {
            throw new ContractsException.OfProject.Update(
                this.repoFullName,
                this.provider
            );
        } else {
            return this.storage.contracts().update(contract, hourlyRate);
        }
    }

    @Override
    public Contract markForRemoval(
        final Contract contract,
        final LocalDateTime time
    ) {
        final Contract.Id cid = contract.contractId();
        if(!this.repoFullName.equalsIgnoreCase(cid.getRepoFullName())
            || !this.provider.equalsIgnoreCase(cid.getProvider())
        ) {
            throw new ContractsException.OfProject.Delete(
                this.repoFullName,
                this.provider
            );
        } else {
            return this.storage.contracts()
                .markForRemoval(contract, time);
        }
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.get().iterator();
    }

    @Override
    public void remove(final Contract contract) {
        final Contract.Id cid = contract.contractId();
        if (
            !this.repoFullName.equalsIgnoreCase(cid.getRepoFullName())
                || !this.provider.equalsIgnoreCase(cid.getProvider())
        ) {
            throw new ContractsException.OfProject.Delete(
                this.repoFullName,
                this.provider
            );
        } else {
            this.storage.contracts().remove(contract);
        }
    }
}
