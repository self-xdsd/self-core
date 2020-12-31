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
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Unit tests for {@link GitlabIssueLabels}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 */
public final class GitlabIssueLabelsTestCase {

    /**
     * GitlabIssueLabels can add some labels.
     */
    @Test
    public void canAddLabels() {
        final MockJsonResources resources =
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    JsonValue.NULL
                )
            );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "/amihaiemil%2Ftestrepo/issues/1"
        );
        final Labels labels = new GitlabIssueLabels(
            uri,
            resources,
            Json.createObjectBuilder().add("id", 1).build()
        );
        MatcherAssert.assertThat(
            labels.add("blue", "red", "green"),
            Matchers.is(Boolean.TRUE)
        );
        MockJsonResources.MockRequest addBlueLabel = resources.requests()
            .atIndex(0);
        MatcherAssert.assertThat(
            addBlueLabel.getUri(),
            Matchers.equalTo(
                URI.create(
                    "https://gitlab.com/api/v4/projects/"
                    + "/amihaiemil%2Ftestrepo/labels"
                )
            )
        );
        MatcherAssert.assertThat(
            addBlueLabel.getMethod(), Matchers.equalTo("POST")
        );
        final JsonObject body = (JsonObject) addBlueLabel.getBody();
        MatcherAssert.assertThat(
            body.getString("name"),
            Matchers.equalTo("blue")
        );
        MatcherAssert.assertThat(
            body.getString("color").length(),
            Matchers.equalTo(7)
        );

        MockJsonResources.MockRequest addIssueLabels = resources.requests()
            .atIndex(3);
        MatcherAssert.assertThat(
            addIssueLabels.getUri(), Matchers.equalTo(uri)
        );
        MatcherAssert.assertThat(
            addIssueLabels.getMethod(), Matchers.equalTo("PUT")
        );
        final JsonObject bodyAddIssueLabel = (JsonObject) addIssueLabels
            .getBody();
        MatcherAssert.assertThat(
            bodyAddIssueLabel.getString("add_labels"),
            Matchers.equalTo("blue,red,green")
        );
    }

    /**
     * GitlabIssueLabels can remove a label.
     */
    @Test
    public void canRemoveLabel() {
        final MockJsonResources resources =
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    JsonValue.NULL
                )
            );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "/amihaiemil%2Ftestrepo/issues/1"
        );
        final Labels labels = new GitlabIssueLabels(
            uri,
            resources,
            Json.createObjectBuilder().add("id", 1).build()
        );
        MatcherAssert.assertThat(
            labels.remove("green"),
            Matchers.is(Boolean.TRUE)
        );
        MockJsonResources.MockRequest request = resources.requests().first();
        MatcherAssert.assertThat(request.getUri(), Matchers.equalTo(uri));
        MatcherAssert.assertThat(
            request.getMethod(), Matchers.equalTo("PUT")
        );
        final JsonObject body = (JsonObject) request.getBody();
        MatcherAssert.assertThat(
            body.getString("remove_labels"),
            Matchers.equalTo("green")
        );
    }

    /**
     * GitlabIssueLabels can be iterated.
     */
    @Test
    public void canBeIterated() {
        final JsonObject issue = Json.createObjectBuilder()
            .add("id", 1)
            .add(
                "labels",
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder().add("name", "blue"))
                    .add(Json.createObjectBuilder().add("name", "green"))
                    .add(Json.createObjectBuilder().add("name", "red"))
            ).build();
        final Labels labels = new GitlabIssueLabels(
            URI.create(
                "https://gitlab.com/api/v4/projects/"
                + "/amihaiemil%2Ftestrepo/issues/1"
            ),
            Mockito.mock(JsonResources.class),
            issue
        );
        MatcherAssert.assertThat(
            labels,
            Matchers.iterableWithSize(3)
        );
        final Iterator<Label> labelsIt = labels.iterator();
        MatcherAssert.assertThat(
            labelsIt.next().name(),
            Matchers.equalTo("blue")
        );
        MatcherAssert.assertThat(
            labelsIt.next().name(),
            Matchers.equalTo("green")
        );
        MatcherAssert.assertThat(
            labelsIt.next().name(),
            Matchers.equalTo("red")
        );
    }

}
