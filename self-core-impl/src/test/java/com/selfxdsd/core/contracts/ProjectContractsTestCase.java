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

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.ContractsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contributors.StoredContributor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        list.add(this.mockContract("john/test", "mihai", "github"));
        list.add(this.mockContract("john/test", "vlad", "github"));
        list.add(this.mockContract("john/test", "mihai", "github"));
        list.add(this.mockContract("john/test", "george", "gitlab"));
        list.add(this.mockContract("john/test", "alin", "bitbucket"));
        list.add(this.mockContract("john/test", "mihai", "github"));
        final Contracts contracts = new ProjectContracts(
            "john/test", "github", list::stream, Mockito.mock(Storage.class)
        );
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
            new ProjectContracts(
                "john/test", "gitlab",
                Stream::empty, Mockito.mock(Storage.class)
            ).ofContributor(
                new StoredContributor(
                    "cristi", "github", Mockito.mock(Storage.class)
                )
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ProjectContracts(
                "john/test", "gitlab",
                Stream::empty, Mockito.mock(Storage.class)
            ).ofContributor(
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
        final Contracts contracts = new ProjectContracts(
            "john/test", "github",
            Stream::empty, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contracts.ofProject("john/test", "github"),
            Matchers.is(contracts)
        );
    }

    /**
     * Method ofProject throws an exception if a different projectId
     * is specified.
     */
    @Test (expected = ContractsException.OfProject.List.class)
    public void ofProjectComplainsWhenDifferentId() {
        final Contracts contracts = new ProjectContracts(
            "john/test", "github",
            Stream::empty, Mockito.mock(Storage.class)
        );
        contracts.ofProject("john/test", "gitlab");
    }

    /**
     * We should be able to register a contract for the same Project.
     */
    @Test
    public void addsNewContract() {
        final Contract newContract = Mockito.mock(Contract.class);
        final List<Contract> allSrc = new ArrayList<>();
        final Contracts all = Mockito.mock(Contracts.class);
        Mockito.when(
            all.addContract(
                "john/test",
                "mihai",
                "github",
                BigDecimal.valueOf(10000),
                Contract.Roles.DEV
            )
        ).thenAnswer(invocation -> {
            allSrc.add(newContract);
            return newContract;
        });
        Mockito.when(all.spliterator()).thenReturn(allSrc.spliterator());
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contracts()).thenReturn(all);

        final Contracts ofOne = new ProjectContracts(
            "john/test", "github",
            allSrc::stream, storage
        );
        MatcherAssert.assertThat(ofOne, Matchers.iterableWithSize(0));
        MatcherAssert.assertThat(
            ofOne.addContract(
                "john/test",
                "mihai",
                "github",
                BigDecimal.valueOf(10000),
                Contract.Roles.DEV
            ),
            Matchers.is(newContract)
        );
        MatcherAssert.assertThat(ofOne, Matchers.iterableWithSize(1));
    }

    /**
     * Method addContract should throw ISE if the id of a different Project is
     * specified.
     */
    @Test (expected = ContractsException.OfProject.Add.class)
    public void doesNotAddContractForDifferentProject() {
        new ProjectContracts(
            "john/test1", "github",
            Stream::empty, Mockito.mock(Storage.class)
        ).addContract(
            "john/test",
            "mihai",
            "github",
            BigDecimal.valueOf(10000),
            Contract.Roles.DEV
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
            new ProjectContracts(
                "john/test", "github",
                list::stream, Mockito.mock(Storage.class)
            ),
            Matchers.iterableWithSize(3)
        );

        MatcherAssert.assertThat(
            new ProjectContracts(
                "john/test", "github",
                Stream::empty, Mockito.mock(Storage.class)
            ),
            Matchers.emptyIterable()
        );
    }

    /**
     * Finds a contract by id.
     */
    @Test
    public void findsContractById(){
        final Storage storage = Mockito.mock(Storage.class);
        final Contract contract = Mockito.mock(Contract.class);
        final Project project = Mockito.mock(Project.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        final Contracts contracts = new ProjectContracts(
            "john/test",
            Provider.Names.GITHUB, () -> Stream.of(contract),
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(contributor.username()).thenReturn("john");
        Mockito.when(contract.role()).thenReturn(Contract.Roles.DEV);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(contract.contributor()).thenReturn(contributor);

        final Contract found = contracts
            .findById(new Contract.Id("john/test", "john",
                Provider.Names.GITHUB, Contract.Roles.DEV));

        MatcherAssert.assertThat(found, Matchers.is(contract));

    }

    /**
     * Mock a Contract for test.
     * @param repoFullName Repo's full name.
     * @param contributorUsername Contributor's username.
     * @param provider Contributor/Project's provider.
     * @return Contract.
     */
    private Contract mockContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(repoFullName);
        Mockito.when(project.provider()).thenReturn(provider);

        final Contributor contributor = new StoredContributor(
            contributorUsername,
            provider,
            Mockito.mock(Storage.class)
        );

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(contract.project()).thenReturn(project);

        return contract;
    }


}
