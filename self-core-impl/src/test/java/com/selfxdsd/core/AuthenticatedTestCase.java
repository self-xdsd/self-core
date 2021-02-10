/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
import com.selfxdsd.api.storage.Storage;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link BaseSelf.Authenticated}.
 *
 * @author Aislan Nadrowski (aislan.nadrowski@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
public final class AuthenticatedTestCase {

    /**
     * Should return the username.
     */
    @Test
    public void returnsUsername() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("john");
        final BaseSelf.Authenticated authenticated = 
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.username(),
            Matchers.equalTo("john")
        );
    }

    /**
     * Should return the email.
     */
    @Test
    public void returnsEmail() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.email()).thenReturn("john@john.com");
        final BaseSelf.Authenticated authenticated = 
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.email(),
            Matchers.equalTo("john@john.com")
        );
    }

    /**
     * Authenticated can return the User's role.
     */
    @Test
    public void returnsRole() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final BaseSelf.Authenticated authenticated =
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.role(),
            Matchers.equalTo("user")
        );
    }

    /**
     * Should verify the provider.
     */
    @Test
    public void verifyProvider() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(user.provider()).thenReturn(provider);
        final BaseSelf.Authenticated authenticated = 
            new BaseSelf.Authenticated(user, "tok3n");
        final Provider prov = authenticated.provider();

        Mockito.verify(provider).withToken("tok3n");
    }

    /**
     * Should returns the projects.
     */
    @Test
    public void returnsProjects() {
        final User user = Mockito.mock(User.class);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(user.projects()).thenReturn(all);
        final BaseSelf.Authenticated authenticated = 
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.projects(),
            Matchers.equalTo(all)
        );
    }

    /**
     * Should returns the corresponding Contributor.
     */
    @Test
    public void returnsContributor() {
        final User user = Mockito.mock(User.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        final BaseSelf.Authenticated authenticated =
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.asContributor(),
            Matchers.is(contributor)
        );
    }

    /**
     * Should return the Admin from the underlying User.
     */
    @Test
    public void returnsAdmin() {
        final User user = Mockito.mock(User.class);
        final Admin admin = Mockito.mock(Admin.class);
        Mockito.when(user.asAdmin()).thenReturn(admin);
        final BaseSelf.Authenticated authenticated =
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.asAdmin(),
            Matchers.is(admin)
        );
    }

    /**
     * Should return the ApiTokens from the underlying User.
     */
    @Test
    public void returnsApiTokens() {
        final User user = Mockito.mock(User.class);
        final ApiTokens tokens = Mockito.mock(ApiTokens.class);
        Mockito.when(user.apiTokens()).thenReturn(tokens);
        final BaseSelf.Authenticated authenticated =
            new BaseSelf.Authenticated(user, "tok3n");

        MatcherAssert.assertThat(
            authenticated.apiTokens(),
            Matchers.is(tokens)
        );
    }

    /**
     * Registers a token.
     */
    @Test
    public void registersNewApiToken() {
        final Storage storage = Mockito.mock(
            Storage.class,
            Mockito.withSettings()
                .verboseLogging()
                .defaultAnswer(Mockito.RETURNS_DEEP_STUBS)
        );
        final User original = new StoredUser(
            "amihaiemil",
            "amihaiemil@gmail.com",
            "user",
            Provider.Names.GITHUB,
            storage
        );
        final User user = new BaseSelf.Authenticated(
            original,
            "secret"
        );
        user.register("abc", "secret", LocalDateTime.MAX);
        Mockito.verify(storage.apiTokens()).ofUser(Mockito.eq(original));
        Mockito.verify(storage.apiTokens().ofUser(original))
            .register(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(original)
            );
    }
}
