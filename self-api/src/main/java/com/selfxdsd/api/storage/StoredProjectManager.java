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
package com.selfxdsd.api.storage;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.Projects;
import com.selfxdsd.api.Repo;

/**
 * A Project Manager stored in Self. Use this class when implementing
 * the Storage.<br><br>
 *
 * This class is in the API because it is implementation-agnostic!
 * It only works with API interfaces and nothing else.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredProjectManager implements ProjectManager {

    /**
     * This PMs id.
     */
    private final int id;

    /**
     * This PM's access token.
     */
    private final String accessToken;

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param id PM's id.
     * @param accessToken API Access token.
     * @param storage Self's storage.
     */
    public StoredProjectManager(
        final int id, final String accessToken,
        final Storage storage
    ) {
        this.id = id;
        this.accessToken = accessToken;
        this.storage = storage;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String accessToken() {
        return this.accessToken;
    }

    @Override
    public Project assign(final Repo repo) {
        return this.storage.projects().register(repo, this);
    }

    @Override
    public Projects projects() {
        return this.storage.projects().assignedTo(this.id);
    }
}
