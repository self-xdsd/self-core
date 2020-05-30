package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

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
    public User user(final Storage storage) {
        return new StoredUser(
            this.username,
            this.email,
            Provider.Names.GITHUB,
            this.githubToken,
            storage
        );
    }
}
