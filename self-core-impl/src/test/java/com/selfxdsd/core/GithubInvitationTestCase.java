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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.URI;

/**
 * Unit tests for {@link GithubInvitation}.
 * @author criske
 * @version $Id$
 * @since 0.0.30
 */
public final class GithubInvitationTestCase {

    /**
     * A GithubInvitation has a json object wrapped.
     */
    @Test
    public void hasJson() {
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .build();
        final Invitation invitation = new GithubInvitation(
            Mockito.mock(JsonResources.class),
            URI.create("https://api.github.com/repos/john/test/invitations"),
            json,
            new Github(Mockito.mock(User.class), Mockito.mock(Storage.class))
        );
        MatcherAssert.assertThat(invitation.json(), Matchers.equalTo(json));
    }

    /**
     * GithubInvitation.accept() works.
     */
    @Test
    public void acceptWorks(){
        final MockJsonResources res = new MockJsonResources(
            mockRequest -> {
                return new MockJsonResources
                    .MockResource(204, JsonValue.NULL);
            }
        );
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .add("repository", Json.createObjectBuilder()
                .add("full_name", "john/test")
                .build())
            .build();
        final Github github = new Github(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );
        final Invitation invitation = new GithubInvitation(
            res,
            URI.create("https://api.github.com/repos/john/test/invitations"),
            json,
            github
        );

        invitation.accept();

        MatcherAssert.assertThat(res.requests(), Matchers.iterableWithSize(1));

        final MockJsonResources.MockRequest invReq = res.requests().first();
        MatcherAssert.assertThat(invReq.getMethod(),
            Matchers.equalTo("PATCH"));
        MatcherAssert.assertThat(invReq.getUri().toString(),
            Matchers.equalTo(
                "https://api.github.com/repos/john/test/invitations/1"
            )
        );
    }

    /**
     * GithubInvitation.accept() fails.
     */
    @Test
    public void acceptFails(){
        final MockJsonResources res = new MockJsonResources(
            mockRequest -> new MockJsonResources
                .MockResource(401, JsonValue.NULL)
        );
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .add("repository", Json.createObjectBuilder()
                .add("full_name", "john/test")
                .build())
            .build();
        final Github github = new Github(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );
        final Invitation invitation = new GithubInvitation(
            res,
            URI.create("https://api.github.com/repos/john/test/invitations"),
            json,
            github
        );

        invitation.accept();

        //starring request is not called, only invitation request is present
        MatcherAssert.assertThat(res.requests(), Matchers.iterableWithSize(1));

        final MockJsonResources.MockRequest invReq = res.requests().first();
        MatcherAssert.assertThat(invReq.getMethod(),
            Matchers.equalTo("PATCH"));
        MatcherAssert.assertThat(invReq.getUri().toString(),
            Matchers.equalTo(
                "https://api.github.com/repos/john/test/invitations/1"
            )
        );
    }
}
