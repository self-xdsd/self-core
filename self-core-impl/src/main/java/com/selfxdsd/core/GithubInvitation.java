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
import com.selfxdsd.api.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * A Github Repo invitation.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
final class GithubInvitation implements Invitation {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubInvitation.class
    );

    /**
     * Github's JSON resources.
     */
    private final JsonResources resources;

    /**
     * URI of this invitation.
     */
    private final URI uri;

    /**
     * This invitation as JSON.
     */
    private final JsonObject json;

    /**
     * Parent Github.
     */
    private final Github github;

    /**
     * Ctor.
     * @param resources Github's JSON resources.
     * @param baseUri Base URI of the Invitations API.
     * @param json This invitation in JSON format.
     * @param github Parent Github.
     */
    GithubInvitation(
        final JsonResources resources,
        final URI baseUri,
        final JsonObject json,
        final Github github
    ) {
        this.resources = resources;
        this.uri = URI.create(
            baseUri.toString() + "/" + json.getJsonNumber("id")
        );
        this.json = json;
        this.github = github;
    }

    @Override
    public JsonObject json() {
        return this.json;
    }

    @Override
    public Repo repo() {
        final String repoFullName = this.json
            .getJsonObject("repository")
            .getString("full_name");
        LOG.debug("Starring Github repository " + repoFullName + "... ");
        return this.github.repo(
            repoFullName.split("/")[0],
            repoFullName.split("/")[1]
        );
    }

    @Override
    public void accept() {
        LOG.debug(
            "Accepting Github Repo Invitation ["
            + this.uri.toString() +"]..."
        );
        final Resource resp = this.resources
            .patch(this.uri, Json.createObjectBuilder().build());
        if(resp.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.debug("Invitation accepted.");
        } else {
            LOG.warn(
                "Problem when accepting invitation. "
                + "Expected 204 NO CONTENT, but got "
                + resp.statusCode()
            );
        }
    }
}
