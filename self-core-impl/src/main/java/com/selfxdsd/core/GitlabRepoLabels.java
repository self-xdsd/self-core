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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * All the Labels in a GitLab repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 */
final class GitlabRepoLabels implements Labels {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabRepoLabels.class
    );

    /**
     * Repo Labels URI.
     */
    private final URI repoLabelsUri;

    /**
     * Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param repoLabelsUri Repo Labels URI.
     * @param resources Resources.
     */
    GitlabRepoLabels(
        final URI repoLabelsUri,
        final JsonResources resources
    ) {
        this.resources = resources;
        this.repoLabelsUri = repoLabelsUri;
    }

    @Override
    public boolean add(final String... names) {
        boolean added = true;
        for(final String name : names) {
            LOG.debug(
                "Adding Label [" + name + "] to GitLab repo "
                + "["  + this.repoLabelsUri + "]... "
            );
            final Resource resource = this.resources.post(
                this.repoLabelsUri,
                Json.createObjectBuilder()
                    .add("name", name)
                    .add("color", "#" + this.randomColor())
                    .build()
            );
            final int status = resource.statusCode();
            if(status == HttpURLConnection.HTTP_CREATED) {
                LOG.debug("Label successfully added!");
            } else {
                LOG.warn(
                    "Problem while adding label. "
                    + "Expected 201 CREATED, got " + status
                );
                added = false;
            }
        }
        return added;
    }

    /**
     * Remove a label. We have to iterate through
     * all of them and get the Label's ID, which we
     * need for the actual DELETE call.
     * @param name Label to be removed.
     * @return True if everything went fine.
     */
    @Override
    public boolean remove(final String name) {
        int id = -1;
        for(final Label label : this) {
            if(label.name().equalsIgnoreCase(name)) {
                id = label.json().getInt("id");
                break;
            }
        }
        final boolean removed;
        if(id == -1) {
            removed = true;
        } else {
            LOG.debug("Removing label [" + name + "], ID is " + id);
            final Resource response = this.resources.delete(
                URI.create(this.repoLabelsUri.toString() + "/" + id),
                Json.createObjectBuilder().build()
            );
            if(response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                LOG.debug("Label successfully removed.");
                removed = true;
            } else {
                LOG.warn(
                    "Could not remove label [" + name + "]. "
                    + "Expected 204 NO CONTENT, got " + response.statusCode()
                );
                removed = false;
            }
        }
        return removed;
    }

    @Override
    public Iterator<Label> iterator() {
        final Resource resource = this.resources.get(
            URI.create(
                this.repoLabelsUri.toString() + "?per_page=100"
            )
        );
        final List<Label> labels;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            labels = resource.asJsonArray()
                .stream()
                .map(JsonObject.class::cast)
                .map(GitlabLabel::new)
                .collect(Collectors.toList());
        } else {
            labels = List.of();
        }
        return labels.iterator();
    }

    /**
     * Get a random color for the added label.
     * @return String hex code.
     */
    private String randomColor() {
        final Random random = new Random();
        final String[] hex = "0123456789abcdef".split("");
        String color = "";
        while (color.length() < 6){
            color += hex[random.nextInt(15)];
        }
        return color;
    }
}
