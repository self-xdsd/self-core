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
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Commands}.
 * @author criske
 * @version $Id$
 * @since 0.0.72
 */
public final class CommandsTestCase {

    /**
     * Commands.start(...) should return a SendReply step in case
     * of a "commands" event.
     * @checkstyle ExecutableStatementCount (60 lines).
     */
    @Test
    public void returnsStepForCommands() {
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn(Event.Type.COMMANDS);

        final Project project = Mockito.mock(Project.class);
        final Comment comment = Mockito.mock(Comment.class);
        final Comments comments = Mockito.mock(Comments.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(comment.body()).thenReturn("");
        Mockito.when(comment.author()).thenReturn("john");
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Conversation commands = new Commands(
            next -> {
                throw new IllegalStateException(
                    "Conversation should end with 'commands', "
                    + "the next one in chain should not be called."
                );
            }
        );

        final Step step = commands.start(event);
        step.perform(event);
        MatcherAssert.assertThat(
            step,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );
        final ArgumentCaptor<String> reply = ArgumentCaptor
            .forClass(String.class);
        Mockito.verify(comments, Mockito.times(1))
            .post(reply.capture());
        MatcherAssert.assertThat(
            reply.getValue(),
            Matchers
                .startsWith(
                    "> \n\nHi @john! Here are the commands which I understand:"
                )
        );
    }

    /**
     * Commands.start(...) should call the next conversation if the
     * given Event is not 'commands'.
     */
    @Test
    public void goesFurtherIfNotCommands() {
        final Step resolved = Mockito.mock(Step.class);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn("notCommands");
        final Conversation commands = new Commands(
            next -> {
                MatcherAssert.assertThat(event, Matchers.is(next));
                return resolved;
            }
        );
        MatcherAssert.assertThat(
            commands.start(event),
            Matchers.is(resolved)
        );
    }
}