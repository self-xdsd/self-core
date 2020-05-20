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
import com.selfxdsd.api.storage.StoredProject;

import java.util.*;

/**
 * In-memory Projects for testing purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryProjects implements Projects {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * PMs's "table".
     */
    private final Map<Integer, Project> projects;

    /**
     * Projects' id counter.
     */
    private int idCounter;

    /**
     * Constructor.
     * @param storage Parent storage.
     */
    public InMemoryProjects(final Storage storage) {
        this.storage = storage;
        this.projects = new HashMap<>();
    }

    @Override
    public Project register(final Repo repo, final ProjectManager manager){
        if(manager == null
            || this.storage.projectManagers().getById(manager.id()) == null) {
            throw new IllegalArgumentException(
                "PM is missing or not registered!"
            );
        } else {
            final int projectId = this.idCounter++;
            final Project project = new StoredProject(
                projectId, repo, manager, this.storage
            );
            this.projects.put(projectId, project);
            return project;
        }
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        return new Projects() {
            /**
             * The manager's ID.
             */
            private final int pmId = projectManagerId;

            @Override
            public Project register(
                final Repo repo,
                final ProjectManager manager
            ) {
                throw new UnsupportedOperationException(
                    "A manager's assigned Projects are immutable, "
                  + "can't register here."
                );
            }

            @Override
            public Projects assignedTo(final int projectManagerId) {
                throw new UnsupportedOperationException(
                    "You're already seeing the projects assigned "
                  + "to the manager with id " + this.pmId
                );
            }

            @Override
            public Iterator<Project> iterator() {
                final List<Project> assigned = new ArrayList<>();
                final Collection<Project> all = InMemoryProjects.this.projects
                    .values();
                for(final Project project : all) {
                    if(project.projectManager().id() == this.pmId) {
                        assigned.add(project);
                    }
                }
                return assigned.iterator();
            }
        };
    }

    @Override
    public Iterator<Project> iterator() {
        return this.projects.values().iterator();
    }
}
