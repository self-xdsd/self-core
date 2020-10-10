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

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Event;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Register}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class RegisterTestCase {

    /**
     * Register.start(...) returns the steps for the
     * "register" event.
     */
    @Test
    public void returnsRegisterSteps() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn("register");
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Conversation register = new Register(
            next -> {
                throw new IllegalStateException(
                    "Should not be called."
                );
            }
        );
        MatcherAssert.assertThat(
            register.start(event),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(IssueIsClosed.class)
            )
        );
    }

    /**
     * Register.start(...) should call the next conversation if the
     * given Event is not 'register'.
     */
    @Test
    public void goesFurtherIfNotRegister() {
        final Step resolved = Mockito.mock(Step.class);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn("notRegister");
        final Conversation register = new Register(
            next -> {
                MatcherAssert.assertThat(event, Matchers.is(next));
                return resolved;
            }
        );
        MatcherAssert.assertThat(
            register.start(event),
            Matchers.is(resolved)
        );
    }

}
