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

import com.selfxdsd.api.*;
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
        final int projectId,
        final String contributorUsername,
        final String contributorProvider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        final ContractKey key = new ContractKey(
            projectId, contributorUsername, contributorProvider, role
        );
        Contract contract = this.contracts.get(key);
        if(contract == null) {
            final Project project = storage.projects()
                    .getProjectById(projectId);
            final Contributor contributor = this.storage.contributors()
                .getById(contributorUsername, contributorProvider);
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
                    throw new IllegalStateException(
                        "Contract was not created:"
                      + " project was not found in storage"
                    );
                }
                if(contributor == null) {
                    throw new IllegalStateException(
                        "Contract was not created:"
                      + " contributor was not found in storage"
                    );
                }
            }
        } else {
            throw new IllegalStateException(
                "The specified Contract is already registered."
            );
        }
        return contract;
    }

    @Override
    public Contracts ofProject(final int projectId) {
        final List<Contract> ofProject = this.contracts.keySet()
            .stream()
            .filter(key -> key.projectId == projectId)
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ProjectContracts(projectId, ofProject, this.storage);
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        final List<Contract> ofContributor = this.contracts.keySet()
            .stream()
            .filter(
                //@checkstyle LineLength (5 lines)
                key -> {
                    return key.contributorUsername.equals(contributor.username())
                        && key.contributorProvider.equals(contributor.provider());
                }
            )
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ContributorContracts(
            contributor, ofContributor, this.storage
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
         * Project.
         */
        private final int projectId;
        /**
         * Contributor's username.
         */
        private final String contributorUsername;

        /**
         * Contributor's provider.
         */
        private final String contributorProvider;

        /**
         * Contributor's role.
         */
        private final String role;

        /**
         * Constructor.
         *
         * @param projectId Project ID.
         * @param contributorUsername Contributor's username.
         * @param contributorProvider Contributor's provider.
         * @param role Contributor's role.
         */
        ContractKey(
            final int projectId,
            final String contributorUsername,
            final String contributorProvider,
            final String role
        ) {
            this.projectId = projectId;
            this.contributorUsername = contributorUsername;
            this.contributorProvider = contributorProvider;
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
            final InMemoryContracts.ContractKey contractKey =
                (InMemoryContracts.ContractKey) object;
            //@checkstyle LineLength (5 lines)
            return this.projectId == contractKey.projectId
                && this.contributorUsername.equals(contractKey.contributorUsername)
                && this.contributorProvider.equals(contractKey.contributorProvider)
                && this.role.equals(contractKey.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.projectId,
                this.contributorUsername,
                this.contributorProvider,
                this.role
            );
        }
    }
}
