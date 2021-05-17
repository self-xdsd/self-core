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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link GitlabRepoInvitations}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.45
 */
public final class GitlabRepoInvitationsTestCase {

    /**
     * GitlabRepoInvitations can return the mock invitations if the repo is
     * not starred.
     */
    @Test
    public void returnsMockInvitations() {
        final Provider gitlab = Mockito.mock(Provider.class);

        final Projects projects = Mockito.mock(Projects.class);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.isStarred()).thenReturn(false);

        Mockito.when(gitlab.repo("mihai", "test")).thenReturn(repo);

        Mockito.when(projects.iterator()).thenReturn(
            List.of(project).iterator()
        );
        final User manager = Mockito.mock(User.class);
        Mockito.when(manager.projects()).thenReturn(projects);

        final Invitations invitations = new GitlabRepoInvitations(
            gitlab,
            manager
        );

        MatcherAssert.assertThat(
            invitations,
            Matchers.iterableWithSize(1)
        );
        Mockito.verify(manager, Mockito.times(1)).projects();
    }

    /**
     * GitlabRepoInvitations returns no invitations if the repo is starred.
     */
    @Test
    public void returnsNoInvitations() {
        final Provider gitlab = Mockito.mock(Provider.class);

        final Projects projects = Mockito.mock(Projects.class);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.isStarred()).thenReturn(true);

        Mockito.when(gitlab.repo("mihai", "test")).thenReturn(repo);

        Mockito.when(projects.iterator()).thenReturn(
            List.of(project).iterator()
        );
        final User manager = Mockito.mock(User.class);
        Mockito.when(manager.projects()).thenReturn(projects);

        final Invitations invitations = new GitlabRepoInvitations(
            gitlab,
            manager
        );

        MatcherAssert.assertThat(
            invitations,
            Matchers.iterableWithSize(0)
        );
        Mockito.verify(manager, Mockito.times(1)).projects();
    }
}
