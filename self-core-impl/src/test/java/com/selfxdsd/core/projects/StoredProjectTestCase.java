/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.WalletAlreadyExistsException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link StoredProject}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class StoredProjectTestCase {

    /**
     * StoredProject can return the full name of its Repo.
     */
    @Test
    public void returnsRepoFullName() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn("john/test");
        final Project project = new StoredProject(
            Mockito.mock(User.class),
            repo.fullName(),
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * StoredProject can return its wallet.
     */
    @Test
    public void returnsWallet() {
        final Wallet active = Mockito.mock(Wallet.class);

        final Wallets all = Mockito.mock(Wallets.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.wallets()).thenReturn(all);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.active()).thenReturn(active);

        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        Mockito.when(all.ofProject(project)).thenReturn(ofProject);

        MatcherAssert.assertThat(
            project.wallet(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.is(active)
            )
        );
    }

    /**
     * StoredProject can return its BillingInfo.
     */
    @Test
    public void returnsBillingInfo() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        Mockito.when(info.toString()).thenReturn("Project LLC");

        final Wallet active = Mockito.mock(Wallet.class);
        Mockito.when(active.billingInfo()).thenReturn(info);

        final Wallets all = Mockito.mock(Wallets.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.wallets()).thenReturn(all);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.active()).thenReturn(active);

        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        Mockito.when(all.ofProject(project)).thenReturn(ofProject);

        final BillingInfo returned = project.billingInfo();
        MatcherAssert.assertThat(
            returned,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.is(info)
            )
        );
        MatcherAssert.assertThat(
            returned.toString(),
            Matchers.equalTo("Project LLC")
        );
    }

    /**
     * StoredProject can return its webhook token.
     */
    @Test
    public void returnsWebhookToken() {
        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "mihai/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.webHookToken(),
            Matchers.equalTo("wh123token")
        );
    }

    /**
     * StoredProject can return its provider.
     */
    @Test
    public void returnsProvider() {
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * StoredProject can return its Repo.
     */
    @Test
    public void returnsRepo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(prov.repo("john", "test")).thenReturn(repo);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(project.repo(), Matchers.is(repo));
    }

    /**
     * StoredProject can return its ProjectManager.
     */
    @Test
    public void returnsProjectManager() {
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "john/test",
            "wh123token",
            manager,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.projectManager(),
            Matchers.is(manager)
        );
    }

    /**
     * StoredProject can return its contracts.
     */
    @Test
    public void returnsContracts() {
        final Contracts all = Mockito.mock(Contracts.class);
        final Contracts ofProject = Mockito.mock(Contracts.class);
        Mockito.when(
            all.ofProject("john/test", "github")
        ).thenReturn(ofProject);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contracts()).thenReturn(all);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner, "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        MatcherAssert.assertThat(project.contracts(), Matchers.is(ofProject));
    }

    /**
     * StoredProject can return its contributors.
     */
    @Test
    public void returnsContributors() {
        final Contributors all = Mockito.mock(Contributors.class);
        final Contributors ofProject = Mockito.mock(Contributors.class);
        Mockito.when(
            all.ofProject("john/test", "github")
        ).thenReturn(ofProject);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contributors()).thenReturn(all);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner, "john/test", "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        MatcherAssert.assertThat(
            project.contributors(),
            Matchers.is(ofProject)
        );
    }

    /**
     * StoredProject can return its Tasks.
     */
    @Test
    public void returnsTasks() {
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(
            all.ofProject("john/test", "github")
        ).thenReturn(ofProject);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner, "john/test", "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        MatcherAssert.assertThat(
            project.tasks(),
            Matchers.is(ofProject)
        );
    }

    /**
     * The StoredProject can return its spoken language. For now, only English
     * is available.
     */
    @Test
    public void returnsLanguage() {
        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "john/test",
            "123whToken",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.language(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(English.class)
            )
        );
    }

    /**
     * Can compare two StoredProject objects.
     */
    @Test
    public void comparesStoredProjectObjects() {
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        final Project projectTwo = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(project, Matchers.equalTo(projectTwo));
    }

    /**
     * Verifies HashCode generation from StoredProject.
     */
    @Test
    public void verifiesStoredProjectHashcode() {
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        final Project projectTwo = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(project.hashCode(),
            Matchers.equalTo(projectTwo.hashCode()));
    }

    /**
     * We should not be able to create more than 1 Stripe Wallet per
     * Project.
     */
    @Test (expected = WalletAlreadyExistsException.class)
    public void createsOnlyOneStripeWallet() {
        final List<Wallet> list = new ArrayList<>();
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn(Wallet.Type.STRIPE);
        list.add(stripe);

        final Wallets all = Mockito.mock(Wallets.class);
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(list.iterator());

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.wallets()).thenReturn(all);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner, "john/test", "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );
        Mockito.when(
            all.ofProject(project)
        ).thenReturn(ofProject);
        project.createStripeWallet(Mockito.mock(BillingInfo.class));
    }

    /**
     * A StoredProject cannot be deactivated if it still
     * has contracts.
     */
    @Test(expected = IllegalStateException.class)
    public void doesNotDeactivateWithContracts() {
        final Storage storage = Mockito.mock(Storage.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            storage
        );

        final Contracts all = Mockito.mock(Contracts.class);
        final Contracts ofProject = Mockito.mock(Contracts.class);
        Mockito.when(ofProject.count()).thenReturn(5);
        Mockito.when(all.ofProject("john/test", "github")).thenReturn(
            ofProject
        );
        Mockito.when(storage.contracts()).thenReturn(all);

        project.deactivate(Mockito.mock(Repo.class));
    }

    /**
     * A StoredProject with no Contracts can be deactivated.
     */
    @Test
    public void canBeDeactivated() {
        final Repo repo = Mockito.mock(Repo.class);

        final Storage storage = Mockito.mock(Storage.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.username()).thenReturn("charlesmike");
        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            manager,
            storage
        );

        final Contracts all = Mockito.mock(Contracts.class);
        final Contracts ofProject = Mockito.mock(Contracts.class);
        Mockito.when(ofProject.count()).thenReturn(0);
        Mockito.when(all.ofProject("john/test", "github")).thenReturn(
            ofProject
        );
        Mockito.when(storage.contracts()).thenReturn(all);
        final Projects allProjects = Mockito.mock(Projects.class);
        Mockito.when(storage.projects()).thenReturn(allProjects);

        final Webhooks webhooks = Mockito.mock(Webhooks.class);
        final Collaborators collaborators = Mockito.mock(Collaborators.class);
        Mockito.when(repo.webhooks()).thenReturn(webhooks);
        Mockito.when(repo.collaborators()).thenReturn(collaborators);

        MatcherAssert.assertThat(
            project.deactivate(repo),
            Matchers.is(repo)
        );

        Mockito.verify(allProjects, Mockito.times(1)).remove(project);
        Mockito.verify(repo, Mockito.times(1)).webhooks();
        Mockito.verify(repo, Mockito.times(1)).collaborators();
        Mockito.verify(webhooks, Mockito.times(1)).remove();
        Mockito.verify(collaborators, Mockito.times(1)).remove("charlesmike");
    }

    /**
     * StoredProject can be renamed. Webhooks should be removed, project renamed
     * in the Storage and new Webhook should be added.
     */
    @Test
    public void canBeRenamed() {
        final Project renamed = Mockito.mock(Project.class);

        final Webhooks hooks = Mockito.mock(Webhooks.class);
        Mockito.when(hooks.remove()).thenReturn(true);

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.webhooks()).thenReturn(hooks);

        final Storage storage = Mockito.mock(Storage.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(prov.repo("john", "test")).thenReturn(repo);

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            manager,
            storage
        );

        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.rename(project, "newName"))
            .thenReturn(renamed);
        Mockito.when(storage.projects()).thenReturn(all);
        Mockito.when(hooks.add(renamed)).thenReturn(true);

        project.rename("newName");

        Mockito.verify(hooks, Mockito.times(1)).remove();
        Mockito.verify(all, Mockito.times(1)).rename(project, "newName");
        Mockito.verify(hooks, Mockito.times(1)).add(renamed);
    }

    /**
     * StoredProject should not be renamed if we fail to remove the original
     * Webhooks.
     */
    @Test
    public void doesNotRenameWhenWebhookRemovalFails() {
        final Webhooks hooks = Mockito.mock(Webhooks.class);
        Mockito.when(hooks.remove()).thenReturn(false);

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.webhooks()).thenReturn(hooks);

        final Storage storage = Mockito.mock(Storage.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(prov.repo("john", "test")).thenReturn(repo);

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner,
            "john/test",
            "wh123token",
            manager,
            storage
        );

        project.rename("newName");

        Mockito.verify(hooks, Mockito.times(1)).remove();
        Mockito.verify(storage, Mockito.times(0)).projects();
        Mockito.verify(hooks, Mockito.times(0)).add(Mockito.any());
    }

    /**
     * Mock a Repo for test.
     *
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(final String fullName, final String provider) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);
        return repo;
    }
}
