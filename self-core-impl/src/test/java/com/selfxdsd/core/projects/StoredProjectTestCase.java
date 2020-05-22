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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StoredProject}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredProjectTestCase {

    /**
     * StoredProject can return its ID.
     */
    @Test
    public void returnsId() {
        final Project project = new StoredProject(
            1, Mockito.mock(Repo.class), Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(project.projectId(), Matchers.is(1));
    }

    /**
     * StoredProject can return its Repo.
     */
    @Test
    public void returnsRepo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Project project = new StoredProject(
            1, repo, Mockito.mock(ProjectManager.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(project.repo(), Matchers.is(repo));
    }

    /**
     * StoredProject can return its ProjectManager.
     */
    @Test
    public void returnsProjectManager() {
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final Project project = new StoredProject(
            1, Mockito.mock(Repo.class), manager,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            project.projectManager(),
            Matchers.is(manager)
        );
    }

    /**
     * StoredProject can return its contracts.
     */
    @Test
    public void returnsContracts() {
        final Contracts all = Mockito.mock(Contracts.class);
        final Contracts ofProject = Mockito.mock(Contracts.class);
        Mockito.when(all.ofProject(1)).thenReturn(ofProject);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contracts()).thenReturn(all);

        final Project project = new StoredProject(
            1, Mockito.mock(Repo.class), Mockito.mock(ProjectManager.class),
            storage
        );
        MatcherAssert.assertThat(project.contracts(), Matchers.is(ofProject));
    }
}
