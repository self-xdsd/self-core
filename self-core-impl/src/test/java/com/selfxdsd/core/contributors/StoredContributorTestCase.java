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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
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
        final Contract contract = this.mockContract(
            new Contract.Id(
                "john/repo",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        final Contracts all = Mockito.mock(Contracts.class);
        Mockito.when(
            all.findById(
                new Contract.Id(
                    "john/repo",
                    "mihai",
                    Provider.Names.GITHUB,
                    Contract.Roles.DEV
                )
            )
        ).thenReturn(contract);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contracts()).thenReturn(all);
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            storage
        );
        final Contract found = contributor.contract(
            "john/repo",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(
            found,
            Matchers.is(contract)
        );
    }

    /**
     * Contributor.contract(...) returns null because the Contract is
     * not found.
     */
    @Test
    public void returnsNullOnMissingContract() {
        final Contracts all = Mockito.mock(Contracts.class);
        Mockito.when(
            all.findById(
                new Contract.Id(
                    "john/repo",
                    "mihai",
                    Provider.Names.GITHUB,
                    Contract.Roles.DEV
                )
            )
        ).thenReturn(null);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contracts()).thenReturn(all);
        final Contributor contributor = new StoredContributor(
            "mihai",
            Provider.Names.GITHUB,
            storage
        );
        final Contract found = contributor.contract(
            "john/repo",
            Provider.Names.GITHUB,
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * StoredContributor.createStripeAccount(...) should throw an ISE
     * if the Contributor already has a PayoutMethod of type "STRIPE".
     */
    @Test
    public void complainsOnDuplicateStripeAccount() {
        final List<PayoutMethod> list = new ArrayList<>();
        final PayoutMethod stripe = Mockito.mock(PayoutMethod.class);
        Mockito.when(stripe.type()).thenReturn(PayoutMethod.Type.STRIPE);
        list.add(stripe);

        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        Mockito.when(ofContributor.iterator()).thenReturn(list.iterator());

        final PayoutMethods all = Mockito.mock(PayoutMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.payoutMethods()).thenReturn(all);

        final Contributor contributor = new StoredContributor(
            "amihaiemil", Provider.Names.GITHUB, storage
        );
        Mockito.when(all.ofContributor(contributor)).thenReturn(ofContributor);

        try {
            contributor.createStripeAccount(Mockito.mock(BillingInfo.class));
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            Mockito.verify(ofContributor, Mockito.times(0)).register(
                Mockito.any(), Mockito.anyString(), Mockito.anyString()
            );
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                    "Contributor amihaiemil at github "
                    + "already has a Stripe Connect Account."
                )
            );
        }
    }

    /**
     * StoredContributor.createStripeAccount(...) should throw an ISE
     * if the stripe.api.token env variable is not set.
     */
    @Test
    public void complainsOnMissingApiKey() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        Mockito.when(ofContributor.iterator())
            .thenReturn(new ArrayList<PayoutMethod>().iterator());

        final PayoutMethods all = Mockito.mock(PayoutMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.payoutMethods()).thenReturn(all);

        final Contributor contributor = new StoredContributor(
            "amihaiemil", Provider.Names.GITHUB, storage
        );
        Mockito.when(all.ofContributor(contributor)).thenReturn(ofContributor);

        try {
            contributor.createStripeAccount(Mockito.mock(BillingInfo.class));
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            Mockito.verify(ofContributor, Mockito.times(0)).register(
                Mockito.any(), Mockito.anyString(), Mockito.anyString()
            );
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                    "[CREATE_STRIPE_ACCOUNT] Please specify the "
                    + "self_stripe_token Environment Variable!"
                )
            );
        }
    }

    /**
     * StoredContributor.payoutMethods can return the contributor's
     * payout methods.
     */
    @Test
    public void returnsPayoutMethods() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);

        final PayoutMethods all = Mockito.mock(PayoutMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.payoutMethods()).thenReturn(all);

        final Contributor contributor = new StoredContributor(
            "amihaiemil", Provider.Names.GITHUB, storage
        );
        Mockito.when(all.ofContributor(contributor)).thenReturn(ofContributor);

        MatcherAssert.assertThat(
            contributor.payoutMethods(),
            Matchers.is(ofContributor)
        );
    }

    /**
     * StoredContributor can return its BillingInfo from the
     * active PayoutMethod.
     */
    @Test
    public void returnsBillingInfoOfActivePayoutMethod() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        final PayoutMethod stripe = Mockito.mock(PayoutMethod.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.billingInfo()).thenReturn(info);

        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        Mockito.when(ofContributor.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        Mockito.when(ofContributor.getByType("STRIPE")).thenReturn(stripe);

        final PayoutMethods all = Mockito.mock(PayoutMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.payoutMethods()).thenReturn(all);

        final Contributor contributor = new StoredContributor(
            "amihaiemil", Provider.Names.GITHUB, storage
        );
        Mockito.when(all.ofContributor(contributor)).thenReturn(ofContributor);

        MatcherAssert.assertThat(
            contributor.billingInfo(),
            Matchers.is(info)
        );
    }

    /**
     * StripeContributor returns its default BillingInfo if it has
     * no PayoutMethods set up.
     */
    @Test
    public void returnsDefaultBillingInfo() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        Mockito.when(ofContributor.iterator()).thenReturn(
            new ArrayList<PayoutMethod>().iterator()
        );

        final PayoutMethods all = Mockito.mock(PayoutMethods.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.payoutMethods()).thenReturn(all);

        final Contributor contributor = new StoredContributor(
            "amihaiemil", Provider.Names.GITHUB, storage
        );
        Mockito.when(all.ofContributor(contributor)).thenReturn(ofContributor);

        MatcherAssert.assertThat(
            contributor.billingInfo().legalName(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            contributor.billingInfo().toString(),
            Matchers.equalTo("Contributor amihaiemil at github.")
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
