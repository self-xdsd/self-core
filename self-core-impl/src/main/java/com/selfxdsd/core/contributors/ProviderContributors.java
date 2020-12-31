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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Contributors;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.exceptions.ContributorsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contributors belonging to a Provider. This class <b>just represents</b>
 * the contributors. The actual filtering has to be done in an upper layer.
 * @author criske
 * @version $Id$
 * @since 0.0.23
 */
public final class ProviderContributors extends BasePaged
    implements Contributors {

    /**
     * Provider.
     */
    private final String provider;

    /**
     * The Provider contributors.
     */
    private final Supplier<Stream<Contributor>> contributors;

    /**
     * Self storage, to save new contributors.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param provider Provider.
     * @param contributors The Provider contributors.
     * @param storage Self's storage, to save new contracts.
     */
    public ProviderContributors(
        final String provider,
        final Supplier<Stream<Contributor>> contributors,
        final Storage storage
    ) {
        this(provider, contributors, storage, Page.all());
    }


    /**
     * Constructor.
     * @param provider Provider.
     * @param contributors The Provider contributors.
     * @param storage Self's storage, to save new contracts.
     * @param page Current Page.
     * @checkstyle LineLength (5 lines)
     */
    private ProviderContributors(final String provider,
                                 final Supplier<Stream<Contributor>> contributors,
                                 final Storage storage,
                                 final Page page){
        super(page, () -> (int) contributors.get().count());
        this.provider = provider;
        this.contributors = contributors;
        this.storage = storage;
    }

    @Override
    public Contributor register(
        final String username,
        final String provider
    ) {
        if(!provider.equalsIgnoreCase(this.provider)) {
            throw new ContributorsException.OfProvider.Add(this.provider);
        }
        return this.storage.contributors().register(username, provider);
    }

    @Override
    public Contributor getById(
        final String username,
        final String provider
    ) {
        final Page page = super.current();
        return this.contributors.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(c -> c.username().equalsIgnoreCase(username)
                && c.provider().equalsIgnoreCase(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Contributors ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        if(!this.provider.equalsIgnoreCase(repoProvider)) {
            throw new ContributorsException.OfProvider.List(this.provider);
        }
        final Project project = this.storage
            .projects()
            .getProjectById(repoFullName, repoProvider);
        return new ProjectContributors(
            project,
            () -> this.contributors
                .get()
                .filter(contrib -> StreamSupport
                    .stream(contrib.contracts().spliterator(), false)
                    .anyMatch(contract -> contract.project()
                        .equals(project))),
            this.storage
        );
    }

    @Override
    public Contributors ofProvider(final String provider) {
        if(!this.provider.equalsIgnoreCase(provider)) {
            throw new ContributorsException.OfProvider.List(this.provider);
        }
        return this;
    }

    @Override
    public Contributors page(final Page page) {
        return new ProviderContributors(this.provider,
            this.contributors,
            this.storage,
            page
        );
    }

    @Override
    public Contributor elect(final Task task) {
        throw new ContributorsException.Election();
    }

    @Override
    public Iterator<Contributor> iterator() {
        final Page page = super.current();
        return this.contributors.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .iterator();
    }
}
