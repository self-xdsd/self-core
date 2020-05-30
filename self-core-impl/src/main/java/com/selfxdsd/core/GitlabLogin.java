package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

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
    public User user(final Storage storage) {
        return new StoredUser(
            this.username,
            this.email,
            Provider.Names.GITLAB,
            this.gitlabToken,
            storage
        );
    }
}
