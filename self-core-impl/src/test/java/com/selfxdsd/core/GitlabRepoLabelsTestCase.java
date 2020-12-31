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

import com.selfxdsd.api.Label;
import com.selfxdsd.api.Labels;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabRepoLabels}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 */
public final class GitlabRepoLabelsTestCase {

    /**
     * GitlabRepoLabels can be iterated.
     */
    @Test
    public void canIterateOverLabels() {
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("name", "bug")
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("name", "enhancement")
                        .build())
                    .build()));
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "/amihaiemil%2Ftestrepo/labels"
        );

        final Iterable<Label> iterable = () -> new GitlabRepoLabels(
            uri, resources
        ).iterator();

        MatcherAssert.assertThat(
            iterable,
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            resources.requests().first().getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "?per_page=100")
            )
        );
    }

    /**
     * GitlabRepoLabels can add a label.
     */
    @Test
    public void canAddLabel(){
        final MockJsonResources resources =
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_CREATED,
                    JsonValue.NULL
                )
            );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "/amihaiemil%2Ftestrepo/labels"
        );
        final Labels repoLabels = new GitlabRepoLabels(
            uri, resources
        );
        MatcherAssert.assertThat(
            repoLabels.add("bugLabel"),
            Matchers.is(true)
        );
        MockJsonResources.MockRequest request = resources.requests().first();
        MatcherAssert.assertThat(request.getUri(), Matchers.equalTo(uri));
        MatcherAssert.assertThat(
            request.getMethod(), Matchers.equalTo("POST")
        );
        final JsonObject body = (JsonObject) request.getBody();
        MatcherAssert.assertThat(
            body.getString("name"),
            Matchers.equalTo("bugLabel")
        );
        MatcherAssert.assertThat(
            body.getString("color").length(),
            Matchers.equalTo(7)
        );
    }

    /**
     * GitlabRepoLabels can remove a label. Here, we expect
     * 2 API calls to be made: 1 for fetching all labels and 1 for
     * actually removing the specified label.
     */
    @Test
    public void canRemoveLabel() {
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("name", "bug")
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("id", 2)
                        .add("name", "enhancement")
                        .build())
                    .build()));
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "/amihaiemil%2Ftestrepo/labels"
        );

        new GitlabRepoLabels(
            uri, resources
        ).remove("bug");

        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "?per_page=100")
            )
        );

        MatcherAssert.assertThat(
            resources.requests().atIndex(1).getMethod(),
            Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(1).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "/1")
            )
        );
    }
}
