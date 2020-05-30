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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

/**
 * A Contributor stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredContributor implements Contributor {

    /**
     * Username.
     */
    private final String username;

    /**
     * Provider.
     */
    private final String provider;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param username Username.
     * @param provider Provider.
     * @param storage Storage.
     */
    public StoredContributor(
        final String username,
        final String provider,
        final Storage storage
    ) {
        this.username = username;
        this.provider = provider;
        this.storage = storage;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String provider() {
        return this.provider;
    }

    @Override
    public Contracts contracts() {
        return this.storage.contracts().ofContributor(this);
    }

    @Override
    public Tasks tasks() {
        return this.storage.tasks().ofContributor(this.username, this.provider);
    }
}
