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

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabIssues}.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
public final class GitlabIssuesTestCase {


    /**
     * GitlabIssues.getById(...) can get an issue by its id.
     */
    @Test
    public void getsIssueById() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                JsonValue.EMPTY_JSON_OBJECT
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).getById("1");

        final MockJsonResources.MockRequest req = resources.requests().first();

        MatcherAssert.assertThat(req.getUri().toString(), Matchers
            .equalTo("https://gitlab.com/api/v4/projects/john%2Ftest/issues/1")
        );
        MatcherAssert.assertThat(req.getMethod(), Matchers
            .equalTo("GET")
        );
        MatcherAssert.assertThat(issue, Matchers.allOf(
            Matchers.notNullValue(),
            Matchers.instanceOf(WithContributorLabel.class)
        ));
    }

    /**
     * GitlabIssues.getById(...) can't get an issue by its id.
     */
    @Test
    public void getIssueByIdIsNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * GitlabIssues.getById(...) returns null when response code is
     * no content.
     */
    @Test
    public void getsIssueByIdNoContent() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NO_CONTENT,
                JsonValue.NULL
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * GitlabIssues.getById(...) throws IllegalStateException when response
     * code is other then HTTP_OK, HTTP_NOT_FOUND or HTTP_NO_CONTENT.
     */
    @Test(expected = IllegalStateException.class)
    public void getIssueByIdThrowsWhenResponseCodeNotProcessed() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_UNAVAILABLE,
                JsonValue.NULL
            )
        );
        new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).getById("1");
    }

    /**
     * GitlabIssues.received(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void receivedIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).received(JsonObject.EMPTY_JSON_OBJECT);
    }

    /**
     * GitlabIssues.open(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void openIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).open("", "", "");
    }

    /**
     * GitlabIssues.getById(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void searchIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).search("", "");
    }

    /**
     * Iterating over all Gitlab issues is not allowed.
     */
    @Test(expected = IllegalStateException.class)
    public void iterationOverAllIssuesNotAllowed() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).iterator();
    }
}
