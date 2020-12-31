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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link InvitePm}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
public final class InvitePmTestCase {

    /**
     * InvitePm complains if the provider is unknown.
     */
    @Test(expected = IllegalStateException.class)
    public void complainsOnUnknownProvider() {
        final Step invitePm = new InvitePm(Mockito.mock(Step.class));

        final Repo repo = Mockito.mock(Repo.class);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repo()).thenReturn(repo);
        Mockito.when(project.projectManager()).thenReturn(manager);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.project().provider()).thenReturn("unknown");
        invitePm.perform(event);
    }

    /**
     * InvitePm invites the PM to be a collaborator on Gitlab and
     * calls the next step in the chain.
     */
    @Test
    public void invitesGitlabPm() {
        final Step next = Mockito.mock(Step.class);
        final Step invitePm = new InvitePm(next);

        final Collaborators collaborators = Mockito.mock(Collaborators.class);
        Mockito.when(
            collaborators.invite(Mockito.anyString())
        ).thenReturn(true);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.collaborators()).thenReturn(collaborators);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.userId()).thenReturn("123");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repo()).thenReturn(repo);
        Mockito.when(project.projectManager()).thenReturn(manager);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.project().provider())
            .thenReturn(Provider.Names.GITLAB);

        invitePm.perform(event);

        Mockito.verify(
            collaborators,
            Mockito.times(1)
        ).invite("123");
        Mockito.verify(
            next,
            Mockito.times(1)
        ).perform(event);
    }

    /**
     * InvitePm invites the PM to be a collaborator on Github and
     * calls the next step in the chain.
     */
    @Test
    public void invitesGithubPm() {
        final Step next = Mockito.mock(Step.class);
        final Step invitePm = new InvitePm(next);

        final Collaborators collaborators = Mockito.mock(Collaborators.class);
        Mockito.when(
            collaborators.invite(Mockito.anyString())
        ).thenReturn(true);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.collaborators()).thenReturn(collaborators);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.username()).thenReturn("zoeself");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repo()).thenReturn(repo);
        Mockito.when(project.projectManager()).thenReturn(manager);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.project().provider())
            .thenReturn(Provider.Names.GITHUB);

        invitePm.perform(event);

        Mockito.verify(
            collaborators,
            Mockito.times(1)
        ).invite("zoeself");
        Mockito.verify(
            next,
            Mockito.times(1)
        ).perform(event);
    }

}
