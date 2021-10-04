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
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Webhook event coming from GitLab. This is the event which triggers
 * everything (conversations, steps etc).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.61
 * @see <a href="https://docs.gitlab.com/ee/user/project/integrations/webhooks.html">Documentation.</a>
 */
final class GitlabWebhookEvent implements Event {

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
     * Cached Issue. We need this until we will be able to "receive"
     * Issues from this event via GitlabIssues.receive (ticket self-core/961).
     */
    private Issue issue;

    /**
     * Ctor.
     * @param project Project where the event happened.
     * @param type Type.
     * @param payload Payload.
     */
    GitlabWebhookEvent(
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
        if("Issue Hook".equalsIgnoreCase(this.type)
            || "Merge Request Hook".equalsIgnoreCase(this.type)) {
            final String action = this.event.getJsonObject(
                "object_attributes"
            ).getString("action", "");
            if("open".equalsIgnoreCase(action)) {
                resolved = Type.NEW_ISSUE;
            } else if ("reopen".equalsIgnoreCase(action)) {
                resolved = Type.REOPENED_ISSUE;
            } else if ("update".equalsIgnoreCase(action)){
                final boolean labelsChanged = this.labelsChanged();
                if (labelsChanged) {
                    resolved = Type.LABEL;
                } else {
                    resolved = this.type;
                }
            } else {
                resolved = this.type;
            }
        } else if("Note Hook".equalsIgnoreCase(this.type)) {
            final String noteableType = this.event.getJsonObject(
                "object_attributes"
            ).getString("noteable_type", "");
            if("Issue".equalsIgnoreCase(noteableType)
                || "MergeRequest".equalsIgnoreCase(noteableType)) {
                resolved = Type.ISSUE_COMMENT;
            } else {
                resolved = this.type;
            }
        } else {
            resolved = this.type;
        }
        return resolved;
    }

    @Override
    public Issue issue() {
        if(this.issue == null) {
            final String iid;
            boolean mergeRequest = false;
            if ("Issue Hook".equalsIgnoreCase(this.type)
                || "Merge Request Hook".equalsIgnoreCase(this.type)) {
                if ("Merge Request Hook".equalsIgnoreCase(this.type)) {
                    mergeRequest = true;
                }
                iid = String.valueOf(
                    this.event.getJsonObject("object_attributes")
                        .getInt("iid")
                );
            } else if ("Note Hook".equalsIgnoreCase(this.type)) {
                final String noteableType = this.event.getJsonObject(
                    "object_attributes"
                ).getString("noteable_type", "");
                if ("Issue".equalsIgnoreCase(noteableType)) {
                    iid = String.valueOf(
                        this.event.getJsonObject("issue")
                            .getInt("iid")
                    );
                } else if ("MergeRequest".equalsIgnoreCase(noteableType)) {
                    iid = String.valueOf(
                        this.event.getJsonObject("merge_request")
                            .getInt("iid")
                    );
                    mergeRequest = true;
                } else {
                    iid = null;
                }
            } else {
                iid = null;
            }
            if (iid != null) {
                final String repoFullName = this.project.repoFullName();
                if (mergeRequest) {
                    this.issue = this.project.projectManager().provider().repo(
                        repoFullName.split("/")[0],
                        repoFullName.split("/")[1]
                    ).pullRequests().getById(iid);
                } else {
                    this.issue = this.project.projectManager().provider().repo(
                        repoFullName.split("/")[0],
                        repoFullName.split("/")[1]
                    ).issues().getById(iid);
                }
            } else {
                this.issue = null;
            }
        }
        return this.issue;
    }

    @Override
    public Comment comment() {
        final JsonObject jsonComment;
        if("Note Hook".equalsIgnoreCase(this.type)) {
            final JsonObject attributes = this.event.getJsonObject(
                "object_attributes"
            );
            final String noteableType = attributes.getString(
                "noteable_type", ""
            );
            if ("Issue".equalsIgnoreCase(noteableType)
                || "MergeRequest".equalsIgnoreCase(noteableType)) {
                jsonComment = Json.createObjectBuilder()
                    .add("id", attributes.getInt("id"))
                    .add("body", attributes.getString("note"))
                    .add(
                        "author",
                        Json.createObjectBuilder()
                            .add(
                                "username",
                                this.event.getJsonObject("user")
                                    .getString("username")
                            )
                    ).build();
            } else {
                jsonComment = null;
            }
        } else {
            jsonComment = null;
        }
        final Comment comment;
        if(jsonComment != null) {
            comment = this.issue().comments().received(jsonComment);
        } else {
            comment = null;
        }
        return comment;
    }

    @Override
    public Commit commit() {
        final Commit commit;
        if("Push Hook".equalsIgnoreCase(this.type)) {
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
    public String repoNewName() {
        throw new UnsupportedOperationException(
            "GitLab doesn't support Webhook for REPO_RENAMED!"
        );
    }

    @Override
    public Project project() {
        return this.project;
    }

    /**
     * Checks if current issue payload has info about changing
     * labels. (adding, changing, removing).
     * @return Boolean.
     */
    private boolean labelsChanged(){
        final JsonObject changes = this.event.getJsonObject("changes");
        boolean hasChanged = false;
        if (changes != null && changes.containsKey("labels")) {
            final JsonArray previous = changes
                .getJsonObject("labels")
                .getOrDefault("previous", JsonArray.EMPTY_JSON_ARRAY)
                .asJsonArray();
            final JsonArray current = changes
                .getJsonObject("labels")
                .getOrDefault("current", JsonArray.EMPTY_JSON_ARRAY)
                .asJsonArray();
            hasChanged = !previous.isEmpty() || !current.isEmpty();
        }
        return hasChanged;
    }
}