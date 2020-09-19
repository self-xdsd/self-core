/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permission is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.ContributorsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.ContributorContracts;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link ProviderContributors}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.23
 */
public final class ProviderContributorsTestCase {

    /**
     * ProviderContributors should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(contributors, Matchers.iterableWithSize(3));
    }

    /**
     * Returns null when the specified Contributor is not found.
     */
    @Test
    public void getByIdFindsNothing() {
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.getById("george", Provider.Names.GITHUB),
            Matchers.nullValue()
        );
    }

    /**
     * Returns the found Contributor.
     */
    @Test
    public void getByIdFindReturnsFound() {
        final Contributor mihai = Mockito.mock(Contributor.class);
        Mockito.when(mihai.username()).thenReturn("mihai");
        Mockito.when(mihai.provider()).thenReturn(Provider.Names.GITHUB);
        final Contributor vlad = Mockito.mock(Contributor.class);
        Mockito.when(vlad.username()).thenReturn("vlad");
        Mockito.when(vlad.provider()).thenReturn(Provider.Names.GITHUB);

        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(vlad, mihai)::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.getById("mihai", Provider.Names.GITHUB),
            Matchers.is(mihai)
        );
    }

    /**
     * Method ofProvider should return the same instance if the provider
     * is a match.
     */
    @Test
    public void ofProviderReturnsSelfIfSameProvider() {
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.ofProvider(Provider.Names.GITHUB),
            Matchers.is(contributors)
        );
    }

    /**
     * Method ofProvider should complain if the ID of another provider
     * is given as input.
     */
    @Test(expected = ContributorsException.OfProvider.List.class)
    public void ofProviderComplainsIfDifferentProvider() {
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.ofProvider(Provider.Names.GITLAB),
            Matchers.is(contributors)
        );
    }

    /**
     * We should only be able to register contributors from the same provider.
     */
    @Test(expected = ContributorsException.OfProvider.Add.class)
    public void registerComplainsWhenDiffProvider() {
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        contributors.register("mihai", Provider.Names.GITLAB);
    }

    /**
     * If the contributor is already registered, just return it.
     */
    @Test
    public void contributorAlreadyRegistered() {
        final Contributor vlad = Mockito.mock(Contributor.class);
        Mockito.when(vlad.username()).thenReturn("vlad");
        Mockito.when(vlad.provider()).thenReturn(Provider.Names.GITHUB);
        final Contributors contributors = new ProjectContributors(
            this.mockProject(
                "john/test",
                Provider.Names.GITHUB,
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(1000)
            ),
            List.of(vlad)::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.register("vlad", Provider.Names.GITHUB),
            Matchers.is(vlad)
        );
    }

    /**
     * A new contributor is registered if is part of ProviderContributors
     * provider.
     */
    @Test
    public void registersNewContributor() {
        final Contributors allContributors = Mockito.mock(Contributors.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contributors()).thenReturn(allContributors);
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            Stream::empty,
            storage
        );
        contributors.register("mihai", Provider.Names.GITHUB);
        Mockito.verify(storage.contributors())
            .register("mihai", Provider.Names.GITHUB);
    }

    /**
     * ProviderContributors.ofProject(...) returns ProjectContributors.
     */
    @Test
    public void canBeIteratedByOfProject(){
        final Project project = this.mockProject(
            "john/test",
            Provider.Names.GITHUB,
            BigDecimal.valueOf(100000),
            BigDecimal.valueOf(1000)
        );
        final Storage storage = Mockito.mock(Storage.class);
        final Projects allProjects = Mockito.mock(Projects.class);
        Mockito.when(allProjects.getProjectById("john/test",
            Provider.Names.GITHUB)).thenReturn(project);
        Mockito.when(storage.projects()).thenReturn(allProjects);

        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITHUB,
            List.of(
                this.mockContributor("mihai", BigDecimal.valueOf(10000),
                    project, "DEV", "REV", "QA"),
                this.mockContributor("vlad", BigDecimal.valueOf(10000),
                    project, "DEV"),
                this.mockContributor("mary", BigDecimal.valueOf(10000),
                    project, "REV", "QA"),
                this.mockContributor("george", BigDecimal.valueOf(10000),
                    project, "DEV", "ARCH"),
                this.mockContributor("karen", BigDecimal.valueOf(10000),
                    project, "DEV")
            )::stream,
            storage
        );

        final Contributors ofProject = contributors
            .ofProject("john/test", Provider.Names.GITHUB);
        MatcherAssert.assertThat(ofProject, Matchers.iterableWithSize(5));
    }

    /**
     * ProviderContributors.ofProject(...) throws if provider is different
     * then ProviderContributors internal provider.
     */
    @Test(expected = ContributorsException.OfProvider.List.class)
    public void ofProjectComplainsIfProviderIsDifferent(){
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITLAB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        contributors.ofProject("john/test", Provider.Names.GITHUB);
    }

    /**
     * Elect(...) will throw Self Exception.
     */
    @Test(expected = ContributorsException.Election.class)
    public void electNotAllowed(){
        final Contributors contributors = new ProviderContributors(
            Provider.Names.GITLAB,
            List.of(Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class))::stream,
            Mockito.mock(Storage.class)
        );
        contributors.elect(Mockito.mock(Task.class));
    }

    /**
     * Mock a Contributor.
     *
     * @param username Username.
     * @param hourlyRate Hourly rate.
     * @param project Contributor's Project.
     * @param roles Roles.
     * @return Contributor.
     */
    public Contributor mockContributor(
        final String username,
        final BigDecimal hourlyRate,
        final Project project,
        final String... roles
    ) {
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.username()).thenReturn(username);

        final List<Contract> contracts = new ArrayList<>();
        for (final String role : roles) {
            final Contract mock = Mockito.mock(Contract.class);
            Mockito.when(mock.role()).thenReturn(role);
            Mockito.when(mock.hourlyRate()).thenReturn(hourlyRate);
            Mockito.when(mock.project()).thenReturn(project);
            contracts.add(mock);
        }

        Mockito.when(contributor.contracts()).thenReturn(
            new ContributorContracts(
                contributor,
                contracts::stream,
                Mockito.mock(Storage.class))
        );
        return contributor;
    }

    /**
     * Mock a Project.
     * @param repoFullName Repo full name.
     * @param providerName Provider name.
     * @param budget Budget.
     * @param pmCommission Pm's commission.
     * @return Project.
     */
    private Project mockProject(
        final String repoFullName,
        final String providerName,
        final BigDecimal budget,
        final BigDecimal pmCommission
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(repoFullName);

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(providerName);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(provider);

        Mockito.when(project.owner()).thenReturn(owner);
        final Wallet wallet = Mockito.mock(Wallet.class);
        Mockito.when(wallet.available()).thenReturn(budget);
        Mockito.when(project.wallet()).thenReturn(wallet);

        final ProjectManager projectManager = Mockito
            .mock(ProjectManager.class);
        Mockito.when(projectManager.commission()).thenReturn(pmCommission);
        Mockito.when(project.projectManager()).thenReturn(projectManager);

        return project;
    }
}
