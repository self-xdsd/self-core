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
import com.selfxdsd.api.Label;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.api.Labels;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link IssueHasLabel}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.30
 */
public final class IssueHasLabelTestCase {

    /**
     * Issue has the label. OnTrue should be called.
     */
    @Test
    public void issueHasTheLabel() {
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("the-label");
        final List<Label> list = Arrays.asList(label);
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(list.iterator());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.labels()).thenReturn(labels);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(Mockito.mock(Project.class));

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new IssueHasLabel(
            "the-label",
            onTrue,
            onFalse -> {
                throw new IllegalStateException("Should not be called.");
            }
        );

        step.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * Issue does NOT have the label. OnFalse should be called.
     */
    @Test
    public void issueDoesNotHaveTheLabel() {
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("the-label");
        final List<Label> list = Arrays.asList(label);
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(list.iterator());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.labels()).thenReturn(labels);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(Mockito.mock(Project.class));

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new IssueHasLabel(
            "other-label",
            onTrue -> {
                throw new IllegalStateException("Should not be called.");
            },
            onFalse
        );

        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }
}
