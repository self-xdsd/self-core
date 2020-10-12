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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.Wallets;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link ProjectWallets}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 */
public final class ProjectWalletsTestCase {

    /**
     * ProjectWallets can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Wallets wallets = new ProjectWallets(
            Mockito.mock(Project.class),
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            wallets,
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * ProjectWallets can return the active wallet.
     */
    @Test
    public void returnsActiveWallet() {
        final Wallet active = Mockito.mock(Wallet.class);
        Mockito.when(active.active()).thenReturn(Boolean.TRUE);
        final Wallets wallets = new ProjectWallets(
            Mockito.mock(Project.class),
            Arrays.asList(
                Mockito.mock(Wallet.class),
                active,
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            wallets.active(),
            Matchers.is(active)
        );
    }

    /**
     * ProjectWallets.ofProject should return self if the
     * Project matches.
     */
    @Test
    public void ofProjectReturnsSelf() {
        final Project project = Mockito.mock(Project.class);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            wallets.ofProject(project),
            Matchers.is(wallets)
        );
    }

    /**
     * ProjectWallets.ofProject should throw an ISE if the
     * given project is different.
     */
    @Test (expected = IllegalStateException.class)
    public void ofProjectComplainsOnDifferentProject() {
        final Project project = Mockito.mock(Project.class);
        final Project other = Mockito.mock(Project.class);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        wallets.ofProject(other);
    }

    /**
     * We shouldn't be able to register a wallet in a different project.
     */
    @Test (expected = IllegalStateException.class)
    public void registerComplainsOnDifferentProject() {
        final Project project = Mockito.mock(Project.class);
        final Project other = Mockito.mock(Project.class);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        wallets.register(
            other,
            Wallet.Type.STRIPE,
            BigDecimal.valueOf(10000),
            "stripe-123w"
        );
    }

    /**
     * We shouldn't be able to register more wallets of the same type.
     */
    @Test (expected = IllegalStateException.class)
    public void registerComplainsIfWalletTypeExists() {
        final Project project = Mockito.mock(Project.class);
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn(Wallet.Type.STRIPE);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(stripe),
            Mockito.mock(Storage.class)
        );
        wallets.register(
            project, Wallet.Type.STRIPE, BigDecimal.valueOf(10000), "fk-123w"
        );
    }

    /**
     * We can register a new wallet.
     */
    @Test
    public void registerWalletWorks() {
        final Wallet registered = Mockito.mock(Wallet.class);

        final Project project = Mockito.mock(Project.class);
        final Wallet missing = Mockito.mock(Wallet.class);
        Mockito.when(missing.type()).thenReturn(Wallet.Type.FAKE);

        final Wallets all = Mockito.mock(Wallets.class);
        Mockito.when(
            all.register(
                project,
                Wallet.Type.STRIPE,
                BigDecimal.valueOf(1000),
                "stripe-123w"
            )
        ).thenReturn(registered);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.wallets()).thenReturn(all);

        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(missing),
            storage
        );
        MatcherAssert.assertThat(
            wallets,
            Matchers.iterableWithSize(1)
        );
        final Wallet wallet = wallets.register(
            project,
            Wallet.Type.STRIPE,
            BigDecimal.valueOf(1000),
            "stripe-123w"
        );
        MatcherAssert.assertThat(
            wallet,
            Matchers.is(registered)
        );
        MatcherAssert.assertThat(
            wallets,
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * ProjectWallets.activate(Wallet) complains if the given Wallet
     * belongs to another project.
     */
    @Test(expected = IllegalStateException.class)
    public void activateWalletComplainsOnDifferentProject() {
        final Project project = Mockito.mock(Project.class);
        final Project other = Mockito.mock(Project.class);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            Mockito.mock(Storage.class)
        );
        final Wallet wallet = Mockito.mock(Wallet.class);
        Mockito.when(wallet.project()).thenReturn(other);
        wallets.activate(wallet);
    }

    /**
     * We can activate a Wallet belonging to the project.
     */
    @Test
    public void activateWalletWorks() {
        final Wallet active = Mockito.mock(Wallet.class);
        final Wallets all = Mockito.mock(Wallets.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.wallets()).thenReturn(all);

        final Project project = Mockito.mock(Project.class);
        final Wallets wallets = new ProjectWallets(
            project,
            Arrays.asList(
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class),
                Mockito.mock(Wallet.class)
            ),
            storage
        );
        final Wallet wallet = Mockito.mock(Wallet.class);
        Mockito.when(wallet.project()).thenReturn(project);
        Mockito.when(all.activate(wallet)).thenReturn(active);

        MatcherAssert.assertThat(
            wallets.activate(wallet),
            Matchers.is(active)
        );
    }

    /**
     * We can update cash for a Wallet belonging to the project.
     */
    @Test
    public void updatesCashWorks(){
        final Storage storage = Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);
        final Wallet wallet = Mockito.mock(Wallet.class);
        final Wallets all = Mockito.mock(Wallets.class);
        final Wallets wallets = new ProjectWallets(
            project,
            List.of(wallet),
            storage
        );
        Mockito.when(wallet.project()).thenReturn(project);
        Mockito.when(storage.wallets()).thenReturn(all);

        wallets.updateCash(wallet, BigDecimal.TEN);

        Mockito.verify(all, Mockito.times(1))
            .updateCash(wallet, BigDecimal.TEN);
    }

    /**
     * ProjectWallets.updateCash(...) complains if the given Wallet
     * belongs to another project.
     */
    @Test(expected = IllegalStateException.class)
    public void complainsWhenUpdatesCash(){
        final Storage storage = Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);
        final Wallet wallet = Mockito.mock(Wallet.class);
        final Wallets wallets = new ProjectWallets(
            project,
            List.of(Mockito.mock(Wallet.class)),
            storage
        );
        Mockito.when(wallet.project()).thenReturn(Mockito.mock(Project.class));
        wallets.updateCash(wallet, BigDecimal.TEN);
    }
}
