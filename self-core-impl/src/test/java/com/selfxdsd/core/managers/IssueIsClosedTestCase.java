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

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.pm.Step;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link IssueIsClosed}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.26
 */
public final class IssueIsClosedTestCase {

    /**
     * IssueIsClosed can perform on a closed Issue.
     */
    @Test
    public void performsOnClosedIssue() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isClosed()).thenReturn(Boolean.TRUE);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onTrue = Mockito.mock(Step.class);
        final IssueIsClosed issueIsClosed = new IssueIsClosed(
            onTrue,
            onFalse -> {
                throw new IllegalStateException(
                    "OnFalse should not be called, since the Issue is closed."
                );
            }
        );
        issueIsClosed.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * IssueIsClosed can perform on an open Issue.
     */
    @Test
    public void performsOnOpenIssue() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isClosed()).thenReturn(Boolean.FALSE);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onFalse = Mockito.mock(Step.class);
        final IssueIsClosed issueIsClosed = new IssueIsClosed(
            onTrue -> {
                throw new IllegalStateException(
                    "OnTrue should not be called, since the Issue is open."
                );
            },
            onFalse
        );
        issueIsClosed.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }
}
