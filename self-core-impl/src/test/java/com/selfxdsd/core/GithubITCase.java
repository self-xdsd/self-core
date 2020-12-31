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

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.exceptions.RepoException;
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
        final JsonObject repo = github
            .repo("amihaiemil", "docker-java-api")
            .json();
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
     * @checkstyle LineLength (100 lines).
     */
    @Test
    public void fetchesRepoNotFound() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider github = new Github(user, null);
        try {
            github.repo("amihaiemil", "missing-test").json();
            Assert.fail("IllegalStateException was expected.");
        } catch (final RepoException.NotFound ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                    "Repo [https://api.github.com/repos/amihaiemil/missing-test] not found. "
                    + "Expected 200 OK, but got 404. "
                )
            );
        }
    }

    /**
     * Provider fetches its organizations.
     */
    @Test
    public void fetchesOrganizations(){
        final Provider github = new Github(
            Mockito.mock(User.class),
            null);
        MatcherAssert.assertThat(github.organizations(),
            Matchers.instanceOf(GithubOrganizations.class));
    }

}
