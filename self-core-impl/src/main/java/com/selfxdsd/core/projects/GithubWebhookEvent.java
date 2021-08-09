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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Webhook event coming from Github. This is the event which triggers
 * everything (conversations, steps etc).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.61
 */
final class GithubWebhookEvent implements Event {

    /**
     * Project where the event happened.
     */
    private final Project project;

    /**
     * Event type.
     */
    private final String type;

    /**
     * Event payload.
     */
    private final JsonObject event;

    /**
     * Ctor.
     * @param project Project where the event happened.
     * @param type Type.
     * @param payload Payload.
     */
    GithubWebhookEvent(
        final Project project,
        final String type,
        final String payload
    ) {
        this.project = project;
        this.type = type;
        this.event  = Json.createReader(
            new StringReader(payload)
        ).readObject();
    }

    @Override
    public String type() {
        final String resolved;
        if ("issues".equalsIgnoreCase(type)
            || "pull_request".equalsIgnoreCase(type)) {
            final String act = event.getString("action");
            if ("opened".equalsIgnoreCase(act)) {
                resolved = Type.NEW_ISSUE;
            } else if ("reopened".equalsIgnoreCase(act)) {
                resolved = Type.REOPENED_ISSUE;
            } else {
                resolved = type;
            }
        } else if("repository".equalsIgnoreCase(type)){
            final String act = event.getString("action");
            if("renamed".equalsIgnoreCase(act)) {
                resolved = Type.REPO_RENAMED;
            } else {
                resolved = type;
            }
        } else {
            resolved = type;
        }
        return resolved;
    }

    @Override
    public Issue issue() {
        final JsonObject jsn;
        if ("pull_request".equalsIgnoreCase(type)) {
            jsn = this.event.getJsonObject("pull_request");
        } else {
            jsn = this.event.getJsonObject("issue");
        }
        final String repoFullName = project.repoFullName();
        return this.project.projectManager().provider().repo(
            repoFullName.split("/")[0],
            repoFullName.split("/")[1]
        ).issues().received(jsn);
    }

    @Override
    public Comment comment() {
        return this.issue().comments().received(
            this.event.getJsonObject("comment")
        );
    }

    @Override
    public Commit commit() {
        final Commit commit;
        if("push".equalsIgnoreCase(this.type)) {
            final JsonObject latest = this.event.getJsonArray(
                "commits"
            ).getJsonObject(0);
            final String repoFullName = this.project.repoFullName();
            commit = this.project.projectManager().provider().repo(
                repoFullName.split("/")[0],
                repoFullName.split("/")[1]
            ).commits().getCommit(latest.getString("id"));
        } else {
            commit = null;
        }
        return commit;
    }

    @Override
    public Project project() {
        return project;
    }

}
