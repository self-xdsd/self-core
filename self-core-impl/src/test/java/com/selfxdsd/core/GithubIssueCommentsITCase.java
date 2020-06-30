package com.selfxdsd.core;

import org.junit.Test;
import org.mockito.Mockito;

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
    @Test(expected = UnsupportedOperationException.class)
    public void iteratesIssueComments(){
        new GithubIssueComments(
            URI.create("/"),
            Mockito.mock(JsonResources.class)
        ).iterator();
    }

    /**
     * Issue can have a comment posted on Github.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void postsIssueCommentOk(){
        new GithubIssueComments(
            URI.create("/"),
            Mockito.mock(JsonResources.class)
        ).post("");
    }
}
