package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.net.URI;

/**
 * Integration tests for {@link GithubIssueComments}.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class GithubIssueCommentsITCase {


    /**
     * Issue comments can be fetched from Github as json array.
     */
    @Test
    public void iteratesIssueComments(){
        final URI issueUri = URI.create(
            "https://api.github.com/repos/octocat/Hello-World/issues/1/"
        );
        final JsonResources resources = Mockito.mock(JsonResources.class);

        Mockito
            .when(resources.get(Mockito.any(URI.class)))
            .thenAnswer(invocation -> {
                final URI uri = (URI) invocation.getArguments()[0];
                final JsonArray array = Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("url", uri.resolve("1").toString())
                        .add("body", "Comment issue #1")
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("id", 2)
                        .add("url", uri.resolve("2").toString())
                        .add("body", "Comment issue #2")
                        .build())
                    .build();
                return buildResource(200, null, array);
            });

        final Iterable<Comment> iterable  =
            () -> new GithubIssueComments(issueUri, resources).iterator();

        MatcherAssert.assertThat(iterable, Matchers.iterableWithSize(2));

        final Comment firstComment = iterable.iterator().next();

        MatcherAssert.assertThat(firstComment.commentId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(firstComment.body(),
            Matchers.equalTo("Comment issue #1"));
        MatcherAssert.assertThat(firstComment.json().getString("url"),
            Matchers.equalTo(issueUri.resolve("comments/1").toString()));
    }

    /**
     * Iterates nothing if issue comments are not found on Github.
     */
    @Test
    public void iteratesNothingIfIssueCommentsNotFound(){
        final URI issueUri = URI.create(
            "https://api.github.com/repos/octocat/Hello-World/issues/1/"
        );
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final Resource resource = buildResource(404, null, null);
        Mockito
            .when(resources.get(Mockito.any(URI.class)))
            .thenReturn(resource);
        final Iterable<Comment> iterable  =
            () -> new GithubIssueComments(issueUri, resources).iterator();

        MatcherAssert.assertThat(iterable, Matchers.emptyIterable());
    }

    /**
     * Issue can have a comment posted on Github.
     */
    @Test
    public void postsIssueCommentOk() {
        final URI issueUri = URI.create(
            "https://api.github.com/repos/octocat/Hello-World/issues/1/"
        );
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final Comments issueComments =
            new GithubIssueComments(issueUri, resources);

        Mockito
            .when(resources.post(
                Mockito.any(URI.class),
                Mockito.any(JsonObject.class)
            ))
            .thenAnswer(invocation -> {
                final URI uri = ((URI) invocation.getArguments()[0])
                    .resolve("1");
                final String body = ((JsonObject) invocation
                    .getArguments()[1]).getString("body");
                final JsonObject object = Json.createObjectBuilder()
                    .add("id", 1)
                    .add("url", uri.toString())
                    .add("body", body)
                    .build();
                return buildResource(201, object, null);
            });

        final Comment comment = issueComments
            .post("Comment issue #1");

        MatcherAssert.assertThat(comment.commentId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(comment.body(),
            Matchers.equalTo("Comment issue #1"));
        MatcherAssert.assertThat(comment.json().getString("url"),
            Matchers.equalTo(issueUri.resolve("comments/1").toString()));
    }

    /**
     * Throws if comment not created (unauthorized, server down,
     * no network etc...).
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIfCommentNotCreated() {
        final URI issueUri = URI.create(
            "https://api.github.com/repos/octocat/Hello-World/issues/1/"
        );
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final Comments issueComments =
            new GithubIssueComments(issueUri, resources);
        final Resource resource = buildResource(401, null, null);

        Mockito
            .when(resources.post(
                Mockito.any(URI.class),
                Mockito.any(JsonObject.class)
            ))
            .thenReturn(resource);

        issueComments
            .post("Comment issue #1");
    }

    /**
     * Builds a Resource response.
     * @param status Status code.
     * @param object JSON Object.
     * @param array JSON Array.
     * @return Resource.
     */
    private Resource buildResource(final int status,
                                   final JsonObject object,
                                   final JsonArray array){
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.statusCode()).thenReturn(status);
        Mockito.when(resource.asJsonObject()).thenReturn(object);
        Mockito.when(resource.asJsonArray()).thenReturn(array);
        return resource;
    }
}
