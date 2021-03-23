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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link BitbucketRepo}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
public final class BitbucketRepoTestCase {

    /**
     * BitbucketRepo has full name.
     */
    @Test
    public void hasFullName() {
        final MockJsonResources res = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                200,
                Json.createObjectBuilder()
                    .add("full_name", "john/test")
                    .build()
            ));
        final BitbucketRepo repo = new BitbucketRepo(
            res,
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );

        final String fullName = repo.fullName();
        final MockJsonResources.MockRequest req = res.requests().first();
        MatcherAssert.assertThat(fullName, Matchers
            .equalTo("john/test"));
        MatcherAssert.assertThat(req.getUri(), Matchers.equalTo(
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test")
        ));
        MatcherAssert.assertThat(req.getMethod(), Matchers.equalTo("GET"));
    }

    /**
     * BitbucketRepo.activate() can activate the repo.
     */
    @Test
    public void activatesProject() {
        // projects fixture
        final Projects projects = Mockito.mock(Projects.class);
        Mockito.when(projects.getProjectById(
            "alilo",
            "test")
        ).thenReturn(null);
        // user fixture
        final User owner = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.BITBUCKET);
        Mockito.when(owner.provider()).thenReturn(provider);
        // project manager fixture
        final ProjectManagers managers = Mockito.mock(ProjectManagers.class);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(managers.pick(Provider.Names.BITBUCKET))
            .thenReturn(manager);
        // storage fixture
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(projects);
        Mockito.when(storage.projectManagers()).thenReturn(managers);

        final Repo repo = new BitbucketRepo(
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add(
                    "full_name",
                    "alilo/test"
                ).build()
            )),
            URI.create("https://bitbucket.org/api/2.0/repositories/alilo/test"),
            owner,
            storage
        );
        // project fixture
        final Project activated = Mockito.mock(Project.class);
        Mockito.when(manager.assign(repo)).thenReturn(activated);
        Mockito.when(activated.wallets())
            .thenReturn(Mockito.mock(Wallets.class));
        Mockito.when(activated.contracts())
            .thenReturn(Mockito.mock(Contracts.class));

        MatcherAssert.assertThat(
            repo.activate(),
            Matchers.is(activated)
        );
        Mockito.verify(activated).resolve(Mockito.any());
    }

    /**
     * BitbucketRepo.issues() returns its issues.
     */
    @Test
    public void returnsIssues() {
        BitbucketRepo repo = new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.issues(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketIssues.class)
            )
        );
    }

    /**
     * BitbucketRepo.pullRequests() returns its pull requests.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsPullRequests() {
        new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        ).pullRequests();
    }

    /**
     * BitbucketRepo.collaborators() returns its collaborators.
     */
    @Test
    public void returnsCollaborators() {
        final Repo repo = new BitbucketRepo(
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add(
                    "workspace",
                    Json.createObjectBuilder()
                        .add("slug", "john")
                        .build()
                ).build()
            )),
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/john/test"
            ),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            repo.collaborators(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketCollaborators.class)
            )
        );
    }

    /**
     * BitbucketRepo.webhooks() returns its webhooks.
     */
    @Test
    public void returnsWebhooks() {
        final BitbucketRepo repo = new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        final Webhooks webhooks = repo.webhooks();
        MatcherAssert.assertThat(
            webhooks,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketWebhooks.class)
            )
        );
        MatcherAssert.assertThat(
            webhooks.toString(),
            Matchers.equalTo("https://bitbucket.org/api/2.0"
                + "/repositories/john/test/hooks")
        );
    }

    /**
     * BitbucketRepo.stars() returns its stars.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsStars() {
        new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        ).stars();
    }

    /**
     * BitbucketRepo.commits() returns its commits.
     */
    @Test
    public void returnsCommits() {
        MatcherAssert.assertThat(new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        ).commits(), Matchers.instanceOf(BitbucketCommits.class));
    }

    /**
     * BitbucketRepo.labels() returns its labels.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsLabels() {
        new BitbucketRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories/john/test"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        ).labels();
    }
}