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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

/**
 * Unit tests for {@link ProjectContributors}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class ProjectContributorsTestCase {

    /**
     * ProjectContributors should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(contributors, Matchers.iterableWithSize(3));
    }

    /**
     * Returns null when the specified Contributor is not found.
     */
    @Test
    public void getByIdFindsNothing() {
        final Contributors contributors = new ProjectContributors(
            "john/test", "github",
            List.of(),
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


        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(vlad, mihai),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.getById("mihai", Provider.Names.GITHUB),
            Matchers.is(mihai)
        );
    }


    /**
     * Method ofProject should return the same instance if the ID
     * is a match.
     */
    @Test
    public void ofProjectReturnsSelfIfSameId() {
        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.ofProject("john/test", Provider.Names.GITHUB),
            Matchers.is(contributors)
        );
    }

    /**
     * Method ofProject should complain if the ID of another
     * project is given as input.
     */
    @Test(expected = IllegalStateException.class)
    public void ofProjectComplainsIfDifferentId() {
        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.ofProject("george/test", Provider.Names.GITLAB),
            Matchers.is(contributors)
        );
    }

    /**
     * We should only be able to register contributors from the same provider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerComplainsWhenDiffProvider() {
        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class),
                Mockito.mock(Contributor.class)
            ),
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
            "john/test", Provider.Names.GITHUB,
            List.of(vlad),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contributors.register("vlad", Provider.Names.GITHUB),
            Matchers.is(vlad)
        );
    }

    /**
     * A new contributor is registered and a DEV Contract with hourly rate 0
     * is created.
     */
    @Test
    public void registersNewContributor() {
        final Contributor mihai = Mockito.mock(Contributor.class);
        final Contributors allContributors = Mockito.mock(Contributors.class);
        Mockito.when(
            allContributors.register("mihai", Provider.Names.GITHUB)
        ).thenReturn(mihai);
        final Contracts allContracts = Mockito.mock(Contracts.class);
        Mockito.when(
            allContracts.addContract(
                "john/test", "mihai", Provider.Names.GITHUB,
                BigDecimal.valueOf(0), Contract.Roles.DEV
            )
        ).thenReturn(Mockito.mock(Contract.class));
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contributors()).thenReturn(allContributors);
        Mockito.when(storage.contracts()).thenReturn(allContracts);

        final Contributors contributors = new ProjectContributors(
            "john/test", Provider.Names.GITHUB,
            List.of(),
            storage
        );
        MatcherAssert.assertThat(contributors, Matchers.emptyIterable());
        MatcherAssert.assertThat(
            contributors.register("mihai", Provider.Names.GITHUB),
            Matchers.is(mihai)
        );
        MatcherAssert.assertThat(contributors, Matchers.iterableWithSize(1));
    }
}
