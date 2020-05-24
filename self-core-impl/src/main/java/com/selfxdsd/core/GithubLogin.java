package com.selfxdsd.core;

import com.selfxdsd.api.Projects;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import java.net.URL;

/**
 * Login implementation for Github.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubLogin extends BaseLogin {
    /**
     * Username from Github.
     */
    private final String username;
    /**
     * Email from Github.
     */
    private final String email;
    /**
     * Avatar URL from Github.
     */
    private final URL avatar;
    /**
     * Github Access token.
     */
    private final String githubToken;

    /**
     * Constructor.
     * @param username Username from Github.
     * @param email Email from Github.
     * @param avatar Avatar URL from Github.
     * @param githubToken Github Access token.
     * @checkstyle ParameterNumber (10 lines)
     */
    public GithubLogin(final String username, final String email,
                       final URL avatar, final String githubToken) {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.githubToken = githubToken;
    }

    @Override
    protected User userCredentials(final Storage storage) {
        return new User() {
            private final String token = githubToken;

            @Override
            public String username() {
                return username;
            }

            @Override
            public String email() {
                return email;
            }

            @Override
            public URL avatar() {
                return avatar;
            }

            @Override
            public Provider provider() {
                return new Github(this, storage);
            }

            @Override
            public Projects projects() {
                return storage.projects().ownedBy(this);
            }
        };
    }
}
