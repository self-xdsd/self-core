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
 * Unit tests for {@link ProjectContracts}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectContractsTestCase {

    /**
     * ProjectContracts can return the Contracts
     * of a certain Contributor.
     */
    @Test
    public void returnsContractsOfContributor() {
        final List<Contract> list = new ArrayList<>();
        list.add(this.mockContract(1, "mihai", "github"));
        list.add(this.mockContract(1, "vlad", "github"));
        list.add(this.mockContract(1, "mihai", "github"));
        list.add(this.mockContract(1, "george", "gitlab"));
        list.add(this.mockContract(1, "alin", "bitbucket"));
        list.add(this.mockContract(1, "mihai", "github"));
        final Contracts contracts = new ProjectContracts(1, list);
        MatcherAssert.assertThat(
            contracts.ofContributor(
                new StoredContributor(
                    "mihai", "github", Mockito.mock(Storage.class)
                )
            ),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            contracts.ofContributor(
                new StoredContributor(
                    "vlad", "github", Mockito.mock(Storage.class)
                )
            ),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            contracts.ofContributor(
                new StoredContributor(
                    "cristi", "github", Mockito.mock(Storage.class)
                )
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ProjectContracts(1, new ArrayList<>())
                .ofContributor(
                    new StoredContributor(
                        "cristi", "github", Mockito.mock(Storage.class)
                    )
                ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ProjectContracts(1, new ArrayList<>())
                .ofContributor(
                    new StoredContributor(
                        "cristi", "github", Mockito.mock(Storage.class)
                    )
                ),
            Matchers.instanceOf(ContributorContracts.class)
        );
    }

    /**
     * Method ofProject returns the same instance when
     * the ids are matching.
     */
    @Test
    public void ofProjectReturnsItself() {
        final Contracts contracts = new ProjectContracts(1, new ArrayList<>());
        MatcherAssert.assertThat(
            contracts.ofProject(1),
            Matchers.is(contracts)
        );
    }

    /**
     * Method ofProject throws an exception if a different projectId
     * is specified.
     */
    @Test (expected = IllegalStateException.class)
    public void ofProjectComplainsWhenDifferentId() {
        final Contracts contracts = new ProjectContracts(1, new ArrayList<>());
        contracts.ofProject(2);
    }

    /**
     * Should throw UnsupportedOperationException when adding a contract.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addContract(){
        new ProjectContracts(0, List.of())
            .addContract(
                1,
                "mihai",
                "github'",
                BigDecimal.TEN,
                "DEV");
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
            new ProjectContracts(1, list),
            Matchers.iterableWithSize(3)
        );

        MatcherAssert.assertThat(
            new ProjectContracts(1, new ArrayList<>()),
            Matchers.emptyIterable()
        );
    }

    /**
     * Mock a Contract for test.
     * @param projectId Project's ID.
     * @param contributorUsername Contributor's username.
     * @param contributorProvider Contributor's provider.
     * @return Contract.
     */
    private Contract mockContract(
        final int projectId,
        final String contributorUsername,
        final String contributorProvider
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.projectId()).thenReturn(projectId);

        final Contributor contributor = new StoredContributor(
            contributorUsername,
            contributorProvider,
            Mockito.mock(Storage.class)
        );

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(contract.project()).thenReturn(project);

        return contract;
    }


}
