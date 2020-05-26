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
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.projects.PmProjects;
import com.selfxdsd.core.projects.UserProjects;

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
        final List<Project> assigned = new ArrayList<>();
        final Collection<Project> all = this.projects.values();
        for(final Project project : all) {
            if(project.projectManager().id() == projectManagerId) {
                assigned.add(project);
            }
        }
        return new PmProjects(projectManagerId, assigned);
    }

    @Override
    public Projects ownedBy(final User user) {
        final List<Project> owned = new ArrayList<>();
        for(final Project project : this.projects.values()) {
            final User owner = project.owner();
            if(owner.username().equals(owner.username())
                && owner.provider().name().equals(user.provider().name())) {
                owned.add(project);
            }
        }
        return new UserProjects(user, owned);
    }

    @Override
    public Project getProjectById(final int projectId) {
        return projects
            .values()
            .stream()
            .filter(p -> p.projectId() == projectId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Iterator<Project> iterator() {
        return this.projects.values().iterator();
    }
}
