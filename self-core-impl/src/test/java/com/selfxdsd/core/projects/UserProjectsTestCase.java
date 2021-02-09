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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link UserProjects}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UserProjectsTestCase {

    /**
     * UserProjects can return the projects assigned to a PM.
     */
    @Test
    public void returnsAssignedToProjectManager() {
        final List<Project> list = new ArrayList<>();
        list.add(this.projectAssignedTo(1));
        list.add(this.projectAssignedTo(2));
        list.add(this.projectAssignedTo(3));
        list.add(this.projectAssignedTo(2));
        final Projects projects = new UserProjects(
            Mockito.mock(User.class),
            list::stream,
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            projects.assignedTo(2),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * UserProjects can iterate over the projects.
     */
    @Test
    public void iteratorWorks() {
        final List<Project> list = new ArrayList<>();
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        final Projects projects = new UserProjects(
            Mockito.mock(User.class),
            list::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(projects, Matchers.iterableWithSize(3));
    }

    /**
     * UserProjects can iterate over a page of projects.
     */
    @Test
    public void iteratorPageWorks() {
        final List<Project> list = new ArrayList<>();
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        final Projects projects = new UserProjects(
            Mockito.mock(User.class),
            list::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(projects.page(new Paged.Page(1, 2)),
            Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(projects.page(new Paged.Page(2, 2)),
            Matchers.iterableWithSize(1));
    }

    /**
     * Method ownedBy() returns itself if the User matches.
     */
    @Test
    public void ownedByReturnsItself() {
        final Projects projects = new UserProjects(
            this.mockUser("mihai", "github"),
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.ownedBy(this.mockUser("mihai", "github")),
            Matchers.is(projects)
        );
    }

    /**
     * Cannot register a new Project in UserProjects.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerIsUnsupported() {
        final Projects projects = new UserProjects(
            this.mockUser("mihai", "github"),
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        projects.register(
            Mockito.mock(Repo.class),
            Mockito.mock(ProjectManager.class),
            "wtoken123"
        );
    }

    /**
     * Method ownedBy() throws an exception if the specified id
     * is the one of a different User.
     */
    @Test (expected = IllegalStateException.class)
    public void ownedByComplainsOnDifferendUser() {
        final Projects projects = new UserProjects(
            this.mockUser("mihai", "github"),
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        projects.ownedBy(this.mockUser("vlad", "github"));
    }

    /**
     * Should find a project by it's id.
     */
    @Test
    public void projectByIdFound() {
        final User user = this.mockUser("mihai", "github");

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.owner()).thenReturn(user);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("mihai/test", "github")
        ).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final Projects projects = new UserProjects(
            user,
            () -> List.of(
                mockProject("mihai/test", "github"),
                mockProject("mihai/test2", "github")
            ).stream(),
            storage
        );
        final Project found = projects.getProjectById("mihai/test", "github");
        MatcherAssert.assertThat(
            found,
            Matchers.is(project)
        );
    }

    /**
     * A Project is found but it belongs to another User so the
     * method should return null.
     */
    @Test
    public void projectByIdFoundButHasOtherOwner() {
        final User mihai = this.mockUser("mihai", "github");
        final User vlad = this.mockUser("vlad", "github");

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.owner()).thenReturn(vlad);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("vlad/test", "github")
        ).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final Projects projects = new UserProjects(
            mihai,
            () -> List.of(
                mockProject("mihai/test", "github"),
                mockProject("mihai/test2", "github")
            ).stream(),
            storage
        );
        final Project found = projects.getProjectById("vlad/test", "github");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }


    /**
     * Should return null is project is not found by id.
     */
    @Test
    public void projectByIdNotFound() {
        final User user = this.mockUser("mihai", "github");

        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("mihai/test", "github")
        ).thenReturn(null);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final Projects projects = new UserProjects(
            user,
            () -> List.of(
                mockProject("mihai/test", "github"),
                mockProject("mihai/test2", "github")
            ).stream(),
            storage
        );
        final Project found = projects.getProjectById("mihai/test", "github");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * We can remove a Project if it's owned by the same User.
     */
    @Test
    public void removesProjectIfOwnedBySameUser() {
        final User mihai = this.mockUser("mihai", "github");
        final Projects projects = new UserProjects(
            mihai,
            () -> List.of(
                mockProject("mihai/test", "github"),
                mockProject("mihai/test2", "github"),
                mockProject("mihai/test3", "github"),
                mockProject("mihai/test4", "github")
            ).stream(),
            Mockito.mock(Storage.class)
        );
        final Project toRemove = mockProject("mihai/test", "github");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(toRemove.repo()).thenReturn(repo);
        Mockito.when(toRemove.owner()).thenReturn(mihai);

        projects.remove(toRemove);

        Mockito.verify(toRemove, Mockito.times(1)).deactivate(repo);
    }

    /**
     * We can remove a Project if it's owned by the same User.
     */
    @Test(expected = IllegalStateException.class)
    public void doesNotRemoveProjectIfNotOwnedBySameUser() {
        final Projects projects = new UserProjects(
            this.mockUser("mihai", "github"),
            () -> List.of(
                mockProject("mihai/test", "github"),
                mockProject("mihai/test2", "github"),
                mockProject("mihai/test3", "github"),
                mockProject("mihai/test4", "github")
            ).stream(),
            Mockito.mock(Storage.class)
        );
        final Project toRemove = mockProject("mihai/test", "github");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(toRemove.repo()).thenReturn(repo);
        final User vlad = this.mockUser("vlad", "github");
        Mockito.when(toRemove.owner()).thenReturn(vlad);

        projects.remove(toRemove);

        Mockito.verify(toRemove, Mockito.times(0)).deactivate(repo);
    }

    /**
     * Mock a User.
     * @param username Username.
     * @param providerName Name of the provider.
     * @return User.
     */
    private User mockUser(final String username, final String providerName) {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn(username);

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(providerName);

        Mockito.when(user.provider()).thenReturn(provider);
        return user;
    }

    /**
     * Mock a Project owned by a User.
     *
     * @param username User's name.
     * @param providerName Provider's name.
     * @return Project.
     */
    private Project projectOwnedBy(
        final String username, final String providerName
    ) {
        final Project project = Mockito.mock(Project.class);
        final User owner = this.mockUser(username, providerName);
        Mockito.when(project.owner()).thenReturn(owner);

        return project;
    }


    /**
     * Mock a Project assigned to a PM.
     * @param projectManagerId Manager's ID.
     * @return Project.
     */
    private Project projectAssignedTo(final int projectManagerId) {
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.id()).thenReturn(projectManagerId);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.projectManager()).thenReturn(manager);

        return project;
    }


    /**
     * Mocks a bare minimum project.
     *
     * @param fullName Full name.
     * @param provider Provider.
     * @return Mocked Project
     */
    private Project mockProject(final String fullName, final String provider) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(fullName);
        Mockito.when(project.provider()).thenReturn(provider);
        return project;
    }
}
