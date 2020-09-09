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
        list.add(this.mockContract("mihai", "github", "john/test"));
        list.add(this.mockContract("mihai", "github", "alex/test"));
        list.add(this.mockContract("mihai", "github", "john/test"));
        list.add(this.mockContract("mihai", "github", "cris/test"));
        list.add(this.mockContract("mihai", "github", "john/test"));
        final Contracts contracts = new ContributorContracts(
            new StoredContributor(
                "mihai", "github", Mockito.mock(Storage.class)
            ),
            list::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contracts.ofProject("john/test", "github"),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            contracts.ofProject("alex/test", "github"),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            contracts.ofProject("some/test", "github"),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ContributorContracts(
                new StoredContributor(
                    "mihai", "github", Mockito.mock(Storage.class)
                ),
                Stream::empty,
                Mockito.mock(Storage.class)
            ).ofProject("some/test", "github"),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            new ContributorContracts(
                new StoredContributor(
                    "mihai", "github", Mockito.mock(Storage.class)
                ),
                Stream::empty,
                Mockito.mock(Storage.class)
            ).ofProject("some/test", "github"),
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
            Stream::empty,
            Mockito.mock(Storage.class)
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
    @Test(expected = ContractsException.OfContributor.List.class)
    public void ofContributorComplainsWhenDifferentId() {
        final Contracts contracts = new ContributorContracts(
            new StoredContributor(
                "mihai",
                "github",
                Mockito.mock(Storage.class)
            ),
            Stream::empty,
            Mockito.mock(Storage.class)
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
            new ContributorContracts(
                Mockito.mock(Contributor.class),
                list::stream,
                Mockito.mock(Storage.class)
            ),
            Matchers.iterableWithSize(3)
        );

        MatcherAssert.assertThat(
            new ContributorContracts(
                Mockito.mock(Contributor.class),
                Stream::empty,
                Mockito.mock(Storage.class)
            ),
            Matchers.emptyIterable()
        );
    }


    /**
     * We should be able to register a Contract for the same Contributor.
     */
    @Test
    public void addsNewContract(){
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
        final Contributor mihai = new StoredContributor(
            "mihai", "github", storage
        );

        final Contracts ofMihai = new ContributorContracts(
            mihai, allSrc::stream, storage
        );
        MatcherAssert.assertThat(ofMihai, Matchers.iterableWithSize(0));
        MatcherAssert.assertThat(
            ofMihai.addContract(
                "john/test",
                "mihai",
                "github",
                BigDecimal.valueOf(10000),
                Contract.Roles.DEV
            ),
            Matchers.is(newContract)
        );
        MatcherAssert.assertThat(ofMihai, Matchers.iterableWithSize(1));
    }

    /**
     * Method addContract should throw ISE if a different Contributor is
     * specified.
     */
    @Test (expected = ContractsException.OfContributor.Add.class)
    public void doesNotAddContractForDifferentContributor(){
        final Storage storage = Mockito.mock(Storage.class);
        final Contributor mihai = new StoredContributor(
            "mihai", "github", storage
        );

        final Contracts ofMihai = new ContributorContracts(
            mihai, Stream::empty, storage
        );
        ofMihai.addContract(
            "john/test",
            "mihai",
            "gitlab",
            BigDecimal.valueOf(10000),
            Contract.Roles.DEV
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
        final Contracts contracts = new ContributorContracts(
            contributor,
            ()->Stream.of(contract),
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
     * @param contributorUsername Contributor's username.
     * @param provider Contributor/Project's provider.
     * @param repoFullName Repo full name.
     * @return Contract.
     */
    private Contract mockContract(
        final String contributorUsername,
        final String provider,
        final String repoFullName
    ) {
        final Contributor contributor = new StoredContributor(
            contributorUsername,
            provider,
            Mockito.mock(Storage.class)
        );

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(repoFullName);
        Mockito.when(project.provider()).thenReturn(provider);

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);
        Mockito.when(contract.project()).thenReturn(project);

        return contract;
    }

}
