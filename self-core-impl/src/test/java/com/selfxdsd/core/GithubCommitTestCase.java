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

import com.selfxdsd.api.Commit;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;

/**
 * Unit tests for {@link GithubCommit}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.31
 */
public final class GithubCommitTestCase {

    /**
     * GithubCommit can return its json representation.
     */
    @Test
    public void returnsJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        final Commit commit = new GithubCommit(
            URI.create("localhost:8080/repos/mihai/test/commits/123ref"),
            json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.json(),
            Matchers.is(json)
        );
    }

    /**
     * GithubCommit can return its author.
     */
    @Test
    public void returnsAuthor() {
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "123ref")
            .add(
                "author",
                Json.createObjectBuilder().add("login", "mihai")
            ).build();
        final Commit commit = new GithubCommit(
            URI.create("localhost:8080/repos/mihai/test/commits/123ref"),
            json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.author(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * GithubCommit can return its SHA ref.
     */
    @Test
    public void returnsShaRef() {
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "123ref")
            .add(
                "author",
                Json.createObjectBuilder().add("login", "mihai")
            ).build();
        final Commit commit = new GithubCommit(
            URI.create("localhost:8080/repos/mihai/test/commits/123ref"),
            json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.shaRef(),
            Matchers.equalTo("123ref")
        );
    }

    /**
     * Returns Commit's comments.
     */
    @Test
    public void returnsComments(){
        final Commit commit = new GithubCommit(
            URI.create("http://localhost/repos/mihai/test/commits/ref1"),
            Json.createObjectBuilder().build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.comments(),
            Matchers.instanceOf(GithubCommitComments.class)
        );
    }
}
