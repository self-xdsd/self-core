/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
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
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import java.net.URI;

/**
 * Github as a Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #20:30min Implemented method repo(). Write unit tests,
 *  as well as integration tests for it. This method assumes that
 *  the repo in question is a user-owned and public.
 */
final class Github implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Github's URI.
     */
    private final URI uri;

    /**
     * Constructor.
     * @param user Authenticated user.
     */
    Github(final User user) {
        this(user, URI.create("https://api.github.com"));
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param uri Base URI of Github's API.
     */
    Github(final User user, final URI uri) {
        this.user = user;
        this.uri = uri;
    }

    @Override
    public String name() {
        return "github";
    }

    @Override
    public Repo repo(final String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
