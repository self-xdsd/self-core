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
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link IgnoreBots}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.25
 * @checkstyle ExecutableStatementCount (300 lines)
 */
public final class IgnoreBotsTestCase {

    /**
     * Ignores a Comment coming from a known bot.
     */
    @Test
    public void ignoresKnownBot() {
        final Conversation ignore = new IgnoreBots(
            next -> {
                throw new IllegalStateException(
                    "Next Conversation should not start."
                );
            }
        );
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("rultor");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.storage()).thenThrow(
            new IllegalStateException(
                "Project.storage() should not be called!"
            )
        );

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);

        ignore.start(event);

        Mockito.verify(event, Mockito.times(1)).comment();
        Mockito.verify(event, Mockito.times(1)).project();
    }

    /**
     * Ignores a Comment coming from a PM.
     */
    @Test
    public void ignoresPm() {
        final Conversation ignore = new IgnoreBots(
            next -> {
                throw new IllegalStateException(
                    "Next Conversation should not start."
                );
            }
        );
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("ana");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);

        final ProjectManagers managers = Mockito.mock(ProjectManagers.class);
        Mockito
            .when(managers.getByUsername("ana", Provider.Names.GITHUB))
            .thenReturn(Mockito.mock(ProjectManager.class));
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(managers);
        Mockito.when(project.storage()).thenReturn(storage);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);

        ignore.start(event);

        Mockito.verify(event, Mockito.times(1)).comment();
        Mockito.verify(event, Mockito.times(2)).project();
        Mockito.verify(project, Mockito.times(1)).storage();
        Mockito.verify(storage, Mockito.times(1)).projectManagers();
        Mockito.verify(managers, Mockito.times(1))
            .getByUsername("ana", Provider.Names.GITHUB);
    }

    /**
     * A comment coming from an author who is not a known bot or PM is not
     * ignored (the next conversation is started).
     */
    @Test
    public void doesNotIgnoreUnknownAuthor() {
        final Conversation next = Mockito.mock(Conversation.class);
        final Conversation ignore = new IgnoreBots(next);

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("ana");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);

        final ProjectManagers managers = Mockito.mock(ProjectManagers.class);
        Mockito
            .when(managers.getByUsername("ana", Provider.Names.GITHUB))
            .thenReturn(null);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(managers);
        Mockito.when(project.storage()).thenReturn(storage);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);

        ignore.start(event);

        Mockito.verify(event, Mockito.times(1)).comment();
        Mockito.verify(event, Mockito.times(2)).project();
        Mockito.verify(project, Mockito.times(1)).storage();
        Mockito.verify(storage, Mockito.times(1)).projectManagers();
        Mockito.verify(managers, Mockito.times(1))
            .getByUsername("ana", Provider.Names.GITHUB);

        Mockito.verify(next, Mockito.times(1)).start(event);
    }
}
