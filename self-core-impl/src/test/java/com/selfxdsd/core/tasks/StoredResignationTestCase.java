/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permission is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Unit tests for {@link StoredResignation}.
 * @author criske
 * @version $Id$
 * @since 0.0.21
 */
public final class StoredResignationTestCase {

    /**
     * {@link StoredResignation} returns its details.
     */
    @Test
    public void returnsItsDetails(){
        final Task task = Mockito.mock(Task.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        final LocalDateTime timestamp = LocalDateTime.now();
        final Resignation resignation = new StoredResignation(
            task,
            contributor,
            timestamp,
            Resignations.Reason.ASKED
        );
        MatcherAssert.assertThat(resignation.task(), Matchers.equalTo(task));
        MatcherAssert.assertThat(resignation.contributor(),
            Matchers.equalTo(contributor));
        MatcherAssert.assertThat(resignation.timestamp(),
            Matchers.equalTo(timestamp));
        MatcherAssert.assertThat(resignation.reason(),
            Matchers.equalTo(Resignations.Reason.ASKED));
    }

    /**
     * {@link StoredResignation} respects equals contract.
     */
    @Test
    public void respectsEqualsContract(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.issueId()).thenReturn("1223");
        Mockito.when(contributor.username()).thenReturn("mike");
        final Resignation resignation = new StoredResignation(task, contributor,
            LocalDateTime.now(), Resignations.Reason.ASKED);

        final Contributor otherContrib = Mockito.mock(Contributor.class);
        Mockito.when(otherContrib.username()).thenReturn("mike");
        final Resignation other = new StoredResignation(task, otherContrib,
            LocalDateTime.now(), Resignations.Reason.ASKED);


        MatcherAssert.assertThat(resignation.equals(resignation),
            Matchers.is(true));

        MatcherAssert.assertThat(resignation.equals(other),
            Matchers.is(true));

        MatcherAssert.assertThat(resignation.equals(new Object()),
            Matchers.is(false));

    }


    /**
     * {@link StoredResignation} respects hash contract.
     */
    @Test
    public void respectsHashContract(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.issueId()).thenReturn("1223");
        Mockito.when(contributor.username()).thenReturn("mike");

        final Resignation resignation = new StoredResignation(task, contributor,
            LocalDateTime.now(), Resignations.Reason.ASKED);

        MatcherAssert.assertThat(resignation.hashCode(),
            Matchers.is(Objects.hash("1223", "john/test",
                "github", "mike")));
    }

}