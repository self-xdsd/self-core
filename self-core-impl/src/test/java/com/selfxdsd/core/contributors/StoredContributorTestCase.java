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

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link StoredContributor}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredContributorTestCase {

    /**
     * StoredContributor can return his username.
     */
    @Test
    public void returnsUsername() {
        MatcherAssert.assertThat(
            new StoredContributor("mihai", "github", null).username(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * StoredContributor can return his provider.
     */
    @Test
    public void returnsProvider() {
        MatcherAssert.assertThat(
            new StoredContributor("mihai", "github", null).provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * StoredContributor can return his contracts from the Storage
     * if they are not given as ctor argument.
     */
    @Test
    public void returnsContractsFromStorage() {
        final Storage storage = Mockito.mock(Storage.class);
        final Contributor mihai = new StoredContributor(
            "mihai", "github", storage
        );

        final Contracts all = Mockito.mock(Contracts.class);
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(all.ofContributor(mihai)).thenReturn(contracts);

        Mockito.when(storage.contracts()).thenReturn(all);

        MatcherAssert.assertThat(
            mihai.contracts(),
            Matchers.is(contracts)
        );
    }

    /**
     * StoredContributor can return his Contracts (doesn't call Storage),
     * if they've been given as constructor argument.
     */
    @Test
    public void returnsEagerContracts() {
        final Contracts contracts = Mockito.mock(Contracts.class);
        final Contributor mihai = new StoredContributor(
            "mihai", Provider.Names.GITHUB,
            contracts, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            mihai.contracts(),
            Matchers.is(contracts)
        );
    }

    /**
     * StoredContributor can return his tasks.
     */
    @Test
    public void returnsTasks() {
        final Storage storage = Mockito.mock(Storage.class);
        final Contributor mihai = new StoredContributor(
            "mihai", "github", storage
        );

        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(all.ofContributor(mihai.username(), mihai.provider()))
            .thenReturn(tasks);

        Mockito.when(storage.tasks()).thenReturn(all);

        MatcherAssert.assertThat(
            mihai.tasks(),
            Matchers.is(tasks)
        );
    }
    
    /**
     * Can compare two StoredContributor objects.
     */
    @Test
    public void comparesStoredContributorObjects() {
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        final Contributor contributorTwo = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(contributor, Matchers.equalTo(contributorTwo));
    }

    /**
     * Verifies HashCode generation from StoredContributor.
     */
    @Test
    public void verifiesStoredContributorHashcode() {
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        final Contributor contributorTwo = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(contributor.hashCode(),
            Matchers.equalTo(contributorTwo.hashCode()));
    };

    /**
     * Contributor.contract(...) returns the found Contract.
     */
    @Test
    public void returnsFoundContract() {
        final List<Contract> list = new ArrayList<>();
        list.add(
            this.mockContract(
                new Contract.Id(
                    "john/test",
                    "mihai",
                    "github",
                    "DEV"
                )
            )
        );
        list.add(
            this.mockContract(
                new Contract.Id(
                    "john/test2",
                    "mihai",
                    "github",
                    "DEV"
                )
            )
        );
        list.add(
            this.mockContract(
                new Contract.Id(
                    "john/test2",
                    "mihai",
                    "github",
                    "DEV"
                )
            )
        );
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(list.iterator());
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            contracts,
            Mockito.mock(Storage.class)
        );
        final Contract found = contributor.contract(
            "john/test2", "github", "DEV"
        );
        MatcherAssert.assertThat(
            found.contractId(),
            Matchers.equalTo(list.get(1).contractId())
        );
    }

    /**
     * Contributor.contract(...) returns null because the Contract is
     * not found.
     */
    @Test
    public void returnsNullOnMissingContract() {
        final List<Contract> list = new ArrayList<>();
        list.add(
            this.mockContract(
                new Contract.Id(
                    "john/test",
                    "mihai",
                    "github",
                    "REV"
                )
            )
        );
        list.add(
            this.mockContract(
                new Contract.Id(
                    "john/test2",
                    "mihai",
                    "github",
                    "REV"
                )
            )
        );
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(list.iterator());
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            contracts,
            Mockito.mock(Storage.class)
        );
        final Contract found = contributor.contract(
            "john/test", "github", "DEV"
        );
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * Contributor.contract(...) returns null, since no Contract
     * is found because the Contributor has no contracts.
     */
    @Test
    public void returnsNullOnMissingContractNoContracts() {
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator()).thenReturn(
            new ArrayList<Contract>().iterator()
        );
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            contracts,
            Mockito.mock(Storage.class)
        );
        final Contract found = contributor.contract(
            "john/test2", "github", "DEV"
        );
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * Mock a Contract for test.
     * @param contractId ID of he contract.
     * @return Contract mock.
     */
    public Contract mockContract(final Contract.Id contractId) {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(contractId);
        return contract;
    }
}
