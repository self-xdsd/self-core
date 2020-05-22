/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.api.Projects;

import java.net.URL;

/**
 * Self operating at Github.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (200 lines)
 */
public final class SelfCore extends BaseSelf {

    /**
     * Constructor.
     * @param storage Storage for Self.
     */
    public SelfCore(final Storage storage) {
        super(storage);
    }

    @Override
    public User githubLogin(
        final String username, final String email,
        final URL avatar, final String githubToken
    ) {
        return this.signUp(
            new User() {
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
                    return new Github(this, SelfCore.this.storage());
                }

                @Override
                public Projects projects() {
                    return SelfCore.this.storage().projects().ownedBy(this);
                }
            }
        );
    }

    @Override
    public User gitlabLogin(
        final String username, final String email,
        final URL avatar, final String gitLabToken
    ) {
        return this.signUp(
            new User() {
                private final String token = gitLabToken;

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
                    return new Gitlab(this, SelfCore.this.storage());
                }

                @Override
                public Projects projects() {
                    return SelfCore.this.storage().projects().ownedBy(this);
                }
            }
        );
    }

    /**
     * Sign up a given user. Check if we already have him/her
     * in the Storage (from a previous authentication) and register him/her
     * if we do not.
     * @param user User to sing up.
     * @return User.
     */
    private User signUp(final User user) {
        final Storage storage = this.storage();
        User authenticated = storage.users().user(
            user.username(), user.provider().name()
        );
        if(authenticated == null) {
            authenticated = storage.users().signUp(user);
        }
        return authenticated;
    }
}
