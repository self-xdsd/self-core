/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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

import java.time.LocalDateTime;

/**
 * Unit tests for {@link SelfCore}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfCoreTestCase {

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
     * SelfCore can give us its PlatformInvoices when the User is an Admin.
     */
    @Test
    public void returnsPlatformInvoices() {
        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        final Storage storage = Mockito.mock(Storage.class);
        final User user = Mockito.mock(User.class);
        final Users users = Mockito.mock(Users.class);
        Mockito.when(storage.users()).thenReturn(users);
        Mockito.when(
                users.signUp(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(user);
        final Admin admin = Mockito.mock(Admin.class);
        Mockito.when(user.asAdmin()).thenReturn(admin);
        Mockito.when(admin.platformInvoices()).thenReturn(all);

        final Self self = new SelfCore(storage);
        final Login githubLogin = new GithubLogin(
                "amihaiemil", "amihaiemil@gmail.com", "gh123token"
        );
        MatcherAssert.assertThat(
                self.login(githubLogin)
                        .asAdmin()
                        .platformInvoices(),
                Matchers.is(all));
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

    /**
     * The authenticate(token) method returns null if the token does not
     * exist.
     */
    @Test
    public void userNullWhenApiTokenIsMissing() {
        final Storage storage = Mockito.mock(Storage.class);
        final ApiTokens all = Mockito.mock(ApiTokens.class);
        Mockito.when(all.getById("token123")).thenReturn(null);
        Mockito.when(storage.apiTokens()).thenReturn(all);

        final Self self = new SelfCore(storage);

        MatcherAssert.assertThat(
            self.authenticate("token123"),
            Matchers.nullValue()
        );
    }

    /**
     * The authenticate(token) method returns the User if the ApiToken
     * exists and the expiration is before now.
     */
    @Test
    public void userExistsWhenApiTokenAfterNow() {
        final LocalDateTime now = LocalDateTime.now();

        final Storage storage = Mockito.mock(Storage.class);
        final ApiTokens all = Mockito.mock(ApiTokens.class);

        final ApiToken token = Mockito.mock(ApiToken.class);
        final User owner = Mockito.mock(User.class);
        Mockito.when(token.owner()).thenReturn(owner);
        Mockito.when(token.expiration()).thenReturn(now.plusHours(1));

        Mockito.when(all.getById("token123")).thenReturn(token);
        Mockito.when(storage.apiTokens()).thenReturn(all);

        final Self self = new SelfCore(storage);

        MatcherAssert.assertThat(
            self.authenticate("token123"),
            Matchers.is(owner)
        );
    }

    /**
     * The authenticate(token) method returns null if the token exists,
     * but it is expired.
     */
    @Test
    public void userNullWhenApiTokenExpired() {
        final User user = Mockito.mock(User.class);
        final LocalDateTime now = LocalDateTime.now();

        final Storage storage = Mockito.mock(Storage.class);
        final ApiTokens all = Mockito.mock(ApiTokens.class);

        final ApiToken token = Mockito.mock(ApiToken.class);
        Mockito.when(token.owner()).thenReturn(user);
        Mockito.when(token.expiration()).thenReturn(now.minusHours(1));

        Mockito.when(all.getById("token123")).thenReturn(token);
        Mockito.when(storage.apiTokens()).thenReturn(all);

        final Self self = new SelfCore(storage);

        MatcherAssert.assertThat(
            self.authenticate("token123"),
            Matchers.nullValue()
        );
    }
}
