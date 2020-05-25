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
package com.selfxdsd.core.contracts;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contributors.StoredContributor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link ContributorContracts}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorContractsTestCase {

    /**
     * ContributorContracts can return the Contracts
     * of a certain Project.
     */
    @Test
    public void returnsContractsOfProject() {
        final List<Contract> list = new ArrayList<>();
        list.add(this.mockContract("mihai", "github", 1));
        list.add(this.mockContract("mihai", "github", 2));
        list.add(this.mockContract("mihai", "github", 1));
        list.add(this.mockContract("mihai", "github", 3));
        list.add(this.mockContract("mihai", "github", 1));
        final Contracts contracts = new ContributorContracts(
            new StoredContributor(
                "mihai", "github", Mockito.mock(Storage.class)
            ),
            list
        );
        MatcherAssert.assertThat(
            contracts.ofProject(1),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            contracts.ofProject(2),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            contracts.ofProject(10),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ContributorContracts(
                new StoredContributor(
                "mihai", "github", Mockito.mock(Storage.class)
                ),
                new ArrayList<>()
            ).ofProject(10),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ContributorContracts(
                new StoredContributor(
                "mihai", "github", Mockito.mock(Storage.class)
                ),
                new ArrayList<>()
            ).ofProject(10),
            Matchers.instanceOf(ProjectContracts.class)
        );
    }

    /**
     * Method ofContributor returns the same instance when
     * the ids are matching.
     */
    @Test
    public void ofContributorReturnsItself() {
        final Contracts contracts = new ContributorContracts(
            new StoredContributor(
                "mihai",
                "github",
                Mockito.mock(Storage.class)
            ),
            new ArrayList<>()
        );
        MatcherAssert.assertThat(
            contracts.ofContributor(
                new StoredContributor(
                    "mihai",
                    "github",
                    Mockito.mock(Storage.class)
                )
            ),
            Matchers.is(contracts)
        );
    }

    /**
     * Method ofContributor throws an exception if a different contributorId
     * is specified.
     */
    @Test(expected = IllegalStateException.class)
    public void ofContributorComplainsWhenDifferentId() {
        final Contracts contracts = new ContributorContracts(
            new StoredContributor(
                "mihai",
                "github",
                Mockito.mock(Storage.class)
            ),
            new ArrayList<>()
        );
        contracts.ofContributor(
            new StoredContributor(
                "vlad",
                "gitlab",
                Mockito.mock(Storage.class)
            )
        );
    }

    /**
     * ProjectContracts are iterable.
     */
    @Test
    public void canBeIterated() {
        final List<Contract> list = new ArrayList<>();
        list.add(Mockito.mock(Contract.class));
        list.add(Mockito.mock(Contract.class));
        list.add(Mockito.mock(Contract.class));

        MatcherAssert.assertThat(
            new ContributorContracts(Mockito.mock(Contributor.class), list),
            Matchers.iterableWithSize(3)
        );

        MatcherAssert.assertThat(
            new ContributorContracts(
                Mockito.mock(Contributor.class),
                new ArrayList<>()
            ),
            Matchers.emptyIterable()
        );
    }


    /**
     * Should throw UnsupportedOperationException when adding a contract.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addContract(){
        new ContributorContracts(Mockito.mock(Contributor.class), List.of())
            .addContract(
                1,
                "mihai",
                "github",
                BigDecimal.TEN,
                Contract.Roles.DEV);
    }

    /**
     * Mock a Contract for test.
     * @param contributorUsername Contributor's username.
     * @param contributorProvider Contributor's provider.
     * @param projectId Project's ID.
     * @return Contract.
     */
    private Contract mockContract(
        final String contributorUsername,
        final String contributorProvider,
        final int projectId
    ) {
        final Contributor contributor = new StoredContributor(
            contributorUsername,
            contributorProvider,
            Mockito.mock(Storage.class)
        );

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.projectId()).thenReturn(projectId);

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(contract.project()).thenReturn(project);

        return contract;
    }

}
