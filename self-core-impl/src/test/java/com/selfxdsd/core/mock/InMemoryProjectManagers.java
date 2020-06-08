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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.managers.StoredProjectManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * In memory PMs for testing purposes.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryProjectManagers implements ProjectManagers {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * PMs's "table".
     */
    private final Map<Integer, ProjectManager> pms = new HashMap<>();

    /**
     * ProjectManagers' id counter.
     */
    private int idCounter;

    /**
     * Constructor.
     *
     * @param storage Parent storage.
     */
    public InMemoryProjectManagers(final Storage storage) {
        this.storage = storage;
        register("github", "123token");
        register("gitlab", "123token");
    }

    @Override
    public Iterator<ProjectManager> iterator() {
        return this.pms.values().iterator();
    }

    @Override
    public ProjectManager getById(final int id) {
        return this.pms.get(id);
    }

    @Override
    public ProjectManager pick(final String provider) {
        return pms.values().stream()
            .filter(pm -> pm.provider().equals(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public ProjectManager register(final String provider,
                                   final String accessToken) {
        final int id = ++idCounter;
        final StoredProjectManager projectManager = new StoredProjectManager(
            id, "", provider, accessToken, storage);
        pms.put(id, projectManager);
        return projectManager;
    }
}
