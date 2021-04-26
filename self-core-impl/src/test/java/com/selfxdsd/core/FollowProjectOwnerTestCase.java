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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link FollowProjectOwner}.
 * @author Mihai Andronache (amihaiemil@gmail.com)\
 * @since 0.0.75
 * @version $Id$
 */
public final class FollowProjectOwnerTestCase {

    /**
     * FollowProjectOwner delegates json() to the original Invitation.
     */
    @Test
    public void delegatesJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.json()).thenReturn(json);
        final Invitation followPo = new FollowProjectOwner(origin);
        MatcherAssert.assertThat(
            followPo.json(),
            Matchers.is(json)
        );
        Mockito.verify(origin, Mockito.times(1)).json();
    }

    /**
     * FollowProjectOwner delegates inviter() to the original Invitation.
     */
    @Test
    public void delegatesInviter() {
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.inviter()).thenReturn("mihai");
        final Invitation followPo = new FollowProjectOwner(origin);
        MatcherAssert.assertThat(
            followPo.inviter(),
            Matchers.is("mihai")
        );
        Mockito.verify(origin, Mockito.times(1)).inviter();
    }

    /**
     * FollowProjectOwner delegates repo() to the original Invitation.
     */
    @Test
    public void delegatesRepo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.repo()).thenReturn(repo);
        final Invitation followPo = new FollowProjectOwner(origin);
        MatcherAssert.assertThat(
            followPo.repo(),
            Matchers.is(repo)
        );
        Mockito.verify(origin, Mockito.times(1)).repo();
    }

    /**
     * FollowProjectOwner can accept the original invitation and follows the PO.
     */
    @Test
    public void acceptsAndFollowsPo() {
        final Provider provider = Mockito.mock(Provider.class);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(provider);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.owner()).thenReturn(owner);
        final Invitation origin = Mockito.mock(Invitation.class);
        Mockito.when(origin.repo()).thenReturn(repo);
        Mockito.when(origin.inviter()).thenReturn("amihaiemil");

        final Invitation followPo = new FollowProjectOwner(origin);

        followPo.accept();

        Mockito.verify(origin, Mockito.times(1)).accept();
        Mockito.verify(origin, Mockito.times(1)).repo();
        Mockito.verify(repo, Mockito.times(1)).owner();
        Mockito.verify(owner, Mockito.times(1))
            .provider();
        Mockito.verify(provider, Mockito.times(1))
            .follow("amihaiemil");
    }
}
