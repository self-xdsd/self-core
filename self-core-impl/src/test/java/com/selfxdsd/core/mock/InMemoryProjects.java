/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
import com.selfxdsd.api.exceptions.ProjectsException;
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;
import com.selfxdsd.core.projects.PmProjects;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.projects.UserProjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * In-memory Projects for testing purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryProjects extends BasePaged implements Projects{

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Projects' "table".
     */
    private final Map<ProjectKey, Project> projects;

    /**
     * Constructor.
     * @param storage Parent storage.
     */
    public InMemoryProjects(final Storage storage) {
        this(storage, new HashMap<>(), new Page(1, 10));
    }

    /**
     * Constructor.
     * @param storage Parent storage.
     * @param projects Projects "table".
     * @param page Current Page.
     */
    private InMemoryProjects(
        final Storage storage,
        final Map<ProjectKey, Project> projects,
        final Page page) {
        super(page, projects::size);
        this.storage = storage;
        this.projects = projects;
    }

    @Override
    public Project register(
        final Repo repo,
        final ProjectManager manager,
        final String webHookToken
    ){
        if(manager == null
            || this.storage.projectManagers().getById(manager.id()) == null) {
            throw new ProjectsException.Single.Add(
                repo.fullName(),
                repo.provider(),
                "PM is missing or not registered!"
            );
        } else {
            final ProjectKey key = new ProjectKey(
                repo.fullName(), repo.provider()
            );
            if(this.projects.get(key) != null) {
                throw new ProjectsException.Single.Add(
                    repo.fullName(),
                    repo.provider(),
                    "already exists."
                );
            }
            final Project project = new StoredProject(
                repo.owner(), repo.fullName(), webHookToken,
                manager, this.storage
            );
            this.projects.put(key, project);
            return project;
        }
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        final Page page = super.current();
        final Supplier<Stream<Project>> assigned = () -> this.projects
            .values()
            .stream()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> p.projectManager().id() == projectManagerId);
        return new PmProjects(projectManagerId, assigned);
    }

    @Override
    public Projects ownedBy(final User user) {
        final Page page = super.current();
        final Supplier<Stream<Project>> owned = () -> this.projects
            .values()
            .stream()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> {
                final User owner = p.owner();
                return owner.username().equals(user.username())
                    && owner.provider().name()
                    .equals(user.provider().name());
            });
        return new UserProjects(user, owned);
    }

    @Override
    public Project getProjectById(
        final String repoFullName, final String repoProvider
    ) {
        final Page page = super.current();
        return this.projects
            .entrySet()
            .stream()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(e -> e.getKey()
                .equals(new ProjectKey(repoFullName, repoProvider)))
            .map(Map.Entry::getValue)
            .findFirst().orElse(null);
    }

    @Override
    public Projects page(final Paged.Page page) {
        return new InMemoryProjects(this.storage, this.projects, page);
    }

    @Override
    public void remove(final Project project) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Project> iterator() {
        final Page page = super.current();
        return this.projects
            .entrySet()
            .stream()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .map(Map.Entry::getValue)
            .iterator();
    }

    /**
     * Project PK.
     */
    public static final class ProjectKey {
        /**
         * Repos's full name (e.g. amihaiemil/docker-java-api).
         */
        private final String repoFullName;

        /**
         * Repo's provider (github, gitlab etc).
         */
        private final String repoProvider;

        /**
         * Constructor.
         *
         * @param repoFullName Contributor's username.
         * @param repoProvider Repo's provider.
         * */
        ProjectKey(
            final String repoFullName,
            final String repoProvider
        ) {
            this.repoFullName = repoFullName;
            this.repoProvider = repoProvider;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final ProjectKey key = (ProjectKey) object;
            //@checkstyle LineLength (5 lines)
            return this.repoFullName.equals(key.repoFullName)
                && this.repoProvider.equals(key.repoProvider);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.repoFullName,
                this.repoProvider
            );
        }
    }

}
