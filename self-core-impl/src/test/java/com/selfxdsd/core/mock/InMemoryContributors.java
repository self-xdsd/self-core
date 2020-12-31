/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
import com.selfxdsd.api.exceptions.ContributorsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;
import com.selfxdsd.core.contributors.ProjectContributors;
import com.selfxdsd.core.contributors.ProviderContributors;
import com.selfxdsd.core.contributors.StoredContributor;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * In-memory contributors for test purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle ReturnCount (400 lines)
 * @since 0.0.1
 */
public final class InMemoryContributors extends BasePaged
    implements Contributors {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Contributors "table".
     */
    private final Map<ContributorKey, Contributor> table;

    /**
     * Ctor.
     *
     * @param storage Parent storage.
     */
    public InMemoryContributors(final Storage storage) {
        this(new TreeMap<>(Comparator.comparing(ContributorKey::toString)),
            storage,
            Page.all());
    }

    /**
     * Ctor.
     * @param table Contributors "table".
     * @param storage Parent storage.
     * @param page Current Page.
     */
    private InMemoryContributors(final Map<ContributorKey, Contributor> table,
                                 final Storage storage,
                                 final Page page) {
        super(page, table::size);
        this.table = table;
        this.storage = storage;
    }

    @Override
    public Contributor register(final String username, final String provider) {
        final ContributorKey key = new ContributorKey(username, provider);
        final Contributor contributor = this.table.get(key);
        if(contributor != null) {
            throw new ContributorsException
                .Single.Add(contributor.username(), contributor.provider());
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
    public Contributors page(final Page page) {
        return new InMemoryContributors(this.table, this.storage, page);
    }

    @Override
    public Contributors ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        final Page page = super.current();
        final Supplier<Stream<Contributor>> found = () -> this.table
            .values()
            .stream()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(contributor -> {
                for (final Contract ctc : contributor.contracts()) {
                    final Project prj = ctc.project();
                    if (prj.repoFullName().equals(repoFullName)
                        && prj.provider().equals(repoProvider)) {
                        return true;
                    }
                }
                return false;
            });
        return new ProjectContributors(
            this.storage.projects().getProjectById(
                repoFullName,
                repoProvider
            ),
            found, this.storage
        );
    }

    @Override
    public Contributors ofProvider(final String provider) {
        final Page page = super.current();
        final Supplier<Stream<Contributor>> ofProvider = () -> this.table
            .values()
            .stream()
            .filter(c -> c.provider().equals(provider))
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize());
        return new ProviderContributors(provider, ofProvider, this.storage);
    }

    @Override
    public Contributor elect(final Task task) {
        throw new ContributorsException.Election();
    }

    @Override
    public Iterator<Contributor> iterator() {
        throw new ContributorsException.List();
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
         */
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

        @Override
        public String toString() {
            return contributorUsername + "-" + contributorProvider;
        }
    }
}
