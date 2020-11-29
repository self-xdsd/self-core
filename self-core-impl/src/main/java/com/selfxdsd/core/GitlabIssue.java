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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * An Issue in a Gitlab repository.
 * <br/>
 * <a href="https://docs.gitlab.com/ee/api/issues.html#single-project-issue">
 *  See specification </a>
 *  <br/>
 *  For a merge request
 *  <a href ="https://docs.gitlab.com/ee/api/merge_requests.html#get-single-mr">
 *     See specification </a>
 * @author criske
 * @version $Id$
 * @since 0.0.38
 * @todo #723:60min Implement and test method `unassign()` for
 *  GitlabIssue by following GithubIssue as model.
 * @todo #724:60min Implement and test method `close()` for
 *  GitlabIssue by following GithubIssue as model.
 * @todo #725:60min Implement and test method `reopen()` for
 *  GitlabIssue by following GithubIssue as model.
 * @todo #726:60min Start implementing GitlabComments by following
 *  GithubComments as model. These will be used by GitlabIssue.
 */
final class GitlabIssue implements Issue {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabIssue.class
    );

    /**
     * Issue base uri.
     */
    private final URI issueUri;

    /**
     * Issue JSON as returned by Gitlab's API.
     */
    private final JsonObject json;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Gitlab's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param issueUri Issues base URI.
     * @param json Json Issue as returned by Gitlab's API.
     * @param storage Storage.
     * @param resources Gitlab's JSON Resources.
     */
    GitlabIssue(
        final URI issueUri,
        final JsonObject json,
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
        return String.valueOf(this.json.getInt("iid"));
    }

    @Override
    public String provider() {
        return Provider.Names.GITLAB;
    }

    @Override
    public String role() {
        final String role;
        if(this.isPullRequest()) {
            role = Contract.Roles.REV;
        } else {
            role = Contract.Roles.DEV;
        }
        return role;
    }

    @Override
    public String repoFullName() {
        return this.json
            .getJsonObject("references")
            .getString("full")
            .split("[#!]")[0];
    }

    @Override
    public String author() {
        return this.json.getJsonObject("author").getString("username");
    }

    @Override
    public String body() {
        return this.json.getString("description");
    }

    @Override
    public String assignee() {
        final JsonValue assignee = this.json.get("assignee");
        final String username;
        if (assignee instanceof JsonObject) {
            username = ((JsonObject) assignee).getString("username");
        } else {
            username = null;
        }
        return username;
    }

    /**
     * {@inheritDoc}
     * <br/>
     * For Gitlab, assigning is done via
     * <a href="https://docs.gitlab.com/ee/api/issues.html#edit-issue">
     *     updating the issue</a> with `assignee_id` attribute. Thus we need
     *  to get the user `id` by their "username" first.
     */
    @Override
    public boolean assign(final String username) {
        LOG.debug(
            "Assigning user " + username + " to Issue ["
                + this.issueUri + "]..."
        );
        boolean assigned = false;
        final Integer userId = this.findUserId(username);
        if(userId != null) {
            final Resource resource = this.resources.put(
                this.issueUri,
                Json.createObjectBuilder()
                    .add("assignee_id", userId)
                    .build()
            );
            if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
                LOG.debug("User \"" + username + "\" (id: " + userId + ") "
                    + "assigned successfully!");
                assigned = true;
            } else {
                LOG.debug(
                    "Problem while assigning user \"" + username + "\". "
                        + "Expected 200 OK, but got " + resource.statusCode()
                );
                assigned = false;
            }
        }
        return assigned;
    }

    @Override
    public boolean unassign(final String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }

    @Override
    public Comments comments() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void reopen() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isClosed() {
        return "closed".equalsIgnoreCase(this.json.getString("state"));
    }

    @Override
    public boolean isPullRequest() {
        return this.json.getString("web_url")
            .endsWith("/merge_requests/" + this.issueId());
    }

    @Override
    public int estimation() {
        final int estimation;
        if(this.isPullRequest()) {
            estimation = 30;
        } else {
            estimation = 60;
        }
        return estimation;
    }

    @Override
    public Labels labels() {
        return new GitlabIssueLabels(
            this.issueUri,
            this.resources,
            this.json
        );
    }

    /**
     * Find user id by searching it in projects/repo members.
     * @param username Username to query.
     * @return User id or null if not found.
     */
    private Integer findUserId(final String username) {
        final String projectPrefix = this.issueUri.toString()
            .split("/issues")[0];
        final URI projectMembersUri = URI.create(projectPrefix
            + "/search?scope=users&search=" + username);
        final Resource resource = this.resources.get(projectMembersUri);
        LOG.debug(
            "Searching for user \"" + username + "\" id in project members ["
                + projectMembersUri + "]..."
        );
        final Integer userId;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            userId = resource.asJsonArray().stream()
                .filter(o -> o.asJsonObject()
                    .getString("username")
                    .equals(username))
                .map(o -> o.asJsonObject().getInt("id"))
                .findFirst()
                .orElse(null);
            if (userId == null) {
                LOG.debug(
                    "User id for \"" + username + "\" was not found. Make sure "
                        + "they are part of the Issue's Project."
                );
            }
        } else {
            LOG.debug(
                "Could not get id for \"" + username + "\". Status code: "
                    + resource.statusCode()
            );
            userId = null;
        }
        return userId;
    }

}
