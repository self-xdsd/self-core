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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link Deregister}.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class DeregisterTestCase {

    /**
     * Author can deregister task with roles of PO or ARCH.
     */
    @Test
    public void authorCanDeregisterTask(){
        final Storage storage = new InMemory();
        storage.contributors()
            .register("john-arch", "github");
        final Contributor markDev = storage
            .contributors()
            .register("mark", "github");
        final Project project = storage.projects().register(
            this.mockRepo("john/test", "github"),
            storage.projectManagers().pick("github"),
            "token");
        storage.contracts()
            .addContract("john/test", "john-arch", "github",
                BigDecimal.TEN, "ARCH");
        storage.contracts()
            .addContract("john/test", "mark", "github",
                BigDecimal.TEN, "DEV");
        final Issue issue = this.mockIssue("1", "john/test", "github", "DEV");
        storage.tasks().register(issue).assign(markDev);

        final Event event = Mockito.mock(Event.class);
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("john-arch");
        Mockito.when(comment.body()).thenReturn("Deregister please!");
        Mockito.when(event.type()).thenReturn(Event.Type.DEREGISTER);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Step step = new Deregister(e -> Mockito.mock(Step.class))
            .start(event);

        MatcherAssert.assertThat(
            step,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(AuthorHasRoles.class)
            )
        );
        MatcherAssert.assertThat(markDev.tasks(), Matchers.iterableWithSize(1));

        step.perform(event);

        MatcherAssert.assertThat(
            "Mark should not a have the task assigned to him anymore",
            markDev.tasks(), Matchers.emptyIterable());
        MatcherAssert.assertThat(
            "Task should be removed from storage",
            storage.tasks()
                .getById("1", "john/test", "github", Boolean.FALSE),
            Matchers.nullValue());
        Mockito.verify(issue.comments(), Mockito.times(1))
            .post("> Deregister please!\n\n"
                + "@john-arch ok, I've removed this task from scope. "
                + "I'm not managing it anymore."
            );
    }

    /**
     * Author can't deregister task when doesn't have a role of PO or ARCH.
     */
    @Test
    public void authorCanNotDeregisterTask(){
        final Storage storage = new InMemory();
        storage.contributors()
            .register("john-dev", "github");
        final Project project = storage.projects().register(
            this.mockRepo("john/test", "github"),
            storage.projectManagers().pick("github"),
            "token");
        storage.contracts()
            .addContract("john/test", "john-dev", "github",
                BigDecimal.TEN, "DEV");
        final Issue issue = this.mockIssue("1", "john/test", "github", "DEV");
        storage.tasks().register(issue);

        final Event event = Mockito.mock(Event.class);
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("john-dev");
        Mockito.when(comment.body()).thenReturn("Deregister please!");
        Mockito.when(event.type()).thenReturn(Event.Type.DEREGISTER);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Step step = new Deregister(e -> Mockito.mock(Step.class))
            .start(event);

        MatcherAssert.assertThat(
            step,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(AuthorHasRoles.class)
            )
        );

        step.perform(event);

        MatcherAssert.assertThat(
            "Task should not be removed from storage",
            storage.tasks()
                .getById("1", "john/test", "github", Boolean.FALSE),
            Matchers.notNullValue());
        Mockito.verify(issue.comments(), Mockito.times(1))
            .post("> Deregister please!\n\n"
                + "@john-dev you don't have the appropriate role "
                + "to remove this task.\n\n"
                + "Only users with PO or ARCH roles are allowed.");
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
        final Estimation estimation = Mockito.mock(Estimation.class);
        Mockito.when(estimation.minutes()).thenReturn(60);
        Mockito.when(issue.estimation()).thenReturn(estimation);
        return issue;
    }


}
