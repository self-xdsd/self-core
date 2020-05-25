package com.selfxdsd.core.issues;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

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
        final Issues issues = new GithubIssues(uri, mock(Storage.class));
        final JsonObject jsonIssue = issues.getById(346).json();
        assertThat("Add Json Suppliers",
            equalTo(jsonIssue.getString("title")));
        assertThat(346,
            equalTo(jsonIssue.getInt("number")));
    }

    /**
     * Should return null if issue was not found.
     */
    @Test
    public void fetchesIssueNotFound(){
        final URI uri = URI.create(
            "https://api.github.com/repos/amihaiemil/docker-java-api/issues");
        final Issues issues = new GithubIssues(uri, mock(Storage.class));
        final Issue issue = issues.getById(100000);
        assertThat(issue, nullValue());
    }

}
