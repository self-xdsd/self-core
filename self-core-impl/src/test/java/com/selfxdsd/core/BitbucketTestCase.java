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

import com.selfxdsd.api.Organizations;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Bitbucket}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.62
 */
public final class BitbucketTestCase {

    /**
     * Bitbucket provider has a name.
     */
    @Test
    public void hasName() {
        final Provider bitbucket = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            bitbucket.name(),
            Matchers.is(Provider.Names.BITBUCKET)
        );
    }

    /**
     * Bitbucket provider returns one of its repositories.
     */
    @Test
    public void returnsRepository() {
        final Provider bitbucket = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        final Repo repo = bitbucket.repo("john", "test");
        MatcherAssert.assertThat(repo, Matchers
            .instanceOf(BitbucketRepo.class));
    }

    /**
     * Bitbucket provider returns its invitations.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsInvitations() {
        final Provider bitbucket = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        bitbucket.invitations();
    }

    /**
     * Bitbucket provider returns its organizations.
     */
    @Test
    public void organizations() {
        final Provider bitbucket = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        Organizations organizations = bitbucket.organizations();
        MatcherAssert.assertThat(organizations,
                Matchers.instanceOf(BitbucketOrganizations.class));
    }

    /**
     * Bitbucket provider can be created with access token.
     */
    @Test
    public void createsProviderWithToken() {
        final JsonResources resources = Mockito.mock(JsonResources.class);
        new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            resources
        ).withToken("bitbucket123");
        Mockito.verify(resources, Mockito.times(1))
            .authenticated(Mockito.any(AccessToken.Bitbucket.class));
    }

    /**
     * Bitbucket provider is created without access token when token is null.
     */
    @Test
    public void createsProviderWithoutTokenWhenTokenNull() {
        final JsonResources resources = Mockito.mock(JsonResources.class);
        new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            resources
        ).withToken(null);
        Mockito.verify(resources, Mockito.never())
            .authenticated(Mockito.any(AccessToken.Bitbucket.class));
    }

    /**
     * Bitbucket provider is created without access token when token is blank.
     */
    @Test
    public void createsProviderWithoutTokenWhenTokenBlank() {
        final JsonResources resources = Mockito.mock(JsonResources.class);
        new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            resources
        ).withToken(" ");
        Mockito.verify(resources, Mockito.never())
            .authenticated(Mockito.any(AccessToken.Bitbucket.class));
    }
}