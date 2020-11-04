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
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Understand}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class UnderstandTestCase {

    /**
     * Understand can start the conversation by categorizing the
     * event's comment as "hello" and sending the categorized event further,
     * to the next Conversation.
     */
    @Test
    public void startsHelloConversation() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.body()).thenReturn("hello there!");

        final Conversation next = event -> {
            MatcherAssert.assertThat(
                event.type(),
                Matchers.equalTo("hello")
            );
            MatcherAssert.assertThat(
                event.project(),
                Matchers.is(project)
            );
            MatcherAssert.assertThat(
                event.comment(),
                Matchers.is(comment)
            );
            return null;
        };
        final Conversation talk = new Understand(next);

        talk.start(
            new Event() {
                @Override
                public String type() {
                    return "newComment";
                }

                @Override
                public Issue issue() {
                    return null;
                }

                @Override
                public Comment comment() {
                    return comment;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }
            }
        );
    }

    /**
     * Understand can start the conversation by categorizing the
     * event's comment as "confused" and sending the categorized event further,
     * to the next Conversation.
     */
    @Test
    public void startsConfusedConversation() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.body()).thenReturn("It's a wonderful world!");

        final Conversation next = event -> {
            MatcherAssert.assertThat(
                event.type(),
                Matchers.equalTo("confused")
            );
            MatcherAssert.assertThat(
                event.project(),
                Matchers.is(project)
            );
            MatcherAssert.assertThat(
                event.comment(),
                Matchers.is(comment)
            );
            return null;
        };
        final Conversation talk = new Understand(next);

        talk.start(
            new Event() {
                @Override
                public String type() {
                    return "newComment";
                }

                @Override
                public Issue issue() {
                    return null;
                }

                @Override
                public Comment comment() {
                    return comment;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }
            }
        );
    }

}
