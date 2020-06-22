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

import javax.json.Json;
import javax.json.JsonArray;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;

/**
 * Github Repo invitations.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 * @todo #190:30min Add an implementation of interface Invitation,
 *  called GithubInvitation, which will take the invitation's JSON
 *  and work with it.
 */
final class GithubRepoInvitations implements Invitations {

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
     * @param repoInvitationsUri API uri.
     * @param accessToken User access token.
     */
    GithubRepoInvitations(
        final URI repoInvitationsUri,
        final String accessToken
    ) {
        this.repoInvitationsUri = repoInvitationsUri;
        this.accessToken = accessToken;
    }

    @Override
    public Iterator<Invitation> iterator() {
        final JsonArray invitations = this.fetchInvitations();
        return null;
    }

    /**
     * Fetch invitations.
     * @return JsonArray.
     */
    private JsonArray fetchInvitations() {
        try {
            final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .uri(this.repoInvitationsUri)
                        .header("Content-Type", "application/json")
                        .headers("Authorization", "token " + this.accessToken)
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
            final int status = response.statusCode();
            if(status == HttpURLConnection.HTTP_OK) {
                return Json.createReader(
                    new StringReader(response.body())
                ).readArray();
            } else {
                throw new IllegalStateException(
                    "Unexpected response when fetching ["
                  + this.repoInvitationsUri +"]. "
                  + "Expected 200 OK, but got " + status + "."
                );
            }
        } catch (final IOException | InterruptedException ex) {
            throw new IllegalStateException(
                "Couldn't fetch invitations + ["
              + this.repoInvitationsUri.toString() +"]",
                ex
            );
        }
    }
}
