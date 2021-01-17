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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link GitlabCommit}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0p.0.49
 */
public final class GitlabCommitTestCase {

    /**
     * GitlabCommit can return its json representation.
     */
    @Test
    public void returnsJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        final Commit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            json,
            Mockito.mock(Collaborators.class),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.json(),
            Matchers.is(json)
        );
    }

    /**
     * GitlabCommit can return its author's username by
     * iterating over Collaborators.
     */
    @Test
    public void authorFoundInCollaborators() {
        final Collaborator mihai = Mockito.mock(Collaborator.class);
        Mockito.when(mihai.name()).thenReturn("Mihai A.");
        Mockito.when(mihai.username()).thenReturn("amihaiemil");
        final Collaborators collaborators = Mockito.mock(Collaborators.class);
        Mockito.when(collaborators.iterator()).thenReturn(
            List.of(mihai).iterator()
        );
        final Commit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            Json.createObjectBuilder()
                .add("author_name", "Mihai A.")
                .build(),
            collaborators,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.author(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * GitlabCommit returns empty string if its author is not found.
     */
    @Test
    public void authorNotFound() {
        final Collaborators collaborators = Mockito.mock(Collaborators.class);
        Mockito.when(collaborators.iterator()).thenReturn(
            new ArrayList<Collaborator>().iterator()
        );
        final Commit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            Json.createObjectBuilder()
                .add("author_name", "Mihai A.")
                .build(),
            collaborators,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.author(),
            Matchers.equalTo("")
        );
    }


    /**
     * GitlabCommit can return its SHA ref.
     */
    @Test
    public void returnsShaRef() {
        final JsonObject json = Json.createObjectBuilder()
            .add("id", "sha123")
            .build();
        final Commit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            json,
            Mockito.mock(Collaborators.class),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.shaRef(),
            Matchers.equalTo("sha123")
        );
    }

    /**
     * Returns Commit's comments.
     */
    @Test
    public void returnsComments(){
        final Commit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            Mockito.mock(JsonObject.class),
            Mockito.mock(Collaborators.class),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            commit.comments(),
            Matchers.instanceOf(GitlabCommitComments.class)
        );
    }
}
