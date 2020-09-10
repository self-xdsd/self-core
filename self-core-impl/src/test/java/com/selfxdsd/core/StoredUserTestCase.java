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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StoredUser}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredUserTestCase {

    /**
     * StoredUser can return its username.
     */
    @Test
    public void returnsUsername() {
        final User user = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            user.username(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * StoredUser can return its role.
     */
    @Test
    public void returnsRole() {
        final User user = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "admin",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            user.role(),
            Matchers.equalTo("admin")
        );
    }

    /**
     * StoredUser can return its email.
     */
    @Test
    public void returnsEmail() {
        final User user = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            user.email(),
            Matchers.equalTo("amihaiemil@gmail.com")
        );
    }

    /**
     * StoredUser can return its provider.
     */
    @Test
    public void returnsProvider() {
        final User ghUser = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            ghUser.provider(),
            Matchers.instanceOf(Github.class)
        );

        final User glUser = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITLAB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            glUser.provider(),
            Matchers.instanceOf(Gitlab.class)
        );
    }

    /**
     * StoredUser can return its Projects.
     */
    @Test
    public void returnsProjects() {
        final Storage storage = Mockito.mock(Storage.class);
        final User ghUser = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            storage
        );

        final Projects owned = Mockito.mock(Projects.class);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.ownedBy(ghUser)).thenReturn(owned);
        Mockito.when(storage.projects()).thenReturn(all);

        MatcherAssert.assertThat(
            ghUser.projects(),
            Matchers.is(owned)
        );
    }

    /**
     * StoredUser can return the Contributor.
     */
    @Test
    public void returnsContributor() {
        final Storage storage = Mockito.mock(Storage.class);
        final User ghUser = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            storage
        );

        final Contributor contributor = Mockito.mock(Contributor.class);
        final Contributors all = Mockito.mock(Contributors.class);
        Mockito.when(
            all.getById("amihaiemil", Provider.Names.GITHUB)
        ).thenReturn(contributor);
        Mockito.when(storage.contributors()).thenReturn(all);

        MatcherAssert.assertThat(
            ghUser.asContributor(),
            Matchers.is(contributor)
        );
    }

    /**
     * Can compare two StoredUser objects.
     */
    @Test
    public void comparesStoredUserObjects() {
        final User user = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        final User userTwo = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(user, Matchers.equalTo(userTwo));
    }

    /**
     * Verifies HashCode generation from StoredUser.
     */
    @Test
    public void verifiesStoredUserHashcode() {
        final User user = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        final User userTwo = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(user.hashCode(),
            Matchers.equalTo(userTwo.hashCode()));
    }

}
