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

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Contributors;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contributors.StoredContributor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory contributors for test purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 **/

public final class InMemoryContributors implements Contributors {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Contributors "table".
     */
    private final Map<ContributorKey, Contributor> table = new HashMap<>();

    /**
     * Ctor.
     * @param storage Parent storage.
     */
    public InMemoryContributors(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Contributor register(final String username, final String provider) {
        final ContributorKey key = new ContributorKey(username, provider);
        final Contributor contributor = this.table.get(key);
        if(contributor != null) {
            throw new IllegalArgumentException("Contributor already exists.");
        } else {
            final Contributor newContributor = new StoredContributor(
                username, provider, this.storage
            );
            this.table.put(key, newContributor);
            return newContributor;
        }
    }

    @Override
    public Contributor getById(
        final String username, final String provider
    ) {
        return this.table.get(new ContributorKey(username, provider));
    }

    @Override
    public Contributors ofProject(final int projectId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterator<Contributor> iterator() {
        return this.table.values().iterator();
    }

    /**
     * Contributor primary key.
     *
     * @checkstyle VisibilityModifier (50 lines)
     */
    private static class ContributorKey {

        /**
         * Contributor's username.
         */
        private final String contributorUsername;

        /**
         * Contributor's provider.
         */
        private final String contributorProvider;

        /**
         * Constructor.
         *
         * @param contributorUsername Contributor's username.
         * @param contributorProvider Contributor's provider.
         * */
        ContributorKey(
            final String contributorUsername,
            final String contributorProvider
        ) {
            this.contributorUsername = contributorUsername;
            this.contributorProvider = contributorProvider;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final InMemoryContributors.ContributorKey contractKey =
                (InMemoryContributors.ContributorKey) object;
            //@checkstyle LineLength (5 lines)
            return this.contributorUsername.equals(contractKey.contributorUsername)
                && this.contributorProvider.equals(contractKey.contributorProvider);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.contributorUsername,
                this.contributorProvider
            );
        }
    }
}
