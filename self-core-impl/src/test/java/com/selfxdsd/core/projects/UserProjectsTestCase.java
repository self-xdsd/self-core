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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

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
            Mockito.mock(User.class), list
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
            Mockito.mock(User.class), list
        );
        MatcherAssert.assertThat(projects, Matchers.iterableWithSize(3));
    }

    /**
     * Method ownedBy() returns itself if the User matches.
     */
    @Test
    public void ownedByReturnsItself() {
        final Projects projects = new UserProjects(
            this.mockUser("mihai", "github"),
            new ArrayList<>()
        );
        MatcherAssert.assertThat(
            projects.ownedBy(this.mockUser("mihai", "github")),
            Matchers.is(projects)
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
            new ArrayList<>()
        );
        projects.ownedBy(this.mockUser("vlad", "github"));
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
     * Should find a project by it's id.
     */
    @Test
    public void projectByIdFound() {
        final Projects projects = new PmProjects(1, List.of(
            mockProject(1), mockProject(2)
        ));
        MatcherAssert.assertThat(1,
            Matchers.equalTo(projects.getProjectById(1)
                .projectId()));
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
     * @param id Provided id
     * @return Mocked Project
     */
    private Project mockProject(final int id) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.projectId()).thenReturn(id);
        return project;
    }
}
