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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link HelloIssue}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.37
 */
public final class HelloIssueTestCase {

    /**
     * HelloIssue delegates json() to the original Invitation.
     */
    @Test
    public void delegatesJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.json()).thenReturn(json);
        final Invitation helloIssue = new HelloIssue(origin);
        MatcherAssert.assertThat(
            helloIssue.json(),
            Matchers.is(json)
        );
        Mockito.verify(origin, Mockito.times(1)).json();
    }

    /**
     * HelloIssue delegates repo() to the original Invitation.
     */
    @Test
    public void delegatesRepo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.repo()).thenReturn(repo);
        final Invitation helloIssue = new HelloIssue(origin);
        MatcherAssert.assertThat(
            helloIssue.repo(),
            Matchers.is(repo)
        );
        Mockito.verify(origin, Mockito.times(1)).repo();
    }

    /**
     * HelloIssue does not open any Issue if the acceptance of the
     * original Invitation failed.
     */
    @Test
    public void doesNotOpenIssueOnFailedAccept() {
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.doThrow(new IllegalStateException("Accept failed."))
            .when(origin).accept();
        Mockito.doThrow(
            new IllegalStateException("Repo should not be called!")
        ).when(origin).repo();
        final Invitation helloIssue = new HelloIssue(origin);
        try {
            helloIssue.accept();
            Assert.fail(
                "Original accept() should throw ISE!"
            );
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo("Accept failed.")
            );
        }
    }

    /**
     * HelloIssue can accept the original invitation and open the
     * 'hello' Issue.
     */
    @Test
    public void acceptsAndOpensIssue() {
        final Repo repo = Mockito.mock(Repo.class);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        Mockito.when(repo.owner()).thenReturn(owner);
        final Issues issues = Mockito.mock(Issues.class);
        Mockito.when(repo.issues()).thenReturn(issues);
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.repo()).thenReturn(repo);
        final HelloIssue helloIssue = new HelloIssue(origin);

        helloIssue.accept();

        Mockito.verify(origin, Mockito.times(1)).accept();
        Mockito.verify(origin, Mockito.times(1)).repo();
        Mockito.verify(repo, Mockito.times(1)).issues();
        Mockito.verify(issues, Mockito.times(1))
            .open(
                "Hello from Self XDSD!",
                String.format(
                    helloIssue.helloMessage(),
                    "mihai"
                ),
                "no-task"
            );
    }

}
