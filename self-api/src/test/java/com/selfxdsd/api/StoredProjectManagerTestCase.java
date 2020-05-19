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
package com.selfxdsd.api;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StoredProjectManager}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredProjectManagerTestCase {

    /**
     * StoredProjectManager returns its id.
     */
    @Test
    public void returnsId() {
        final ProjectManager manager = new StoredProjectManager(
            1, "123token", Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.id(),
            Matchers.equalTo(1)
        );
    }

    /**
     * StoredProjectManager returns its access token.
     */
    @Test
    public void returnsAccessToken() {
        final ProjectManager manager = new StoredProjectManager(
            1, "123token", Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.accessToken(),
            Matchers.equalTo("123token")
        );
    }

    /**
     * StoredProjectManager returns its assigned projects.
     */
    @Test
    public void returnsProjects() {
        final Projects assigned = Mockito.mock(Projects.class);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.assignedTo(1)).thenReturn(assigned);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final ProjectManager manager = new StoredProjectManager(
            1, "123token", storage
        );
        MatcherAssert.assertThat(
            manager.projects(),
            Matchers.is(assigned)
        );
    }

    /**
     * StoredProjectManager can assign a Repo to the PM it represents.
     */
    @Test
    public void assingsRepo() {
        final Repo repo = Mockito.mock(Repo.class);

        final Storage storage = Mockito.mock(Storage.class);
        final Projects projects = Mockito.mock(Projects.class);
        Mockito.doNothing().when(projects).register(Mockito.any());
        Mockito.when(storage.projects()).thenReturn(projects);

        final ProjectManager manager = new StoredProjectManager(
            1, "123token", storage
        );

        final Project assigned = manager.assign(repo);

        Mockito.verify(projects, Mockito.times(1)).register(Mockito.any());
        MatcherAssert.assertThat(
            assigned.projectManager(),
            Matchers.is(manager)
        );
        MatcherAssert.assertThat(
            assigned.repo(),
            Matchers.is(repo)
        );
    }
}
