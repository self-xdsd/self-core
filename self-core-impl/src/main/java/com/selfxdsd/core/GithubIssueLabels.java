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

import com.selfxdsd.api.Label;
import com.selfxdsd.api.Labels;
import com.selfxdsd.api.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Github Issue Labels.
 * @author criske
 * @version $Id$
 * @since 0.0.30
 */
final class GithubIssueLabels implements Labels {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubIssueLabels.class
    );

    /**
     * Issue Labels URI.
     */
    private final URI uri;

    /**
     * Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param uri Issue Labels URI.
     * @param resources Resources.
     */
    GithubIssueLabels(final URI uri, final JsonResources resources) {
        this.resources = resources;
        this.uri = uri;
    }

    @Override
    public boolean add(final String... names) {
        final GithubRepoLabels repoLabels = new GithubRepoLabels(
            URI.create(
                this.uri.toString()
                    .replaceAll("/issues/[0-9]+", "")
            ),
            this.resources
        );
        repoLabels.add(names);
        final JsonArrayBuilder labels = Json.createArrayBuilder();
        for (final String name : names) {
            labels.add(name);
        }
        final Resource resource = this.resources.post(this.uri, Json
            .createObjectBuilder()
            .add("labels", labels.build())
            .build()
        );
        return resource.statusCode() == HttpURLConnection.HTTP_OK;
    }

    @Override
    public boolean remove(final String name) {
        final URI labelUri = URI.create(this.uri.toString() + "/" + name);
        LOG.debug("Removing Issue Label [" + labelUri + "]...");
        final Resource resource = this.resources.delete(
            labelUri,
            Json.createObjectBuilder().build()
        );
        final int status = resource.statusCode();
        final boolean result;
        if(status == HttpURLConnection.HTTP_OK) {
            result = true;
            LOG.debug("Label Issue removed successfully (200 OK).");
        } else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
            result = true;
            LOG.warn("Label Issue NOT FOUND (still valid).");
        } else {
            result = false;
            LOG.error(
                "Unexpected response. Expected 200 or 404, but got: " + status
            );
        }
        return result;
    }

    @Override
    public Iterator<Label> iterator() {
        final Resource resource = this.resources.get(this.uri);
        final List<Label> labels;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            labels = resource.asJsonArray()
                .stream()
                .map(JsonObject.class::cast)
                .map(GithubLabel::new)
                .collect(Collectors.toList());
        } else {
            labels = List.of();
        }
        return labels.iterator();
    }
}
