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
package com.selfxdsd.api;

import java.net.URL;

/**
 * The Self Platform. This is the highest abstraction.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Self {

    /**
     * Get the User authenticated via Github OAuth2.
     * @param username Username from Github.
     * @param email Email from Github.
     * @param avatar Avatar URL from Github.
     * @param githubToken Github Access token.
     * @return User.
     * @checkstyle ParameterNumber (10 lines)
     */
    User githubLogin(
        final String username, final String email,
        final URL avatar, final String githubToken
    );

    /**
     * Get the User authenticated via GitLab OAuth2.
     * @param username Username from GitLab.
     * @param email Email from GitLab.
     * @param avatar Avatar URL from GitLab.
     * @param gitLabToken GitLab Access token.
     * @return User.
     * @checkstyle ParameterNumber (10 lines)
     */
    User gitlabLogin(
        final String username, final String email,
        final URL avatar, final String gitLabToken
    );

}
