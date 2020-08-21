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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link UnassignTask}.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class RemoveTaskTestCase {

    /**
     * There is actually no task registered for the
     * event's issue.
     */
    @Test
    public void taskIsMissing() {
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(null);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        final Step next = Mockito.mock(Step.class);
        final Step removeTask = new RemoveTask(next);
        removeTask.perform(event);
        Mockito.verify(next, Mockito.times(1)).perform(event);
    }

    /**
     * There is a Task registered for the event's Issue, but it is not
     * assigned to anyone.
     */
    @Test
    public void taskIsRemoved() {
        final Storage storage = new InMemory();
        final Project project = storage.projects().register(
            this.mockRepo("john/test", "github"),
            storage.projectManagers().pick("github"),
            "token"
        );
        final Issue issue = this.mockIssue("1",
            "john/test", "github", "ARCH");
        project.tasks().register(issue);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Step remove = new RemoveTask(Mockito.mock(Step.class));

        MatcherAssert.assertThat(storage.tasks().getById(
            "1", "john/test", "github"
        ), Matchers.notNullValue());
        remove.perform(event);
        MatcherAssert.assertThat(storage.tasks().getById(
            "1", "john/test", "github"
        ), Matchers.nullValue());
    }



    /**
     * Mock a Repo for test.
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(final String fullName, final String provider) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }

    /**
     * Mock an Issue for test.
     * @param issueId ID.
     * @param repoFullName Repo fullname.
     * @param provider Provider.
     * @param role Role.
     * @return Issue.
     */
    private Issue mockIssue(
        final String issueId, final String repoFullName,
        final String provider, final String role) {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn(issueId);
        Mockito.when(issue.repoFullName()).thenReturn(repoFullName);
        Mockito.when(issue.provider()).thenReturn(provider);
        Mockito.when(issue.role()).thenReturn(role);
        Mockito.when(issue.comments()).thenReturn(Mockito.mock(Comments.class));
        return issue;
    }

}
