/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link SelfCore}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfCoreTestCase {

    /**
     * SelfCore can sign up a User on any platform.
     */
    @Test
    public void platformLoginWorks() {
        final User authUser = Mockito.mock(User.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Users all = Mockito.mock(Users.class);
        Mockito.when(
            all.signUp(
                "amihaiemil",
                Provider.Names.GITHUB,
                "amihaiemil@gmail.com"
            )
        ).thenReturn(authUser);
        Mockito.when(storage.users()).thenReturn(all);

        final Self self = new SelfCore(storage);
        final Login auth = new GithubLogin(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "123t"
        );

        MatcherAssert.assertThat(
            self.login(auth),
            Matchers.instanceOf(BaseSelf.Authenticated.class)
        );
    }

    /**
     * SelfCore can give us its ProjectManagers.
     */
    @Test
    public void returnsProjectManagers() {
        final ProjectManagers all = Mockito.mock(ProjectManagers.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(all);

        final Self self = new SelfCore(storage);
        MatcherAssert.assertThat(self.projectManagers(), Matchers.is(all));
    }

    /**
     * SelfCore can give us its Projects.
     */
    @Test
    public void returnsProjects() {
        final Projects all = Mockito.mock(Projects.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final Self self = new SelfCore(storage);
        MatcherAssert.assertThat(self.projects(), Matchers.is(all));
    }

    /**
     * SelfCore should close the underlying Storage when close() is called.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void closesStorageOnClose() throws Exception {
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.doNothing().when(storage).close();

        final Self self = new SelfCore(storage);
        self.close();
        Mockito.verify(storage, Mockito.times(1)).close();
    }
}
