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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * In-Memory Contracts for testing purposes.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @todo #74:30min When Storage API has contributors available, update
 * InMemoryContracts#getContributorById() to use storage. Then update
 * InMemoryContractsTestCase tests to reflect that.
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


    /**
     * Adds a contract based on valid projectId and contributorId.
     * If either one of ids is invalid, an exception will be thrown.
     *
     * @param projectId Valid project id
     * @param contributorId Valid contributor id
     * @param hourlyRate Contract's hourly rate
     * @param role Contract's role
     * @return Contract
     * @checkstyle ParameterNumber (10 lines)
     */
    public Contract addContract(final int projectId,
                                final int contributorId,
                                final BigDecimal hourlyRate,
                                final String role) {
        final ContractKey key = new ContractKey(projectId, contributorId);
        Contract contract = contracts.get(key);
        if(contract == null) {
            final Project project = getProjectById(projectId);
            final Contributor contributor = getContributorById(contributorId);
            if (project != null && contributor != null) {
                contract = new Contract() {
                    @Override
                    public Project project() {
                        return project;
                    }

                    @Override
                    public Contributor contributor() {
                        return contributor;
                    }

                    @Override
                    public BigDecimal hourlyRate() {
                        return hourlyRate;
                    }

                    @Override
                    public String role() {
                        return role;
                    }
                };
                contracts.put(key, contract);
            } else {
                throw new IllegalStateException("Contract was not created:"
                    + " project or contributor was not found in storage");
            }
        }
        return contract;
    }

    /**
     * Get a project from storage by id,
     * or null if project is not found.
     *
     * @param projectId Project id
     * @return Found Project or null
     */
    private Project getProjectById(final int projectId) {
        return StreamSupport
            .stream(storage.projects().spliterator(), false)
            .filter(p -> p.projectId() == projectId)
            .findFirst()
            .orElse(null);
    }
    /**
     * Get a contributor from storage by id,
     * or null if contributor is not found.
     *
     * @param contributorId Project id
     * @return Found Project or null
     */
    private Contributor getContributorById(final int contributorId) {
        //placeholder until there is a way to get contributors from storage API
        return new Contributor() {
            @Override
            public int contributorId() {
                return contributorId;
            }

            @Override
            public Contracts contracts() {
                return ofContributor(contributorId);
            }
        };
    }

    @Override
    public Contracts ofProject(final int projectId) {
        final List<Contract> ofProject = this.contracts.keySet()
            .stream()
            .filter(key -> key.projectId == projectId)
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ProjectContracts(projectId, ofProject);
    }

    @Override
    public Contracts ofContributor(final int contributorId) {
        final List<Contract> ofContributor = this.contracts.keySet()
            .stream()
            .filter(key -> key.contributorId == contributorId)
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new ContributorContracts(contributorId, ofContributor);
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.values().iterator();
    }

    /**
     * Contract primary key.
     *
     * @checkstyle VisibilityModifier (50 lines)
     */
    private static class ContractKey {

        /**
         * Project.
         */
        final int projectId;
        /**
         * Contributor.
         */
        final int contributorId;

        /**
         * Constructor.
         *
         * @param projectId Project ID.
         * @param contributorId Contributor ID.
         */
        ContractKey(final int projectId, final int contributorId) {
            this.projectId = projectId;
            this.contributorId = contributorId;
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
            return this.projectId == contractKey.projectId
                && this.contributorId == contractKey.contributorId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.projectId, this.contributorId);
        }
    }
}
