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

import com.selfxdsd.api.Projects;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import java.util.Objects;

/**
 * User stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredUser implements User {

    /**
     * Username.
     */
    private final String username;

    /**
     * E-Mail address.
     */
    private final String email;

    /**
     * Role in Self.
     */
    private final String role;

    /**
     * Provider's name.
     */
    private final String provider;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param username Username.
     * @param email E-Mail.
     * @param role User's role in Self (simple user, admin etc).
     * @param provider Provider's name (github, gitlab etc).
     * @param storage Self's Storage.
     */
    public StoredUser(
        final String username,
        final String email,
        final String role,
        final String provider,
        final Storage storage
    ) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.storage = storage;
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
    public String role() {
        return this.role;
    }

    @Override
    public Provider provider() {
        final Provider provider;
        if(this.provider.equals(Provider.Names.GITHUB)) {
            provider = new Github(this, this.storage);
        } else {
            provider = new Gitlab(this, this.storage);
        }
        return provider;
    }

    @Override
    public Projects projects() {
        return this.storage.projects().ownedBy(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.provider);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return this.username.equals(other.username())
            && this.provider.equals(other.provider().name());
    }
}
