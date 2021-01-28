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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TaskIsRegistered}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class TaskIsRegisteredTestCase {

    /**
     * TaskIsRegistered determines that the task is registered.
     */
    @Test
    public void taskIsRegistered() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("123", "john/test", "github", Boolean.FALSE)
        ).thenReturn(Mockito.mock(Task.class));
        Mockito.when(project.tasks()).thenReturn(tasks);

        final TaskIsRegistered taskIsRegistered = new TaskIsRegistered(
            onTrue -> MatcherAssert.assertThat(onTrue, Matchers.is(event)),
            onFalse -> {
                throw new IllegalStateException("Should not be called.");
            }
        );
        taskIsRegistered.perform(event);
    }

    /**
     * TaskIsRegistered determines that the task is NOT registered.
     */
    @Test
    public void taskIsNotRegistered() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("123", "john/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);

        final TaskIsRegistered taskIsRegistered = new TaskIsRegistered(
            onTrue -> {
                throw new IllegalStateException("Should not be called.");
            },
            onFalse -> MatcherAssert.assertThat(onFalse, Matchers.is(event))
        );
        taskIsRegistered.perform(event);
    }


}
