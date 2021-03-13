package com.selfxdsd.core;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.URI;


/**
 * Unit test for {@link BitbucketIssue}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.69
 */
public final class BitbucketIssueTestCase {

    /**
     * Has an id.
     */
    @Test
    public void issueId() {
        MatcherAssert.assertThat(
            new BitbucketIssue(
                URI.create("test-issue"),
                Json.createObjectBuilder()
                    .add("id", 1)
                    .build(),
                Mockito.mock(Storage.class),
                Mockito.mock(JsonResources.class)
            ).issueId(),
            Matchers.is("1")
        );
    }

    /**
     * Has provider.
     */
    @Test
    public void provider() {
        MatcherAssert.assertThat(
            new BitbucketIssue(
                URI.create("test-issue"),
                Json.createObjectBuilder()
                    .add("id", 1)
                    .build(),
                Mockito.mock(Storage.class),
                Mockito.mock(JsonResources.class)
            ).provider(),
            Matchers.is(Provider.Names.BITBUCKET)
        );
    }

    /**
     * Has role.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void role() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).role();
    }

    /**
     * Has repo full name.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void repoFullName() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).repoFullName();
    }

    /**
     * Has author.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void author() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).author();
    }

    /**
     * Has body.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void body() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).body();
    }

    /**
     * Has assignee.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void assignee() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).assignee();
    }

    /**
     * Can assign a user.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void assign() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).assign("user");
    }

    /**
     * Can un-assign.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unassign() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).unassign("user");
    }

    /**
     * Has json representation.
     */
    @Test
    public void json() {
        MatcherAssert.assertThat(
            new BitbucketIssue(
                URI.create("test-issue"),
                Json.createObjectBuilder()
                    .add("id", 1)
                    .build(),
                Mockito.mock(Storage.class),
                Mockito.mock(JsonResources.class)
            ).json(),
            Matchers.is(
                Json.createObjectBuilder()
                    .add("id", 1)
                    .build()
            )
        );
    }

    /**
     * Has comments.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void comments() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).comments();
    }

    /**
     * Can close itself.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void close() {
        new BitbucketIssue(
            URI.create("test-issue"),
            Json.createObjectBuilder()
                .add("id", 1)
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).close();
    }

    /**
     * Can reopen.
     */
    @Test
    public void reopen() {
    }

    /**
     * Return isClosed.
     */
    @Test
    public void isClosed() {
    }

    /**
     * Return isPullRequest.
     */
    @Test
    public void isPullRequest() {
    }

    /**
     * Has estimation.
     */
    @Test
    public void estimation() {
    }

    /**
     * Has labels.
     */
    @Test
    public void labels() {
    }
}