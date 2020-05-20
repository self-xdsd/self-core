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

import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.Repo;

/**
 * A Project stored in Self.<br><br>
 *
 * This class is in the API because it is implementation-agnostic!
 * It only works with API interfaces and nothing else.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #31:30min Implement the deactivate method which should remove the
 *  Project form the DB (it means Self will stop managing it). Return the
 *  corresponding Repo when done. Don't forget the tests.
 */
public final class StoredProject implements Project {

    /**
     * Project ID.
     */
    private final int projectId;

    /**
     * Repo of this Project.
     */
    private final Repo repo;

    /**
     * Manager in charge of this Project.
     */
    private final ProjectManager projectManager;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param projectId This project's ID.
     * @param repo Repo of this project.
     * @param projectManager Manager in charge.
     * @param storage Storage of Self.
     * @checkstyle ParameterNumber (10 lines)
     */
    public StoredProject(
        final int projectId, final Repo repo,
        final ProjectManager projectManager, final Storage storage
    ) {
        this.projectId = projectId;
        this.repo = repo;
        this.projectManager = projectManager;
        this.storage = storage;
    }

    @Override
    public int projectId() {
        return this.projectId;
    }

    @Override
    public ProjectManager projectManager() {
        return this.projectManager;
    }

    @Override
    public Repo repo() {
        return this.repo;
    }

    @Override
    public Contracts contracts() {
        return this.storage.contracts().ofProject(this.projectId);
    }

    @Override
    public Repo deactivate() {
        return null;
    }
}
