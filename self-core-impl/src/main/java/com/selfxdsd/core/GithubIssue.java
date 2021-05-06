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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.function.Supplier;

/**
 * An Issue in a Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #1124:60min When fetching the Issue JSON, read the ETAG from the
 *  response http header and add it to the JSON as a string attribute.
 */
final class GithubIssue implements Issue {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubIssue.class
    );

    /**
     * Issue base uri.
     */
    private final URI issueUri;

    /**
     * Supplier of Issue JSON as returned by Github's API.
     */
    private final Supplier<JsonObject> json;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param issueUri Issue URI.
     * @param storage Self's Storage.
     * @param resources JSON Resources.
     */
    GithubIssue(
        final URI issueUri,
        final Storage storage,
        final JsonResources resources
    ) {
        this(
            issueUri,
            new Supplier<>() {

                /**
                 * Cached json. Make sure to read it from the API only once.
                 */
                private JsonObject json;

                @Override
                public JsonObject get() {
                    if(this.json == null) {
                        final Resource resource = resources.get(issueUri);
                        switch (resource.statusCode()) {
                            case HttpURLConnection.HTTP_OK:
                                this.json = resource.asJsonObject();
                                break;
                            default:
                                throw new IllegalStateException(
                                    "Could not get the issue ["
                                    + issueUri.toString() + "]. "
                                    + "Received status code: "
                                    + resource.statusCode()
                                );
                        }
                    }
                    return this.json;
                }
            },
            storage,
            resources
        );
    }

    /**
     * Ctor.
     * @param issueUri Issues base URI.
     * @param json Supplier of Json Issue as returned by Github's API.
     * @param storage Storage.
     * @param resources Github's JSON Resources.
     */
    GithubIssue(
        final URI issueUri,
        final Supplier<JsonObject> json,
        final Storage storage,
        final JsonResources resources
    ) {
        this.issueUri = issueUri;
        this.json = json;
        this.storage = storage;
        this.resources = resources;
    }

    @Override
    public String issueId() {
        return String.valueOf(this.json.get().getInt("number"));
    }

    @Override
    public String provider() {
        return "github";
    }

    @Override
    public String role() {
        return new LabelsRole(this).asString();
    }

    @Override
    public String repoFullName() {
        final String[] parts = this.json.get().getString("url").substring(
            "https://api.github.com/repos/".length()
        ).split("/");
        return parts[0] + "/" + parts[1];
    }

    @Override
    public String author() {
        return this.json.get().getJsonObject("user").getString("login");
    }

    @Override
    public String body() {
        return this.json.get().getString("body");
    }

    @Override
    public String assignee() {
        final JsonValue assignee = this.json.get().get("assignee");
        final String username;
        if (assignee instanceof JsonObject) {
            username = ((JsonObject) assignee).getString("login");
        } else {
            username = null;
        }
        return username;
    }

    @Override
    public boolean assign(final String username) {
        final boolean assigned;
        LOG.debug(
            "Assigning user " + username + " to Issue ["
            + this.issueUri.toString() + "]..."
        );
        final Resource resource = this.resources.post(
            URI.create(this.issueUri + "/assignees"),
            Json.createObjectBuilder()
                .add(
                    "assignees",
                    Json.createArrayBuilder()
                        .add(username)
                        .build()
                ).build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_CREATED) {
            LOG.debug("User " + username + " assigned successfully!");
            assigned = true;
        } else {
            LOG.debug(
                "Problem while assigning user " + username + ". "
                + "Expected 201 CREATED, but got " + resource.statusCode()
            );
            assigned = false;
        }
        return assigned;
    }

    @Override
    public boolean unassign(final String username) {
        final boolean unassigned;
        LOG.debug(
            "Unassigning user " + username + " from Issue ["
            + this.issueUri.toString() + "]..."
        );
        final Resource resource = this.resources.delete(
            URI.create(this.issueUri + "/assignees"),
            Json.createObjectBuilder()
                .add(
                    "assignees",
                    Json.createArrayBuilder()
                        .add(username)
                        .build()
                ).build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug("User " + username + " unassigned successfully!");
            unassigned = true;
        } else {
            LOG.debug(
                "Problem while unassigning user " + username + ". "
                + "Expected 200 OK, but got " + resource.statusCode()
            );
            unassigned = false;
        }
        return unassigned;
    }

    @Override
    public JsonObject json() {
        return this.json.get();
    }

    @Override
    public Comments comments() {
        return new DoNotRepeat(
            new GithubIssueComments(this.issueUri, this.resources)
        );
    }

    @Override
    public void close() {
        LOG.debug(
            "Cosing Issue [" + this.issueUri.toString() + "]..."
        );
        final Resource resource = this.resources.patch(
            this.issueUri,
            Json.createObjectBuilder()
                .add("state", "closed")
                .build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug(
                "Issue [" + this.issueUri.toString() + "] "
                + "successfully closed."
            );
        } else {
            LOG.error(
                "Problem while closing Issue [" + this.issueUri.toString()
                + "]. Expected 200 OK, received " + resource.statusCode()
            );
        }
    }

    @Override
    public void reopen() {
        LOG.debug(
            "Reopening Issue [" + this.issueUri.toString() + "]..."
        );
        final Resource resource = this.resources.patch(
            this.issueUri,
            Json.createObjectBuilder()
                .add("state", "open")
                .build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug(
                "Issue [" + this.issueUri.toString() + "] "
                + "successfully reopened."
            );
        } else {
            LOG.error(
                "Problem while reopening Issue [" + this.issueUri.toString()
                + "]. Expected 200 OK, received " + resource.statusCode()
            );
        }
    }

    @Override
    public boolean isClosed() {
        return "closed".equalsIgnoreCase(this.json.get().getString("state"));
    }

    @Override
    public boolean isPullRequest() {
        return this.json.get().getString("html_url")
            .endsWith("/pull/" + this.issueId());
    }

    @Override
    public Estimation estimation() {
        return new LabelsEstimation(this);
    }

    @Override
    public Labels labels() {
        return new GithubIssueLabels(
            URI.create(this.issueUri.toString() + "/labels"),
            this.resources
        );
    }
}
