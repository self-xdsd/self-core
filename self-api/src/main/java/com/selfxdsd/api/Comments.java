package com.selfxdsd.api;

/**
 * Comments related to a particular Provider resource (e.g Issue).
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public interface Comments extends Iterable<Comment> {

    /**
     * Crates a new Comment related to a particular Provider
     * resource (e.g Issue).
     * @param body Comment's content.
     * @return Created Comment.
     * @throws IllegalStateException if Comment was not created.
     */
    Comment post(final String body);

}
