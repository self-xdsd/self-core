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
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link AuthorHasRoles}.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class AuthorHasRolesTestCase {

    /**
     * Author has roles the execute the next command.
     */
    @Test
    public void authorHasRoles(){
        final Storage storage = new InMemory();
        storage.contributors().register("john", "github");
        final Project project = storage
            .projects()
            .register(
                mockRepo("john/test", "github"),
                storage.projectManagers().pick("github"),
                "wbtoken123"
            );
        project.contracts()
            .addContract("john/test",
                "john", "github", BigDecimal.TEN, "PO");

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("john");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new AuthorHasRoles(
            onTrue,
            onFalse -> {
                throw new IllegalStateException(
                    "Should not be called!"
                );
            },
            Contract.Roles.ARCH, Contract.Roles.PO
        );
        step.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * Author has no appropriate roles the execute the next command.
     */
    @Test
    public void authorHasNoAppropriateRole(){
        final Storage storage = new InMemory();
        storage.contributors().register("john", "github");
        final Project project = storage
            .projects()
            .register(
                mockRepo("john/test", "github"),
                storage.projectManagers().pick("github"),
                "wbtoken123"
            );
        project.contracts()
            .addContract("john/test",
                "john", "github", BigDecimal.TEN, "PO");

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("john");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new AuthorHasRoles(
            onTrue -> {
                throw new IllegalStateException(
                    "Should not be called!"
                );
            },
            onFalse,
            Contract.Roles.DEV
        );
        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }

    /**
     * It will work with ANY role.
     */
    @Test
    public void authorHasAnyRole() {
        final Storage storage = new InMemory();
        storage.contributors().register("john", "github");
        final Project project = storage
            .projects()
            .register(
                mockRepo("john/test", "github"),
                storage.projectManagers().pick("github"),
                "wbtoken123"
            );
        project.contracts()
            .addContract("john/test",
                "john", "github", BigDecimal.TEN, "PO");

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("john");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(comment);

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new AuthorHasRoles(
            onTrue,
            onFalse -> {
                throw new IllegalStateException(
                    "Should not be called!"
                );
            },
            Contract.Roles.ANY
        );
        step.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * Mock a Repo for test.
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(final String fullName, final String provider) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }


}
