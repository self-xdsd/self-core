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

import com.selfxdsd.api.Invitation;
import com.selfxdsd.api.Invitations;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Github Repo invitations.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
final class GithubRepoInvitations implements Invitations {

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * API uri for the repo invitations.
     */
    private final URI repoInvitationsUri;

    /**
     * User access token.
     */
    private final String accessToken;

    /**
     * Ctor.
     * @param resources Github's JSON resources.
     * @param repoInvitationsUri API uri.
     * @param accessToken User access token.
     */
    GithubRepoInvitations(
        final JsonResources resources,
        final URI repoInvitationsUri,
        final String accessToken
    ) {
        this.resources = resources;
        this.repoInvitationsUri = repoInvitationsUri;
        this.accessToken = accessToken;
    }

    @Override
    public Iterator<Invitation> iterator() {
        final List<Invitation> invitations = this.fetchInvitations().stream()
            .map(
                jsonValue -> new GithubInvitation(
                    (JsonObject) jsonValue,
                    this.repoInvitationsUri,
                    this.accessToken
                )
            ).collect(Collectors.toList());
        return invitations.iterator();
    }

    /**
     * Fetch invitations.
     * @return JsonArray.
     */
    private JsonArray fetchInvitations() {
        final Resource invitations = this.resources.get(
            this.repoInvitationsUri, this.accessToken
        );
        if(invitations.statusCode() == HttpURLConnection.HTTP_OK) {
            return invitations.asJsonArray();
        } else {
            throw new IllegalStateException(
                "Unexpected response when fetching [" + this.resources +"]. "
              + "Expected 200 OK, but got " + invitations.statusCode() + "."
            );
        }
    }
}
