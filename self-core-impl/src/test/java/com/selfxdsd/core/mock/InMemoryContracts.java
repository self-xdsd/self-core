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

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-Memory Contracts for testing purposes.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #71:30min Implement method Contracts.addContract(...).
 *  Implement ProjectContracts to replace the anonymous class used
 *  here inside ofProject(...). Once we can test this class, write
 *  some unit tests for it.
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
     * @param storage Parent storage
     */
    public InMemoryContracts(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Contracts ofProject(final int projectId) {
        final List<Contract> ofProject = this.contracts.keySet()
            .stream()
            .filter(key -> key.projectId == projectId)
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new Contracts() {
            @Override
            public Contracts ofProject(final int projectId) {
                return this;
            }

            @Override
            public Contracts ofContributor(final int contributorId) {
                return null;
            }

            @Override
            public Iterator<Contract> iterator() {
                return ofProject.iterator();
            }
        };
    }

    @Override
    public Contracts ofContributor(final int contributorId) {
        final List<Contract> ofContributor = this.contracts.keySet()
            .stream()
            .filter(key -> key.contributorId == contributorId)
            .map(key -> this.contracts.get(key))
            .collect(Collectors.toList());
        return new Contracts() {
            @Override
            public Contracts ofProject(final int projectId) {
                return null;
            }

            @Override
            public Contracts ofContributor(final int contributorId) {
                return this;
            }

            @Override
            public Iterator<Contract> iterator() {
                return ofContributor.iterator();
            }
        };
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.values().iterator();
    }

    /**
     * Contract primary key.
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
