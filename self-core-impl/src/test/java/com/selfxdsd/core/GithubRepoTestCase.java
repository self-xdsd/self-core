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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.RepoAlreadyActiveException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.URI;

/**
 * Unit tests for {@link GithubRepo}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class GithubRepoTestCase {

    /**
     * A GithubRepo can return its owner.
     */
    @Test
    public void returnsOwner() {
        final User owner = Mockito.mock(User.class);
        final Repo repo = new GithubRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080"),
            owner,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repo.owner(), Matchers.is(owner));
    }

    /**
     * A GithubRepo can return its collaborators.
     */
    @Test
    public void returnsCollaborators() {
        final Repo repo = new GithubRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.collaborators(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GithubCollaborators.class)
            )
        );
    }

    /**
     * A GithubRepo can return its webhooks.
     */
    @Test
    public void returnsWebhooks() {
        final Repo repo = new GithubRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.webhooks(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GithubWebhooks.class)
            )
        );
    }

    /**
     * A GithubRepo can be activated.
     */
    @Test
    public void canBeActivated() {
        final Project activated = Mockito.mock(Project.class);
        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(activated.wallets()).thenReturn(wallets);

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final ProjectManagers managers = Mockito.mock(ProjectManagers.class);
        Mockito.when(managers.pick(Provider.Names.GITHUB)).thenReturn(manager);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(managers);
        Mockito.when(storage.projects())
            .thenReturn(Mockito.mock(Projects.class));

        final User owner = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(owner.provider()).thenReturn(provider);

        final JsonResources res = new MockJsonResources(request -> {
            return new MockJsonResources
                .MockResource(200, Json.createObjectBuilder()
                .add("full_name", "john/test")
                .build());
        });
        final Repo repo = new GithubRepo(
            res,
            URI.create("http://localhost:8080/repos/mihai/test/"),
            owner,
            storage
        );

        Mockito.when(manager.assign(repo)).thenReturn(activated);

        Project project = repo.activate();

        MatcherAssert.assertThat(project, Matchers.is(activated));
        Mockito.verify(project, Mockito.times(1)).resolve(Mockito.any());
    }

    /**
     * Throws {@link RepoAlreadyActiveException} if {@link GithubRepo} is
     * already active.
     */
    @Test(expected = RepoAlreadyActiveException.class)
    public void throwsRepoAlreadyActiveExceptionIfActive(){
        final Storage storage = new InMemory();
        final User owner = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(owner.provider()).thenReturn(provider);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        final JsonResources res = new MockJsonResources(request -> {
            return new MockJsonResources
                .MockResource(200, Json.createObjectBuilder()
                .add("full_name", "john/test")
                .build());
        });
        final Repo repo = new GithubRepo(res, URI.create("/"), owner, storage);
        storage.projects().register(repo,
            storage.projectManagers().pick(Provider.Names.GITHUB),
            "token");

        repo.activate();
    }
}
