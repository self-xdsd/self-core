package com.selfxdsd.core;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * Integration tests for {@link GithubIssues}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubIssuesITCase {

    /**
     * Fetches an issue.
     */
    @Test
    public void fetchesIssueOk(){
        final URI uri = URI.create(
            "https://api.github.com/repos/amihaiemil/docker-java-api/issues");
        final Issues issues = new GithubIssues(
            new JsonResources.JdkHttp(),
            uri,
            mock(Repo.class),
            mock(Storage.class)
        );
        final Issue issue = issues.getById("346");
        final JsonObject jsonIssue = issue.json();
        assertThat("Add Json Suppliers",
            equalTo(jsonIssue.getString("title")));
        assertThat(346,
            equalTo(jsonIssue.getInt("number")));
        assertThat(issue.assignee(), Matchers.nullValue());
    }

    /**
     * Fetched Issue has assignee.
     */
    @Test
    public void issueHasAssignee() {
        final JsonObject json = Json
            .createObjectBuilder()
            .add("id", 346)
            .add("assignee", Json
                .createObjectBuilder()
                .add("login", "john")
                .build())
            .build();
        final Issue issue = new GithubIssue(
            URI.create("/"),
            () -> json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.assignee(), Matchers.equalTo("john"));
    }

    /**
     * Should return null if issue was not found.
     */
    @Test
    public void fetchesIssueNotFound(){
        final URI uri = URI.create(
            "https://api.github.com/repos/amihaiemil/docker-java-api/issues");
        final Issues issues = new GithubIssues(
            new JsonResources.JdkHttp(),
            uri,
            mock(Repo.class),
            mock(Storage.class)
        );
        final Issue issue = issues.getById("100000");
        assertThat(issue, nullValue());
    }

}
