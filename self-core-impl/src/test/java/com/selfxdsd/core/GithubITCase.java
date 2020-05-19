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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Storage;
import com.selfxdsd.api.User;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;

/**
 * Integration tests for {@link Github}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubITCase {

    /**
     * A repo can be fetched from Github as json.
     */
    @Test
    public void fetchesRepoOk() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider github = new Github(user, null);
        final JsonObject repo = github.repo("docker-java-api").json();
        MatcherAssert.assertThat(
            repo.getString("name"),
            Matchers.equalTo("docker-java-api")
        );
        MatcherAssert.assertThat(
            repo.getString("full_name"),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
    }

    /**
     * IllegalStateException is thrown because the repo is not found.
     */
    @Test
    public void fetchesRepoNotFound() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider github = new Github(user, null);
        try {
            github.repo("docker-java-apsi").json();
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.startsWith("Unexpected response when fetching")
            );
        }
    }

    /**
     * A repo can be assigned to a ProjectManager. The InMemory storage
     * comes with a pre-registered ProjectManager who has the id 1.
     */
    @Test
    public void assignsRepoToManager() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Storage storage = new InMemory();

        MatcherAssert.assertThat(
            storage.projects(),
            Matchers.iterableWithSize(0)
        );
        MatcherAssert.assertThat(
            storage.projects().assignedTo(1),
            Matchers.iterableWithSize(0)
        );

        final Provider github = new Github(user, storage);
        final Project assigned = github.repo("docker-java-api").activate();
        MatcherAssert.assertThat(
            assigned.projectManager().id(),
            Matchers.equalTo(1)
        );

        MatcherAssert.assertThat(
            storage.projects(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.projects().assignedTo(1),
            Matchers.iterableWithSize(1)
        );
    }

}
