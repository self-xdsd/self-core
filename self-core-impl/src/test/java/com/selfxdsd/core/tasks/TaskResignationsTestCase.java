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
package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Resignation;
import com.selfxdsd.api.Resignations;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link TaskResignations}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 */
public final class TaskResignationsTestCase {

    /**
     * TaskResignations can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Resignations resignations = new TaskResignations(
            Mockito.mock(Task.class),
            () -> {
                final List<Resignation> resigs = new ArrayList<>();
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                return resigs.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            resignations,
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * TaskResignations.ofTask returns self if the specified Task
     * is the same.
     */
    @Test
    public void ofTaskReturnsSelf() {
        final Task task = Mockito.mock(Task.class);
        final Resignations resignations = new TaskResignations(
            task,
            () -> {
                final List<Resignation> resigs = new ArrayList<>();
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                return resigs.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            resignations.ofTask(task),
            Matchers.is(resignations)
        );
    }

    /**
     * TaskResignations.ofTask(...) throws ISE if the specified
     * Task is a different one.
     */
    @Test(expected = IllegalStateException.class)
    public void ofTaskComplainsOnDifferentTask() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.project()).thenReturn(Mockito.mock(Project.class));
        final Task other = Mockito.mock(Task.class);
        final Resignations resignations = new TaskResignations(
            task,
            () -> {
                final List<Resignation> resigs = new ArrayList<>();
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                resigs.add(Mockito.mock(Resignation.class));
                return resigs.stream();
            },
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            resignations.ofTask(other),
            Matchers.nullValue()
        );
    }
}
