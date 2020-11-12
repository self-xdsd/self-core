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

import com.selfxdsd.api.Label;
import com.selfxdsd.api.storage.Labels;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonValue;
import java.net.URI;

/**
 * Unit tests for {@link GithubIssueLabels}.
 * @author criske
 * @version $Id$
 * @since 0.0.30
 */
public final class GithubIssueLabelsTestCase {

    /**
     * A GithubIssueLabels can be iterated.
     */
    @Test
    public void canIterateOverLabels() {
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(200,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("name", "bug")
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("name", "enhancement")
                        .build())
                    .build()));
        final URI uri = URI.create("https://api.github.com/repos/amihaiemil"
            + "/docker-java-api/issues/labels");

        final Iterable<Label> iterable =
            () -> new GithubIssueLabels(uri, resources).iterator();

        MatcherAssert.assertThat(iterable,
            Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(resources.requests().first().getUri(),
            Matchers.equalTo(uri));
    }

    /**
     * A GithubIssueLabels can add new labels to existing ones.
     */
    @Test
    public void canAddLabels(){
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(200,
                JsonValue.NULL));
        final URI uri = URI.create("https://api.github.com/repos/amihaiemil"
            + "/docker-java-api/issues/labels");

        final Labels issueLabels = new GithubIssueLabels(uri, resources);
        MatcherAssert.assertThat(issueLabels.add("bug", "feature"),
            Matchers.is(true));
        MockJsonResources.MockRequest request = resources.requests().first();
        MatcherAssert.assertThat(request.getUri(), Matchers.equalTo(uri));
        MatcherAssert.assertThat(request.getBody(), Matchers.equalTo(Json
            .createObjectBuilder()
            .add("labels", Json.createArrayBuilder()
                .add("bug")
                .add("feature")
                .build()
            ).build()));
    }

    /**
     * A GithubIssueLabels can remove an Issue Label.
     */
    @Test
    public void canRemoveLabel(){
        final MockJsonResources resources =
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    200,
                    JsonValue.NULL
                )
            );

        final URI issueLabelsUri = URI.create(
            "https://api.github.com/repos/amihaiemil"
            + "/docker-java-api/issues/labels"
        );

        final Labels issueLabels = new GithubIssueLabels(
            issueLabelsUri, resources
        );
        MatcherAssert.assertThat(
            issueLabels.remove("bug"),
            Matchers.is(true)
        );
        MockJsonResources.MockRequest request = resources.requests().first();
        MatcherAssert.assertThat(
            request.getMethod(), Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            request.getUri(),
            Matchers.equalTo(URI.create(issueLabelsUri.toString() + "/bug"))
        );
        MatcherAssert.assertThat(
            request.getBody(),
            Matchers.equalTo(Json.createObjectBuilder().build())
        );
    }
}
