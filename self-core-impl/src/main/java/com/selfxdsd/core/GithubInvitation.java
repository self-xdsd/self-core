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

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A Github Repo invitation.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 * @todo #225:30min When accepting an invitation, get the response status code
 *  and if it's different from 204 NO CONTENT, log a warning or something.
 */
final class GithubInvitation implements Invitation {

    /**
     * Invitation as JSON.
     */
    private final JsonObject json;

    /**
     * URI of this invitation.
     */
    private final URI uri;

    /**
     * Access token.
     */
    private final String accessToken;

    /**
     * Ctor.
     * @param json Invitation in JSON format.
     * @param baseUri Base URI of the Invitations API.
     * @param accessToken Access Token.
     */
    GithubInvitation(
        final JsonObject json,
        final URI baseUri,
        final String accessToken
    ) {
        this.json = json;
        this.uri = URI.create(
            baseUri.toString() + "/" + json.getJsonNumber("id")
        );
        this.accessToken = accessToken;
    }

    @Override
    public JsonObject json() {
        return this.json;
    }

    @Override
    public void accept() {
        try {
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .uri(this.uri)
                        .method("PATCH", HttpRequest.BodyPublishers.noBody())
                        .header("Content-Type", "application/json")
                        .header("Authorization", "token " + this.accessToken)
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
        } catch (final IOException | InterruptedException ex) {
            throw new IllegalStateException(
                "Couldn't accept Invitation + [" + this.uri.toString() + "]",
                ex
            );
        }
    }
}
