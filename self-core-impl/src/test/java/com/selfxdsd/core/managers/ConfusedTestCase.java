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
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Confused}.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class ConfusedTestCase {

    /**
     * Confused.start(...) should return a SendReply step in case
     * of a "confused" event.
     */
    @Test
    public void returnsStepForConfused() {
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(event.type()).thenReturn(Event.Type.CONFUSED);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(event.project()).thenReturn(project);

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(comment.body()).thenReturn("gibberish");

        final Step step = (new Confused()).start(event);

        step.perform(event);

        MatcherAssert.assertThat(
            step,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );

        Mockito.verify(comments).post("> " + "gibberish" + "\n\n"
            + "@mihai didn't understand that, sorry.");
    }

    /**
     * Confused.start(...), since is a terminal step, should throw ISE if the
     * event type is not "confused".
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIfEventTypeIsNotConfused() {
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn("notConfused");
        new Confused().start(event);
    }

}
