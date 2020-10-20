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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.exceptions.ContractsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.ContributorContracts;
import com.selfxdsd.core.contracts.ProjectContracts;
import com.selfxdsd.core.contracts.StoredContract;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-Memory Contracts for testing purposes.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryContracts implements Contracts {

    /**
     * Contracts "table".
     */
    private final Map<ContractKey, Contract> contracts = new HashMap<>();

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     *
     * @param storage Parent storage
     */
    public InMemoryContracts(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        final ContractKey key = new ContractKey(
            repoFullName, contributorUsername, provider, role
        );
        Contract contract = this.contracts.get(key);
        if(contract == null) {
            final Project project = storage.projects()
                .getProjectById(repoFullName, provider);
            final Contributor contributor = this.storage.contributors()
                .getById(contributorUsername, provider);
            if (project != null && contributor != null) {
                contract = new StoredContract(
                    project,
                    contributor,
                    hourlyRate,
                    role,
                    this.storage
                );
                this.contracts.put(key, contract);
            } else {
                if(project == null) {
                    throw new ContractsException.Single.Add(
                        new Contract.Id(key.repoFullName,
                            key.contributorUsername,
                            key.provider,
                            key.role),
                        "project was not found in storage.");
                }
                if(contributor == null) {
                    throw new ContractsException.Single.Add(
                        new Contract.Id(key.repoFullName,
                            key.contributorUsername,
                            key.provider,
                            key.role),
                        "contributor was not found in storage.");
                }
            }
        } else {
            throw new ContractsException.Single.Add(
                new Contract.Id(key.repoFullName,
                    key.contributorUsername,
                    key.provider,
                    key.role),
                "is already registered.");
        }
        return contract;
    }

    @Override
    public Contract findById(final Contract.Id id) {
        final ContractKey key = new ContractKey(
            id.getRepoFullName(),
            id.getContributorUsername(),
            id.getProvider(),
            id.getRole()
        );
        return contracts.get(key);
    }

    @Override
    public Contract update(
        final Contract contract,
        final BigDecimal hourlyRate
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Contracts ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        final List<Contract> ofProject = this.contracts.keySet()
            .stream()
            .filter(
                key -> {
                    return key.repoFullName.equals(repoFullName)
                        && key.provider.equals(repoProvider);
                }
            )
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ProjectContracts(
            repoFullName, repoProvider, ofProject::stream, this.storage
        );
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        final List<Contract> ofContributor = this.contracts.keySet()
            .stream()
            .filter(
                //@checkstyle LineLength (5 lines)
                key -> {
                    return key.contributorUsername.equals(contributor.username())
                        && key.provider.equals(contributor.provider());
                }
            )
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ContributorContracts(
            contributor, ofContributor::stream, this.storage
        );
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.values().iterator();
    }

    /**
     * Contract primary key.
     */
    private static class ContractKey {

        /**
         * Full name of the Repo represented by the Project.
         */
        private final String repoFullName;
        /**
         * Contributor's username.
         */
        private final String contributorUsername;

        /**
         * Contributor/Project's provider.
         */
        private final String provider;

        /**
         * Contributor's role.
         */
        private final String role;

        /**
         * Constructor.
         *
         * @param repoFullName Fullname of the Repo represented by the project.
         * @param contributorUsername Contributor's username.
         * @param provider Contributor/Project's provider.
         * @param role Contributor's role.
         */
        ContractKey(
            final String repoFullName,
            final String contributorUsername,
            final String provider,
            final String role
        ) {
            this.repoFullName = repoFullName;
            this.contributorUsername = contributorUsername;
            this.provider = provider;
            this.role = role;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final ContractKey key = (ContractKey) object;
            //@checkstyle LineLength (5 lines)
            return this.repoFullName.equals(key.repoFullName)
                && this.contributorUsername.equals(key.contributorUsername)
                && this.provider.equals(key.provider)
                && this.role.equals(key.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.repoFullName,
                this.contributorUsername,
                this.provider,
                this.role
            );
        }
    }
}
