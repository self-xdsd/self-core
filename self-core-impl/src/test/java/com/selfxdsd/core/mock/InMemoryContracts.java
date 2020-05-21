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

import java.math.BigDecimal;
import java.util.*;

/**
 * In-Memory contracts for testing purposes.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryContracts implements Contracts {
    /**
     * In memory holder of contracts.
     */
    private final Set<Contract> contracts = new HashSet<>();
    /**
     * In memory holder of projects.
     */
    private final Set<ProjectInternal> projects = new HashSet<>();
    /**
     * In memory holder of contributors.
     */
    private final Set<ContributorInternal> contributors = new HashSet<>();
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
    public Contracts ofProject(final int projectId) {
        return projects.stream()
            .filter(p -> p.projectId() == projectId)
            .map(Project::contracts)
            .findFirst().orElse(new Contracts.Empty());
    }

    @Override
    public Iterator<Contract> iterator() {
        return contracts.iterator();
    }

    /**
     * Creates and adds a Contract based on a project id and contributor id.
     *
     * @param projectId Project's id
     * @param contributorId Contributor's id
     */
    public void addContract(final int projectId, final int contributorId) {
        final boolean contractExists = contracts.stream()
            .anyMatch(c -> c.contributor().contributorId() == contributorId
                && c.project().projectId() == projectId);
        if (!contractExists) {
            final ProjectInternal project = findOrCreateProject(projectId);
            final ContributorInternal contributor =
                findOrCreateContributor(contributorId);
            final ContractInternal contract =
                new ContractInternal(project, contributor);
            //store the contract
            contracts.add(contract);
            //link this contract to its project and contributor
            project.addContract(contract);
            contributor.addContract(contract);
        }
    }

    /**
     * Returns a project by id from the projects set.
     * If the project is not found, a project will be created and added to set.
     *
     * @param projectId Project's
     * @return Internal representation of a Project
     */
    private ProjectInternal findOrCreateProject(final int projectId) {
        ProjectInternal project = projects.stream()
            .filter(p -> p.projectId() == projectId)
            .findFirst()
            .orElse(null);
        if (project == null) {
            project = new ProjectInternal(projectId, this);
            projects.add(project);
        }
        return project;
    }

    /**
     * Returns a contributor by id from the contributors set.
     * If the contributor is not found, a contributor will
     * be created and added to set.
     *
     * @param cbId Contributor's id
     * @return Internal representation of a Contributor
     */
    private ContributorInternal findOrCreateContributor(final int cbId) {
        ContributorInternal contributor = contributors.stream()
            .filter(c -> c.contributorId() == cbId)
            .findFirst()
            .orElse(null);
        if (contributor == null) {
            contributor = new ContributorInternal(cbId, this);
            contributors.add(contributor);
        }
        return contributor;
    }


    /**
     * In memory implementation of a {@link Project}.
     * <br>
     * Aids {@link InMemoryContracts}
     * <br>
     * Has capability to link a contract.
     * See: {@link ProjectInternal#addContract(Contract)}.
     */
    private static final class ProjectInternal implements Project {

        /**
         * Project's id.
         */
        private final int id;
        /**
         * Global contracts. Access to {@link InMemoryContracts }
         * Grants access to any arbitrary project's contracts using
         * {@link Contracts#ofProject(int) from any point of the object graph.
         */
        private final Contracts rootContracts;

        /**
         * Contracts associated to this project.
         */
        private final Set<Contract> ownedContracts = new HashSet<>();

        /**
         * Constructor.
         *
         * @param id Project id
         * @param rootContracts Global contracts.
         *                      Access to {@link InMemoryContracts }
         */
        ProjectInternal(final int id, final Contracts rootContracts) {
            this.id = id;
            this.rootContracts = rootContracts;
        }

        /**
         * Adds a contract to ownedContracts.
         *
         * @param contract Contract to be added.
         */
        void addContract(final Contract contract) {
            ownedContracts.add(contract);
        }

        @Override
        public int projectId() {
            return id;
        }

        @Override
        public ProjectManager projectManager() {
            return null;
        }

        @Override
        public Repo repo() {
            return null;
        }

        @Override
        public Contracts contracts() {
            return new Contracts() {
                @Override
                public Contracts ofProject(final int projectId) {
                    return rootContracts.ofProject(projectId);
                }

                @Override
                public Iterator<Contract> iterator() {
                    return ownedContracts.iterator();
                }
            };
        }

        @Override
        public Repo deactivate() {
            return null;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Project)) {
                return false;
            }
            final Project other = (Project) obj;
            return other.projectId() == this.id;
        }
    }

    /**
     * In memory implementation of a {@link Contributor}.
     * <br>
     * Aids {@link InMemoryContracts}
     * <br>
     * Has capability to link a contract.
     * See: {@link ContributorInternal#addContract(Contract)}.
     */
    private static final class ContributorInternal implements Contributor {

        /**
         * Contributors's id.
         */
        private final int id;
        /**
         * Global contracts. Access to {@link InMemoryContracts }
         * Grants access to any arbitrary project's contracts using
         * {@link Contracts#ofProject(int) from any point of the object graph.
         */
        private final Contracts rootContracts;
        /**
         * Contracts associated to this contributor.
         */
        private final Set<Contract> ownedContracts = new HashSet<>();

        /**
         * Constructor.
         *
         * @param id Project id
         * @param rootContracts Global contracts.
         *                      Access to {@link InMemoryContracts }
         */
        private ContributorInternal(final int id,
                                    final Contracts rootContracts) {
            this.rootContracts = rootContracts;
            this.id = id;
        }

        @Override
        public int contributorId() {
            return id;
        }

        @Override
        public Contracts contracts() {
            return new Contracts() {
                @Override
                public Contracts ofProject(final int projectId) {
                    return rootContracts.ofProject(projectId);
                }

                @Override
                public Iterator<Contract> iterator() {
                    return ownedContracts.iterator();
                }
            };
        }

        /**
         * Adds a contract to ownedContracts.
         *
         * @param contract Contract to be added.
         */
        void addContract(final Contract contract) {
            ownedContracts.add(contract);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Contributor)) {
                return false;
            }
            final Contributor other = (Contributor) obj;
            return other.contributorId() == this.id;
        }
    }

    /**
     * In memory implementation of a {@link Contract}.
     * <br>
     * It's following the specification: an association (many-to-many) between a
     * {@link Project} and a {@link Contributor}.
     * The primary key should be the project id and the contributor id.
     * <br>
     * This specification is reflected in the equals and hashcode class contract
     *
     * @author criske
     * @version $Id$
     * @since 0.0.1
     */
    private static final class ContractInternal implements Contract {
        /**
         * The project of this contract.
         */
        private final Project project;

        /**
         * The contributor of this contract.
         */
        private final Contributor contributor;

        /**
         * Constructor.
         *
         * @param project The given project
         * @param contributor The given contributor
         */
        ContractInternal(final Project project, final Contributor contributor) {
            this.project = project;
            this.contributor = contributor;
        }


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
            return BigDecimal.ZERO;
        }

        @Override
        public String role() {
            return "";
        }

        @Override
        public int hashCode() {
            return Objects.hash(project.projectId(),
                contributor.contributorId());
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Contract)) {
                return false;
            }
            final Contract other = (Contract) obj;
            final int thisPrjId = project().projectId();
            final int thatPrjId = other.project().projectId();
            final boolean projectEq = thisPrjId == thatPrjId;
            final int thisCbId = contributor().contributorId();
            final int thatCbId = other.contributor().contributorId();
            final boolean contributorEq = thisCbId == thatCbId;
            return projectEq && contributorEq;
        }
    }

}
