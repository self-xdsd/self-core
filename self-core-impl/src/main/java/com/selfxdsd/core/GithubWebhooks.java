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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.Webhook;
import com.selfxdsd.api.Webhooks;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Github repo webhooks.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
final class GithubWebhooks implements Webhooks {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubWebhooks.class
    );

    /**
     * Github repo Webhooks base uri.
     */
    private final URI hooksUri;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Github's JSON Resources.
     * @param hooksUri Hooks base URI.
     * @param storage Storage.
     */
    GithubWebhooks(
        final JsonResources resources,
        final URI hooksUri,
        final Storage storage
    ) {
        this.resources = resources;
        this.hooksUri = hooksUri;
        this.storage = storage;
    }

    @Override
    public boolean add(final Project project) {
        LOG.debug(
            "Adding Github webhook for Project " + project.repoFullName()
        );
        final boolean added;
        final Resource response = this.resources.post(
            this.hooksUri,
            Json.createObjectBuilder()
                .add(
                    "events",
                    Json.createArrayBuilder()
                        .add("issues")
                        .add("issue_comment")
                        .add("pull_request")
                        .add("push")
                )
                .add(
                    "config",
                    Json.createObjectBuilder()
                        .add(
                            "url",
                            System.getenv(Env.WEBHOOK_BASE_URL) + "/github/"
                            + project.repoFullName()
                        )
                        .add("content_type", "json")
                        .add("secret", project.webHookToken())
                ).build()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_CREATED) {
            added = true;
            LOG.debug("Webhook added successfully!");
        } else {
            added = false;
            LOG.debug(
                "Problem when adding webhook. Expected 201 CREATED, "
                + " but got " + response.statusCode()
            );
        }
        return added;
    }

    @Override
    public boolean remove() {
        boolean removed = true;
        for(final Webhook hook : this) {
            if(hook.url().contains("//self-xdsd.")) {
                LOG.debug(
                    "Removing Self XDSD Webhook from ["
                    + this.hooksUri + "]..."
                );
                final Resource response = this.resources
                    .delete(
                        URI.create(
                            this.hooksUri.toString() + "/" + hook.id()
                        ),
                        Json.createObjectBuilder().build()
                    );
                final int status = response.statusCode();
                if(status == HttpURLConnection.HTTP_NO_CONTENT) {
                    LOG.debug("Hook removed successfully!");
                } else {
                    LOG.debug(
                        "Problem while removing webhook. "
                        + "Expected 204 NO CONTENT, but got " + status + "."
                    );
                    removed = removed && Boolean.FALSE;
                }
            }
        }
        return removed;
    }

    @Override
    public Iterator<Webhook> iterator() {
        final Iterator<Webhook> iterator;
        LOG.debug(
            "Fetching Github webhooks [" + this.hooksUri + "]..."
        );
        final Resource response = this.resources.get(
            URI.create(this.hooksUri.toString() + "?per_page=100")
        );
        if(response.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug("Webhooks fetched successfully!");
            final List<Webhook> list = new ArrayList<>();
            for(final JsonValue hook : response.asJsonArray()) {
                list.add(
                    new Webhook() {
                        /**
                         * Hook in JSON.
                         */
                        private final JsonObject json = (JsonObject) hook;

                        @Override
                        public String id() {
                            return String.valueOf(this.json.getInt("id"));
                        }

                        @Override
                        public String url() {
                            return this.json.getJsonObject("config")
                                .getString("url");
                        }
                    }
                );
            }
            iterator = list.iterator();
        } else {
            LOG.error(
                "Problem when fetching webhooks. Expected 200 OK, "
                + " but got " + response.statusCode()
                + ". Returning empty iterable."
            );
            iterator = new ArrayList<Webhook>().iterator();
        }
        return iterator;
    }
}
