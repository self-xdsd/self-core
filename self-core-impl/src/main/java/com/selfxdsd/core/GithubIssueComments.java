package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;

import java.net.URI;
import java.util.Iterator;

/**
 * Github Issue Comments.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 * @todo #244:30min Implement and test the remaining methods
 *  in GithubIssueComments.
 */
public final class GithubIssueComments implements Comments {

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
     * @param issueUri Comments Issue URI.
     * @param resources Github's JSON Resources.
     */
    public GithubIssueComments(final URI issueUri,
                               final JsonResources resources) {
        this.commentsUri = issueUri.resolve("comments");
        this.resources = resources;
    }

    @Override
    public Comment post(final String body) {
        //POST /repos/:owner/:repo/issues/:issue_number/comments
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterator<Comment> iterator() {
        //GET /repos/:owner/:repo/issues/:issue_number/comments
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
