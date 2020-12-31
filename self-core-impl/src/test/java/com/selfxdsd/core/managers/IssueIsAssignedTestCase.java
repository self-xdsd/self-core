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
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.pm.Step;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link IssueIsAssigned}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.30
 */
public final class IssueIsAssignedTestCase {

    /**
     * The issue is assigned, so onTrue should be called as next step.
     */
    @Test
    public void issueIsAssigned() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.assignee()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(Mockito.mock(Project.class));
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new IssueIsAssigned(
            onTrue,
            onFalse -> {
                throw new IllegalStateException("Should not be called");
            }
        );

        step.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * The issue is NOT assigned, so onFalse should be called as next step.
     */
    @Test
    public void issueIsNotAssigned() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.assignee()).thenReturn(null);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(Mockito.mock(Project.class));
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new IssueIsAssigned(
            onTrue -> {
                throw new IllegalStateException("Should not be called");
            },
            onFalse
        );

        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }
}
