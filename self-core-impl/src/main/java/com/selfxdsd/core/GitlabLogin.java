package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Provider;

/**
 * Login implementation for Gitlab.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabLogin implements Login {

    /**
     * Username from Gitlab.
     */
    private final String username;
    /**
     * Email from Gitlab.
     */
    private final String email;

    /**
     * Gitlab Access token.
     */
    private final String gitlabToken;

    /**
     * Constructor.
     * @param username Username from Gitlab.
     * @param email Email from Gitlab.
     * @param gitlabToken Gitlab Access token.
     * @checkstyle ParameterNumber (10 lines)
     */
    public GitlabLogin(
        final String username, final String email, final String gitlabToken
    ) {
        this.username = username;
        this.email = email;
        this.gitlabToken = gitlabToken;
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
        return this.gitlabToken;
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
        return Provider.Names.GITLAB;
    }
}
