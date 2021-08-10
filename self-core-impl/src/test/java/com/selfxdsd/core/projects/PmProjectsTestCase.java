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
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Unit tests for {@link PmProjects}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class PmProjectsTestCase {

    /**
     * Registering a new Repo within a PM's Projects is
     * not allowed.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerIsUnsupported() {
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        projects.register(
            Mockito.mock(Repo.class),
            Mockito.mock(ProjectManager.class),
            "wh123token"
        );
    }

    /**
     * Method assignedTo() returns itself if the id matches.
     */
    @Test
    public void assignedToReturnsItself() {
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.assignedTo(1), Matchers.is(projects)
        );
    }

    /**
     * Method assignedTo() throws an exception if the specified id
     * is the one of a different PM.
     */
    @Test(expected = IllegalStateException.class)
    public void assignedToComplainsOnDifferendId() {
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        projects.assignedTo(2);
    }

    /**
     * PmProjects can be iterated.
     */
    @Test
    public void iterateWorks() {
        final List<Project> list = new ArrayList<>();
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        list.add(Mockito.mock(Project.class));
        final Projects projects = new PmProjects(
            1, list::stream, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(projects, Matchers.iterableWithSize(3));
    }

    /**
     * Method ownedBy(User) returns the User's projects.
     */
    @Test
    public void ownedByWorks() {
        final List<Project> list = new ArrayList<>();
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("vlad", "github"));
        list.add(this.projectOwnedBy("mihai", "gitlab"));
        list.add(this.projectOwnedBy("mihai", "github"));
        final Projects projects = new PmProjects(
            1, list::stream, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.ownedBy(
                this.mockUser("mihai", "github")
            ),
            Matchers.iterableWithSize(3)
        );
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
        final Projects projects = new PmProjects(
            1,
            () -> List.of(
                mockProject("john/test", "github", "wt1"),
                mockProject("john/test2", "github", "wt2")
            ).stream(),
            Mockito.mock(Storage.class)
        );
        final Project found = projects.getProjectById("john/test", "github");
        MatcherAssert.assertThat(
            found.repoFullName(),
            Matchers.equalTo("john/test")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * Should return null is project is not found by id.
     */
    @Test
    public void projectByIdNotFound() {
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.getProjectById("mihai/missing", "github"),
            Matchers.nullValue()
        );
    }

    /**
     * Should a iterate over an existing page.
     */
    @Test
    public void iteratePageWorks(){
        final Projects projects = new PmProjects(1, () -> IntStream
            .rangeClosed(1, 14)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        );
        //initial page has all available records
        MatcherAssert.assertThat(projects, Matchers.iterableWithSize(14));
        MatcherAssert.assertThat(projects.page(new Paged.Page(2, 5)),
            Matchers.iterableWithSize(5));
        MatcherAssert.assertThat(projects.page(new Paged.Page(3, 5)),
            Matchers.iterableWithSize(4));
        MatcherAssert.assertThat(projects.page(new Paged.Page(1, 15)),
            Matchers.iterableWithSize(14));
        MatcherAssert.assertThat(projects.page(new Paged.Page(14, 1)),
            Matchers.iterableWithSize(1));
    }

    /**
     * Should a iterate over an empty page.
     */
    @Test
    public void iterateEmptyPageWorks(){
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(projects, Matchers.emptyIterable());
    }

    /**
     * Throws when page is upper out of bounds.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenPageIsUpperOutOfBound(){
        new PmProjects(1, () -> IntStream
            .rangeClosed(1, 10)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(5, 10));
    }

    /**
     * Throws when page is under out of bounds.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenPageIsUnderOutOfBound(){
        new PmProjects(1, () -> IntStream
            .rangeClosed(1, 10)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(0, 10));
    }

    /**
     * Should find a project by it's id in a random page.
     */
    @Test
    public void projectByIdFoundInPage(){
        final Project found = new PmProjects(1, () -> IntStream
            .rangeClosed(1, 14)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(2, 5)).getProjectById(
            "repo-7", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            found.repoFullName(),
            Matchers.equalTo("repo-7")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * Should return null if project is not found by id in the specified page,
     * even though project exists in overall.
     */
    @Test
    public void existingProjectNotFoundByIdInPage(){
        final Project found = new PmProjects(
            1, () -> IntStream
            .rangeClosed(1, 14)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(2, 5)).getProjectById(
            "repo-1", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(found, Matchers.nullValue());
    }


    /**
     * Method ownedBy(User) returns the User's projects at the specified page.
     */
    @Test
    public void ownedByInPageWorks() {
        final List<Project> list = new ArrayList<>();
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("vlad", "github"));
        list.add(this.projectOwnedBy("mihai", "gitlab"));
        list.add(this.projectOwnedBy("mihai", "github"));
        final Projects projects = new PmProjects(
            1, list::stream, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.page(new Paged.Page(1, 3))
                .ownedBy(this.mockUser("mihai", "github")),
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            projects.page(new Paged.Page(2, 3))
                .ownedBy(this.mockUser("mihai", "github")),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * We can remove a Project if it's owned by the same PM.
     */
    @Test
    public void removesProjectIfOwnedBySamePm() {
        final List<Project> list = new ArrayList<>();
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("vlad", "github"));
        list.add(this.projectOwnedBy("mihai", "gitlab"));
        list.add(this.projectOwnedBy("mihai", "github"));
        final Projects projects = new PmProjects(
            1, list::stream, Mockito.mock(Storage.class)
        );

        final Project toRemove = this.projectOwnedBy("mihai", "github");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(toRemove.repo()).thenReturn(repo);
        final ProjectManager owner = Mockito.mock(ProjectManager.class);
        Mockito.when(owner.id()).thenReturn(1);
        Mockito.when(toRemove.projectManager()).thenReturn(owner);
        projects.remove(toRemove);

        Mockito.verify(toRemove, Mockito.times(1)).deactivate(repo);
    }

    /**
     * We cannot remove a Project owned by another PM.
     */
    @Test(expected = IllegalStateException.class)
    public void doesNotRemoveProjectIfNotOwnedBySamePm() {
        final List<Project> list = new ArrayList<>();
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("mihai", "github"));
        list.add(this.projectOwnedBy("vlad", "github"));
        list.add(this.projectOwnedBy("mihai", "gitlab"));
        list.add(this.projectOwnedBy("mihai", "github"));
        final Projects projects = new PmProjects(
            1, list::stream, Mockito.mock(Storage.class)
        );

        final Project toRemove = this.projectOwnedBy("mihai", "github");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(toRemove.repo()).thenReturn(repo);
        final ProjectManager owner = Mockito.mock(ProjectManager.class);
        Mockito.when(owner.id()).thenReturn(2);
        Mockito.when(toRemove.projectManager()).thenReturn(owner);
        projects.remove(toRemove);

        Mockito.verify(toRemove, Mockito.times(0)).deactivate(repo);
    }

    /**
     * Should find a project by its Webhook Token.
     */
    @Test
    public void projectByWebhookTokenFound() {
        final Projects projects = new PmProjects(
            1,
            () -> List.of(
                mockProject("john/test", "github", "wt-1"),
                mockProject("john/test2", "github", "wt-2")
            ).stream(),
            Mockito.mock(Storage.class)
        );
        final Project found = projects.getByWebHookToken("wt-1");
        MatcherAssert.assertThat(
            found.repoFullName(),
            Matchers.equalTo("john/test")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * Should return null if project is not found by webhook token.
     */
    @Test
    public void projectByWebHookTokenNotFound() {
        final Projects projects = new PmProjects(
            1, Stream::empty, Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            projects.getByWebHookToken("wt-1"),
            Matchers.nullValue()
        );
    }

    /**
     * Should find a project by its webhook token in a random page.
     */
    @Test
    public void projectByWebHookTokenFoundInPage(){
        final Project found = new PmProjects(1, () -> IntStream
            .rangeClosed(1, 14)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(2, 5)).getByWebHookToken("wt-7");
        MatcherAssert.assertThat(
            found.repoFullName(),
            Matchers.equalTo("repo-7")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * Should return null if project is not found by webhook token in the
     * specified page, even though project exists in overall.
     */
    @Test
    public void existingProjectNotFoundByWebHookTokenInPage(){
        final Project found = new PmProjects(
            1, () -> IntStream
            .rangeClosed(1, 14)
            .mapToObj(i -> mockProject("repo-" + i,
                Provider.Names.GITHUB, "wt-" + i)),
            Mockito.mock(Storage.class)
        ).page(new Paged.Page(2, 5)).getByWebHookToken("wt-1");
        MatcherAssert.assertThat(found, Matchers.nullValue());
    }

    /**
     * Mock a User.
     *
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
     * Mocks a bare minimum project.
     *
     * @param repoFullName Repo full name.
     * @param repoProvider Provider.
     * @param webHookToken WebHook Token.
     * @return Mocked Project
     */
    private Project mockProject(
        final String repoFullName,
        final String repoProvider,
        final String webHookToken
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn(repoFullName);
        Mockito.when(project.provider()).thenReturn(repoProvider);
        Mockito.when(project.webHookToken()).thenReturn(webHookToken);
        return project;
    }
}
