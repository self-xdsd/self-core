package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Provider;

/**
 * Login implementation for Github.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubLogin implements Login {
    /**
     * Username from Github.
     */
    private final String username;
    /**
     * Email from Github.
     */
    private final String email;

    /**
     * Github Access token.
     */
    private final String githubToken;

    /**
     * Constructor.
     * @param username Username from Github.
     * @param email Email from Github.
     * @param githubToken Github Access token.
     * @checkstyle ParameterNumber (10 lines)
     */
    public GithubLogin(
        final String username, final String email, final String githubToken
    ) {
        this.username = username;
        this.email = email;
        this.githubToken = githubToken;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String email() {
        return this.email;
    }

    @Override
    public String accessToken() {
        return this.githubToken;
    }

    @Override
    public String role() {
        final String role;
        if("amihaiemil".equals(this.username)) {
            role = "admin";
        } else {
            role = "user";
        }
        return role;
    }

    @Override
    public String provider() {
        return Provider.Names.GITHUB;
    }
}
