package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;

import com.selfxdsd.api.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gitlab Issue Comments.
 *
 * @author andreoss
 * @version $Id$
 * @since 0.0.45
 */
final class GitlabIssueComments implements Comments {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabIssueComments.class
    );

    /**
     * Base Comments uri.
     */
    private final URI commentsUri;

    /**
     * JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param issueUri Comments Issue URI.
     * @param resources Gitlab's JSON Resources.
     */
    GitlabIssueComments(
        final URI issueUri,
        final JsonResources resources
    ) {
        this.commentsUri = issueUri;
        this.resources = resources;
    }

    @Override
    public Comment post(final String body) {
        LOG.debug("Posting Comment to: [{}].", this.commentsUri);
        final Resource resource = resources.post(
            this.commentsUri,
            Json.createObjectBuilder().add("body", body).build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_CREATED) {
            return new GitlabIssueComment(resource.asJsonObject());
        } else {
            LOG.error(
                "Expected status 201 CREATED, but got: [{}].",
                resource.statusCode()
            );
            throw new IllegalStateException(
                String.format(
                    "Gitlab Issue Comment was not created. Status is %d",
                    resource.statusCode()
                )
            );
        }
    }

    @Override
    public Comment received(final JsonObject comment) {
        return new GitlabIssueComment(comment);
    }

    @Override
    public Iterator<Comment> iterator() {
        final Resource resource = resources.get(this.commentsUri);
        final List<Comment> comments;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            comments = resource.asJsonArray()
                .stream()
                .map(JsonObject.class::cast)
                .map(this::received)
                .collect(Collectors.toList());
        } else {
            throw new IllegalStateException(
                String.format(
                    "Invalid response status: %d for %s",
                    resource.statusCode(),
                    this.commentsUri
                )
            );
        }
        return comments.iterator();
    }
}
