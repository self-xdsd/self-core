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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StoredProject}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
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
        final Project project = new StoredProject(
            Mockito.mock(User.class),
            "john/test",
            "wh123token",
            Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.wallet(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(Wallet.Missing.class)
            )
        );
    }

    /**
     * StoredProject can return its webhook token.
     */
    @Test
    public void returnsWebhookToken(){
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
    public void returnsContributors(){
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
     * The StoredProject can return its spoken language.
     * For now, only English is available.
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
     * Mock a Repo for test.
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
