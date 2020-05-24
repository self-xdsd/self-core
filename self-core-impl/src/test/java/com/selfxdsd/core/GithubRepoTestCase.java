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

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.api.User;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GithubRepo}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubRepoTestCase {

    /**
     * A GithubRepo can return its owner.
     */
    @Test
    public void returnsOwner() {
        final User owner = Mockito.mock(User.class);
        final Repo repo = new GithubRepo(
            owner,
            URI.create("http://localhost:8080"),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repo.owner(), Matchers.is(owner));
    }

    /**
     * A GithubRepo can be activated. The InMemory storage
     * comes with a pre-registered ProjectManager with id 1.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void activatesRepo() throws Exception {
        final Storage storage = new InMemory();
        MatcherAssert.assertThat(
            storage.projects(),
            Matchers.iterableWithSize(0)
        );
        MatcherAssert.assertThat(
            storage.projects().assignedTo(1),
            Matchers.iterableWithSize(0)
        );

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        when(provider.name()).thenReturn("github");
        when(user.provider()).thenReturn(provider);
        final Repo repo = new GithubRepo(
            user,
            URI.create("http://localhost:8080"),
            storage
        );
        MatcherAssert.assertThat(repo.activate(), Matchers.notNullValue());
        MatcherAssert.assertThat(
            storage.projects(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.projects().assignedTo(1),
            Matchers.iterableWithSize(1)
        );
    }

}
