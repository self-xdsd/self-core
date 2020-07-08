package com.selfxdsd.core;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link AccessToken} implementations.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class AccessTokenTestCase {

    /**
     * Github token representation.
     */
    @Test
    public void representsGithubAccessToken(){
        final AccessToken token = new AccessToken.Github("github123");
        MatcherAssert.assertThat(token.header(),
            Matchers.equalTo("Authorization"));
        MatcherAssert.assertThat(token.value(),
            Matchers.equalTo("token github123"));
    }

    /**
     * Gitlab token representation.
     */
    @Test
    public void representsGitlabAccessToken(){
        final AccessToken token = new AccessToken.Gitlab("gitlab123");
        MatcherAssert.assertThat(token.header(),
            Matchers.equalTo("Private-Token"));
        MatcherAssert.assertThat(token.value(),
            Matchers.equalTo("gitlab123"));
    }
}
