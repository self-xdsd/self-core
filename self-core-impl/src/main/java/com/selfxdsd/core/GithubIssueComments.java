package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Github Issue Comments.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.8
 * @todo #100:30min Provide a pagination solution in iterator() using
 *  Github response headers, when {@link Resource} API has access to http
 *  response headers (issue #241).
 */
final class GithubIssueComments implements Comments {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubIssueComments.class
    );

    /**
     * Base Comments uri.
     */
    private final URI commentsUri;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     *
     * @param issueUri Comments Issue URI.
     * @param resources Github's JSON Resources.
     */
    GithubIssueComments(
        final URI issueUri,
        final JsonResources resources
    ) {
        final String issueUriStr = issueUri.toString();
        String slash = "/";
        if(issueUriStr.endsWith("/")){
            slash = "";
        }
        this.commentsUri = URI.create(issueUriStr + slash + "comments");
        this.resources = resources;
    }

    @Override
    public Comment post(final String body) {
        LOG.debug("Posting Comment to: [" + this.commentsUri + "].");
        final Resource resource = resources.post(
            this.commentsUri,
            Json.createObjectBuilder().add("body", body).build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_CREATED) {
            return new GithubComment(resource.asJsonObject());
        } else {
            LOG.error(
                "Expected status 201 CREATED, but got: ["
                + resource.statusCode() + "]."
            );
            throw new IllegalStateException(
                "Github Issue Comment was not created. Status is "
              + resource.statusCode()
            );
        }
    }

    @Override
    public Comment received(final JsonObject comment) {
        return new GithubComment(comment);
    }

    @Override
    public Iterator<Comment> iterator() {
        final Resource resource = resources.get(this.commentsUri);
        final List<Comment> comments;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            comments = resource.asJsonArray()
                .stream()
                .map(JsonObject.class::cast)
                .map(GithubComment::new)
                .collect(Collectors.toList());
        } else {
            comments = List.of();
        }
        return comments.iterator();
    }
}
