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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;

/**
 * Unit tests for {@link GitlabIssue}.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
public final class GitlabIssueTestCase {

    /**
     * Gitlab Issue can return its ID.
     */
    @Test
    public void returnsId() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("iid", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.issueId(), Matchers.equalTo("1"));
    }

    /**
     * Gitlab Issue can return its provider.
     */
    @Test
    public void returnsProvider() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("iid", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.provider(), Matchers.equalTo("gitlab"));
    }

    /**
     * Gitlab Issue can return the DEV role when it is not a PR.
     */
    @Test
    public void returnsDevRole() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/issues/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * Gitlab Issue can return the REV role when it is a PR.
     */
    @Test
    public void returnsRevRole() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/merge_requests/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.role(),
            Matchers.equalTo(Contract.Roles.REV)
        );
    }

    /**
     * GitlabIssue can return the fullName of the Repo it belongs to,
     * from an Issue reference object.
     */
    @Test
    public void returnsRepoFullNameFromIssueReference() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder()
                .add("references",
                    Json.createObjectBuilder()
                        .add("full", "john/test#1")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * GitlabIssue can return the fullName of the Repo it belongs to,
     * from an Merge Request reference object.
     */
    @Test
    public void returnsRepoFullNameFromMergeRequestReference() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("references",
                    Json.createObjectBuilder()
                        .add("full", "john/test!1")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * GitlabIssue can return its author.
     */
    @Test
    public void returnsAuthor() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("author",
                    Json.createObjectBuilder()
                        .add("username", "amihaiemil")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.author(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * GitlabIssue can return its assignee.
     */
    @Test
    public void returnsAssigneeUsername() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("assignee",
                    Json.createObjectBuilder()
                        .add("username", "amihaiemil")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * GitlabIssue can return no assignee.
     */
    @Test
    public void returnsNoAssignee() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.nullValue()
        );
    }

    /**
     * GitlabIssue can return its body.
     */
    @Test
    public void returnsBody() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("description", "Issue description here.")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.body(),
            Matchers.equalTo("Issue description here.")
        );
    }

    /**
     *GitlabIssue.comments() is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void commentsIsNotImplemented(){
        new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).comments();
    }

    /**
     * An Issue should have an estimation of 60 minutes.
     */
    @Test
    public void returnsIssueEstimation() {
        final int estimation = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/issues/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).estimation();
        MatcherAssert.assertThat(estimation, Matchers.is(60));
    }

    /**
     * A Pull Request should have an estimation of 30 minutes.
     */
    @Test
    public void returnsPullRequestEstimation() {
        final int estimation = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/merge_requests/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).estimation();
        MatcherAssert.assertThat(estimation, Matchers.is(30));
    }

    /**
     *GitlabIssue.assign(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void assignIsNotImplemented() {
        new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).assign("");
    }

    
    /**
     * GitlabIssue.unassign(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unassignIsNotImplemented() {
        new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).unassign("");
    }

    

    /**
     * Issue.close() is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void closeIsNotImplemented() {
        new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).close();
    }
    
    /**
     * Issue.reopen() is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void reopenIsNotImplemented() {
        new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).reopen();
    }
    
    /**
     * GitlabIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedTrue() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("state", "closed").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * GitlabIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedFalse() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("state", "open").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * GitlabIssue can return its wrapped json object.
     */
    @Test
    public void returnsItsJson(){
        final JsonObject json = JsonObject.EMPTY_JSON_OBJECT;
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.json(),
            Matchers.equalTo(json)
        );
    }

    /**
     * GitlabIssue can return its Labels.
     */
    @Test
    public void returnsLabels() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.labels(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssueLabels.class)
            )
        );
    }
}
