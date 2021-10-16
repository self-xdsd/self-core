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

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.pm.Step;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link EnableRepoIssues}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.95
 */
public final class EnableRepoIssuesTestCase {

    /**
     * EnableRepoIssues enables the Issues without problems and moves on to
     * the next Step.
     */
    @Test
    public void enablesIssuesOk() {
        final Project project = Mockito.mock(Project.class);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.enableIssues()).thenReturn(
            Mockito.mock(Issues.class)
        );
        Mockito.when(project.repo()).thenReturn(repo);

        final Step next = Mockito.mock(Step.class);
        final Step setup = new EnableRepoIssues(next);
        setup.perform(event);

        Mockito.verify(repo, Mockito.times(1))
            .enableIssues();
        Mockito.verify(next, Mockito.times(1))
            .perform(event);
    }

    /**
     * EnableRepoIssues moves on to the next Step even if Repo.enableIssues()
     * throws an IllegalStateException.
     */
    @Test
    public void enableIssuesThrowsIllegalStateException() {
        final Project project = Mockito.mock(Project.class);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.enableIssues()).thenThrow(
            new IllegalStateException("Enabling of Issues failed.")
        );
        Mockito.when(project.repo()).thenReturn(repo);

        final Step next = Mockito.mock(Step.class);
        final Step setup = new EnableRepoIssues(next);
        setup.perform(event);

        Mockito.verify(repo, Mockito.times(1))
            .enableIssues();
        Mockito.verify(next, Mockito.times(1))
            .perform(event);
    }
}
