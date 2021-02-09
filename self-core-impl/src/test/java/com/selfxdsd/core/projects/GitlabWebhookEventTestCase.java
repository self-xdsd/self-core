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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GitlabWebhookEvent}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.61
 */
public final class GitlabWebhookEventTestCase {

    /**
     * It can return the NEW_ISSUE type for a newly opened Issue.
     */
    @Test
    public void typeNewIssueForOpenedIssue() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Issue Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.NEW_ISSUE)
        );
    }

    /**
     * It can return the NEW_ISSUE type for a newly opened Merge Request.
     */
    @Test
    public void typeNewIssueForOpenedMergeRequest() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Merge Request Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.NEW_ISSUE)
        );
    }

    /**
     * It can return the REOPENED_ISSUE type for a newly opened Issue.
     */
    @Test
    public void typeReopenedIssueForReopenedIssue() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Issue Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "reopen")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.REOPENED_ISSUE)
        );
    }

    /**
     * It can return the REOPENED_ISSUE type for a newly opened Merge Request.
     */
    @Test
    public void typeReopenedIssueForReopenedMergeRequest() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Merge Request Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "reopen")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.REOPENED_ISSUE)
        );
    }

    /**
     * It returns the original type ("Issue Hook") if the Issue's
     * state is not opened or reopened.
     */
    @Test
    public void originalTypeForOtherIssueState() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Issue Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "closed")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo("Issue Hook")
        );
    }

    /**
     * It returns the original type ("Merge Request Hook") if the MR's
     * state is not opened or reopened.
     */
    @Test
    public void originalTypeForOtherMergeRequestState() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Merge Request Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("action", "closed")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo("Merge Request Hook")
        );
    }

    /**
     * It returns the original type if the hook type is not Issue/MR Hook or
     * Note Hook (Comment).
     */
    @Test
    public void originalTypeOnNotIssueAndNotComment() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Commit Hook",
            "{}"
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo("Commit Hook")
        );
    }

    /**
     * It can return the ISSUE_COMMENT type for a comment on Issue.
     */
    @Test
    public void typeIssueCommentForIssueComment() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Issue")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.ISSUE_COMMENT)
        );
    }

    /**
     * It can return the ISSUE_COMMENT type for a comment on MR.
     */
    @Test
    public void typeIssueCommentForMergeRequestComment() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "MergeRequest")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo(Event.Type.ISSUE_COMMENT)
        );
    }

    /**
     * It can return the original type for comments
     * on other entities than Issue or MR.
     */
    @Test
    public void originalTypeForOtherComment() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Commit")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.type(),
            Matchers.equalTo("Note Hook")
        );
    }

    /**
     * It returns the Project.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Commit Hook",
            "{}"
        );
        MatcherAssert.assertThat(
            gitlabEvent.project(),
            Matchers.is(project)
        );
    }

    /**
     * We can retrieve the Issue from an Issue Hook event.
     */
    @Test
    public void returnsIssueFromIssueEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.FALSE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Issue Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
    }

    /**
     * The Issue from an Issue event is cached.
     */
    @Test
    public void issueFromIssueEventIsCached() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.FALSE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Issue Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        Mockito.verify(
            project.projectManager(), Mockito.times(1)
        ).provider();
    }

    /**
     * We can retrieve the Issue from an Merge Request Hook event.
     */
    @Test
    public void returnsIssueFromMergeRequestEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.TRUE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Merge Request Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
    }

    /**
     * Issue from MR Event is cached.
     */
    @Test
    public void issueFromMergeRequestEventIsCached() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.TRUE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Merge Request Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                        .add("action", "open")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        Mockito.verify(
            project.projectManager(), Mockito.times(1)
        ).provider();
    }

    /**
     * We can retrieve the Issue from an issue comment event.
     */
    @Test
    public void returnsIssueFromIssueCommentEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.FALSE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Issue")
                )
                .add(
                    "issue",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
    }

    /**
     * Issue from an Issue Comment event is cached.
     */
    @Test
    public void issueFromIssueCommentIsCached() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.FALSE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Issue")
                )
                .add(
                    "issue",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        Mockito.verify(
            project.projectManager(), Mockito.times(1)
        ).provider();
    }

    /**
     * We can retrieve the Issue from an merge request comment event.
     */
    @Test
    public void returnsIssueFromMergeRequestCommentEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.TRUE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "MergeRequest")
                )
                .add(
                    "merge_request",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
    }

    /**
     * Issue from MR Comment Event is cached.
     */
    @Test
    public void issueFromMergeRequestCommentIsCached() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.TRUE);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "MergeRequest")
                )
                .add(
                    "merge_request",
                    Json.createObjectBuilder()
                        .add("iid", 1)
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.is(issue)
        );
        Mockito.verify(
            project.projectManager(), Mockito.times(1)
        ).provider();
    }

    /**
     * Issue is null if the comment event is from something else
     * other than Issue or MergeRequest (e.g. commit comment).
     */
    @Test
    public void noIssueOnOtherCommentEvent() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Commit")
                )
                .build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.nullValue()
        );
    }

    /**
     * Issue is null if the event is other then Issue/MR Hook or Note Hook.
     */
    @Test
    public void noIssueOnUnknownEvent() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Other Hook",
            Json.createObjectBuilder()
                .build()
                .toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.issue(),
            Matchers.nullValue()
        );
    }

    /**
     * We can retrieve the Comment from an issue comment event.
     */
    @Test
    public void returnsCommentFromIssueCommentEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.FALSE);
        this.mockComment(issue);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "user",
                    Json.createObjectBuilder().add("username", "mihai")
                ).add(
                "object_attributes",
                Json.createObjectBuilder()
                    .add("id", 123)
                    .add("note", "Issue comment here...")
                    .add("noteable_type", "Issue")
            ).add(
                "issue",
                Json.createObjectBuilder().add("iid", 1)
            ).build().toString()
        );
        final Comment comment = gitlabEvent.comment();
        MatcherAssert.assertThat(
            comment.commentId(),
            Matchers.equalTo("123")
        );
        MatcherAssert.assertThat(
            comment.author(),
            Matchers.equalTo("mihai")
        );
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.equalTo("Issue comment here...")
        );
    }

    /**
     * We can retrieve the Comment from an MR comment event.
     */
    @Test
    public void returnsCommentFromMergeRequestCommentEvent() {
        final Project project = Mockito.mock(Project.class);
        final Issue issue = this.mockIssue(project, "1", Boolean.TRUE);
        this.mockComment(issue);

        final Event gitlabEvent = new GitlabWebhookEvent(
            project,
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "user",
                    Json.createObjectBuilder().add("username", "mihai")
                ).add(
                "object_attributes",
                Json.createObjectBuilder()
                    .add("id", 123)
                    .add("note", "MR comment here...")
                    .add("noteable_type", "MergeRequest")
            ).add(
                "merge_request",
                Json.createObjectBuilder().add("iid", 1)
            ).build().toString()
        );
        final Comment comment = gitlabEvent.comment();
        MatcherAssert.assertThat(
            comment.commentId(),
            Matchers.equalTo("123")
        );
        MatcherAssert.assertThat(
            comment.author(),
            Matchers.equalTo("mihai")
        );
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.equalTo("MR comment here...")
        );
    }

    /**
     * The comment is null if the Note Hook event is on other
     * entities than Issue or MergeRequest.
     */
    @Test
    public void commentIsNullOnOtherCommentEvent() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Note Hook",
            Json.createObjectBuilder()
                .add(
                    "object_attributes",
                    Json.createObjectBuilder()
                        .add("noteable_type", "Commit")
                ).build().toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.comment(),
            Matchers.nullValue()
        );
    }

    /**
     * The comment is null if the event is not a Note Hook event.
     */
    @Test
    public void commentIsNullOnOtherEvent() {
        final Event gitlabEvent = new GitlabWebhookEvent(
            Mockito.mock(Project.class),
            "Other Hook",
            Json.createObjectBuilder()
                .build()
                .toString()
        );
        MatcherAssert.assertThat(
            gitlabEvent.comment(),
            Matchers.nullValue()
        );
    }

    /**
     * Mock an Issue/Merge Request for Test.
     * @param project Project where the Issue is coming from.
     * @param iid Internal ID of the Issue/MR.
     * @param pullRequest Is it a pull request?
     * @return Issue.
     */
    public Issue mockIssue(
        final Project project,
        final String iid,
        final boolean pullRequest
    ) {
        final Issue issue = Mockito.mock(Issue.class);

        final Issues all = Mockito.mock(Issues.class);
        Mockito.when(all.getById(iid)).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        if(pullRequest) {
            Mockito.when(repo.pullRequests()).thenReturn(all);
        } else {
            Mockito.when(repo.issues()).thenReturn(all);
        }

        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        final Provider gitlab = Mockito.mock(Provider.class);
        Mockito.when(
            gitlab.repo(
                project.repoFullName().split("/")[0],
                project.repoFullName().split("/")[1]
            )
        ).thenReturn(repo);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(gitlab);
        Mockito.when(project.projectManager()).thenReturn(manager);

        return issue;
    }

    /**
     * Mock an Issue/Merge Request for Test.
     * @param issue Issue where the Comment has been posted.
     */
    public void mockComment(final Issue issue) {
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(comments.received(Mockito.any(JsonObject.class)))
            .thenAnswer(
                new Answer<Comment>() {
                    @Override
                    public Comment answer(
                        final InvocationOnMock invocationOnMock
                    ) {
                        return new Comment() {
                            /**
                             * JsonObject comment.
                             */
                            private final JsonObject json = (JsonObject)
                                invocationOnMock.getArguments()[0];

                            @Override
                            public String commentId() {
                                return String.valueOf(this.json.getInt("id"));
                            }

                            @Override
                            public String author() {
                                return this.json.getJsonObject("author")
                                    .getString("username");
                            }

                            @Override
                            public String body() {
                                return this.json.getString("body");
                            }

                            @Override
                            public JsonObject json() {
                                return this.json;
                            }
                        };
                    }
                }
            );
    }

    /**
     * It can return the Commit if the event is 'Push Hook'.
     */
    @Test
    public void returnsCommitIfPushHook() {
        final Commit commit = Mockito.mock(Commit.class);
        final Commits commits = Mockito.mock(Commits.class);
        Mockito.when(commits.getCommit("sha123")).thenReturn(commit);

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.commits()).thenReturn(commits);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/repo");
        final Provider github = Mockito.mock(Provider.class);
        Mockito.when(github.repo("mihai", "repo")).thenReturn(repo);

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(github);
        Mockito.when(project.projectManager()).thenReturn(manager);

        MatcherAssert.assertThat(
            new GitlabWebhookEvent(
                project,
                "Push Hook",
                Json.createObjectBuilder()
                    .add(
                        "commits",
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder()
                                    .add("id", "sha123")
                                    .build()
                            )
                    ).build().toString()
            ).commit(),
            Matchers.is(commit)
        );
    }

    /**
     * It returns the null Commit if the event is NOT 'Push Hook'.
     */
    @Test
    public void returnsNullCommitIfNotPushHook() {
        MatcherAssert.assertThat(
            new GitlabWebhookEvent(
                Mockito.mock(Project.class),
                "Not Push Hook",
                Json.createObjectBuilder()
                    .build()
                    .toString()
            ).commit(),
            Matchers.nullValue()
        );
    }

}
