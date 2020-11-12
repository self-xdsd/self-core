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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * All the labels in a Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.34
 */
final class GithubRepoLabels implements Labels {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubRepoLabels.class
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
    GithubRepoLabels(final URI repoLabelsUri, final JsonResources resources) {
        this.resources = resources;
        this.repoLabelsUri = repoLabelsUri;
    }

    @Override
    public boolean add(final String... names) {
        for(final String name : names) {
            this.resources.post(
                this.repoLabelsUri,
                Json.createObjectBuilder()
                    .add("name", name)
                    .add("color", this.randomColor())
                    .build()
            );
        }
        return true;
    }

    @Override
    public boolean remove(final String name) {
        final URI labelUri = URI.create(
            this.repoLabelsUri.toString() + "/" + name
        );
        LOG.debug("Removing Repo Label [" + labelUri + "]...");
        final Resource resource = this.resources.delete(
            labelUri,
            Json.createObjectBuilder().build()
        );
        final boolean result;
        final int status = resource.statusCode();
        if(status == HttpURLConnection.HTTP_NO_CONTENT
            || status == HttpURLConnection.HTTP_NOT_FOUND) {
            result = true;
            LOG.debug("Repo Label removed successfully.");
        } else {
            result = false;
            LOG.error(
                "Unexpected response. Expected 204 or 404, but got: "
                + status
            );
        }
        return result;
    }

    @Override
    public Iterator<Label> iterator() {
        final Resource resource = this.resources.get(this.repoLabelsUri);
        final List<Label> repoLabels;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            repoLabels = resource.asJsonArray()
                .stream()
                .map(JsonObject.class::cast)
                .map(GithubLabel::new)
                .collect(Collectors.toList());
        } else {
            repoLabels = List.of();
        }
        return repoLabels.iterator();
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
