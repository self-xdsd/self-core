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

import com.selfxdsd.api.Label;
import com.selfxdsd.api.Labels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Labels on a Gitlab Issue.<br><br>
 *
 * This class is implemented based on the Issue json.
 * Add/Remove label are actually calls to the Edit Issue endpoint.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 * @todo #723:30min Similarly to how GithubIssueLabels.add(...) is implemented,
 *  we should modify the add(...) method here to first add the labels to the
 *  repository, so they are colored differently. At the moment, they will
 *  automatically be created by Gitlab at Repo level, but they will always be
 *  blue.
 */
final class GitlabIssueLabels implements Labels {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabIssueLabels.class
    );

    /**
     * Issue URI.
     */
    private final URI uri;

    /**
     * Resources.
     */
    private final JsonResources resources;

    /**
     * The Issue in json.
     */
    private final JsonObject issue;

    /**
     * Ctor.
     * @param uri Issue URI.
     * @param resources Json resources.
     * @param issue Issue in json.
     */
    GitlabIssueLabels(
        final URI uri,
        final JsonResources resources,
        final JsonObject issue
    ) {
        this.uri = uri;
        this.resources = resources;
        this.issue = issue;
    }

    @Override
    public boolean add(final String... names) {
        final boolean added;
        final String labels = Arrays.stream(names)
            .collect(Collectors.joining(","));
        LOG.debug(
            "Adding labels [" + labels + "] to GitLab Issue ["
            + this.uri + "]..."
        );
        final Resource response = this.resources.put(
            this.uri,
            Json.createObjectBuilder()
                .add("add_labels", labels)
                .build()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug("Labels added successfully!");
            added = true;
        } else {
            LOG.error(
                "Problem while adding labels. "
                + "Expected 200 OK, but got " + response.statusCode()
            );
            added = false;
        }
        return added;
    }

    @Override
    public boolean remove(final String name) {
        final boolean removed;
        LOG.debug(
            "Removing label [" + name + "] from GitLab Issue ["
            + this.uri + "]..."
        );
        final Resource response = this.resources.put(
            this.uri,
            Json.createObjectBuilder()
                .add("remove_labels", name)
                .build()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug("Label removed successfully!");
            removed = true;
        } else {
            LOG.error(
                "Problem while removing labels. "
                + "Expected 200 OK, but got " + response.statusCode()
            );
            removed = false;
        }
        return removed;
    }

    @Override
    public Iterator<Label> iterator() {
        final List<Label> labels = new ArrayList<>();
        final List<JsonObject> array = this.issue.getJsonArray("labels")
            .getValuesAs(JsonObject.class);
        for(final JsonObject label : array) {
            labels.add(new GitlabLabel(label));
        }
        return labels.iterator();
    }
}
