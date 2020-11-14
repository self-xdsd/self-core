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
package com.selfxdsd.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contracts.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (200 lines)
 */
public interface Contracts extends Iterable<Contract> {

    /**
     * Get the Contracts belonging to a given Project.
     * @param repoFullName Full name of the Repo that the Project represents.
     * @param repoProvider Provider of the Repo that the Project represents.
     * @return Contracts.
     */
    Contracts ofProject(
        final String repoFullName,
        final String repoProvider
    );

    /**
     * Get the Contracts belonging to a Contributor.
     * @param contributor Contributor.
     * @return Contracts.
     */
    Contracts ofContributor(final Contributor contributor);

    /**
     * Adds a contract based on valid projectId and contributorId.
     * If either one of ids is invalid, an exception will be thrown.
     *
     * @param repoFullName Full name of the Repo the Project represents.
     * @param contributorUsername Contributor's username.
     * @param provider Contributor/Project's provider.
     * @param hourlyRate Contract's hourly rate
     * @param role Contract's role
     * @return Contract
     */
    Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    );

    /**
     * Remove specific contract.
     *
     * @param contract Contract to remove
     */
    void remove(final Contract contract);

    /**
     * Finds a contract by id.
     * @param id Contract's id.
     * @return Contract or null if not found.
     */
    Contract findById(Contract.Id id);

    /**
     * Update a Contract.
     * @param contract Contract to be updated.
     * @param hourlyRate New hourly rate of the Contract.
     * @return Contract. Updated contract.
     */
    Contract update(final Contract contract, final BigDecimal hourlyRate);

    /**
     * Mark a Contract for removal.
     * @param contract Contract to be marked for removal.
     * @param time LocalDateTime when the Contract is marked.
     * @return Contract. Contract marked for removal.
     */
    Contract markForRemoval(final Contract contract, final LocalDateTime time);

    /**
     * Empty Contracts. Return an instance of this when you cannot
     * find the Contracts you are looking for.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.1
     */
    final class Empty implements Contracts {
        @Override
        public Contracts ofProject(
            final String repoFullName,
            final String repoProvider
        ) {
            return new Empty();
        }

        @Override
        public Contracts ofContributor(final Contributor contributor) {
            return new Empty();
        }

        @Override
        public Contract addContract(
            final String repoFullName,
            final String contributorUsername,
            final String provider,
            final BigDecimal hourlyRate,
            final String role
        ) {
            throw new UnsupportedOperationException(
                "These are Empty Contracts, you cannot add one here."
            );
        }

        @Override
        public Contract findById(final Contract.Id id) {
            return null;
        }

        @Override
        public Contract update(
            final Contract contract,
            final BigDecimal hourlyRate
        ) {
            throw new UnsupportedOperationException(
                "These are Empty Contracts, you cannot update one here."
            );
        }

        @Override
        public Contract markForRemoval(
            final Contract contract,
            final LocalDateTime time
        ) {
            throw new UnsupportedOperationException(
                "These are Empty Contracts, you cannot "
                + "mark one for removal here."
            );
        }

        @Override
        public Iterator<Contract> iterator() {
            return new ArrayList<Contract>().iterator();
        }

        @Override
        public void remove(final Contract contract) {
            throw new UnsupportedOperationException(
                "These are Empty Contracts, you cannot remove from it."
            );
        }
    }
}
