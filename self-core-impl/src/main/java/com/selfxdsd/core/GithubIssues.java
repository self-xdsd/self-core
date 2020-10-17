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

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.storage.Labels;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Issues in a Github repository.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class GithubIssues implements Issues {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubIssues.class
    );

    /**
     * Github repo Issues base uri.
     */
    private final URI issuesUri;

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
     * @param issuesUri Issues base URI.
     * @param storage Storage.
     */
    GithubIssues(
        final JsonResources resources,
        final URI issuesUri,
        final Storage storage
    ) {
        this.resources = resources;
        this.issuesUri = issuesUri;
        this.storage = storage;
    }

    @Override
    public Issue getById(final String issueId) {
        final URI issueUri = URI.create(
            this.issuesUri.toString() + "/" + issueId
        );
        final Resource resource = this.resources.get(issueUri);
        JsonObject jsonObject;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_OK:
                jsonObject = resource.asJsonObject();
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
            case HttpURLConnection.HTTP_NO_CONTENT:
                jsonObject = null;
                break;
            default:
                throw new IllegalStateException(
                    "Could not get the issue " + issueId + ". "
                  + "Received status code: " + resource.statusCode()
                );
        }
        Issue issue = null;
        if(jsonObject != null){
            issue = new GithubIssue(
                issueUri,
                jsonObject,
                this.storage,
                this.resources
            );
        }
        return issue;
    }

    @Override
    public Issue received(final JsonObject issue) {
        return new GithubIssue(
            URI.create(
                this.issuesUri.toString() + "/" + issue.getInt("number")
            ),
            issue,
            this.storage,
            this.resources
        );
    }

    @Override
    public Issue open(
        final String title,
        final String body,
        final String... labels
    ) {
        final JsonArrayBuilder labelsArray = Json.createArrayBuilder();
        for(final String label : labels) {
            labelsArray.add(label);
        }
        final Resource resource = this.resources.post(
            this.issuesUri,
            Json.createObjectBuilder()
                .add("title", title)
                .add("body", body)
                .add("labels", labelsArray.build())
                .build()
        );
        JsonObject jsonObject;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_CREATED:
            case HttpURLConnection.HTTP_OK:
                jsonObject = resource.asJsonObject();
                break;
            default:
                throw new IllegalStateException(
                    "Could not create Issue at [" + this.issuesUri + "]. "
                    + "Expected status 201 CREATED or 200 OK, but received "
                    + "status code: " + resource.statusCode()
                );
        }
        Issue issue = null;
        if(jsonObject != null){
            issue = new GithubIssue(
                URI.create(
                    this.issuesUri.toString() + "/"
                    + jsonObject.getInt("number")
                ),
                jsonObject,
                this.storage,
                this.resources
            );
        }
        return issue;
    }

    @Override
    public Issues search(final String text, final String... labels) {
        final String[] uriParts = this.issuesUri.getRawPath().split("/");
        final String repoFullName = uriParts[2] + "/" + uriParts[3];

        String query;
        if(text != null && !text.trim().isEmpty()) {
            query = "q=" + text + "+repo:" + repoFullName;
        } else {
            query = "q=repo:" + repoFullName;
        }

        for(final String label : labels) {
            query = query + "+label:" + label;
        }

        final URI search = URI
            .create(
                "https://api.github.com/search/issues?" + query
                + "&sort=created&order=desc&per_page=100"
            );

        LOG.debug("Searching for Github Issues at: " + search);
        final Resource resource = this.resources.get(search);

        JsonArray results;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_OK:
                LOG.debug("Search returned status 200 OK.");
                results = resource.asJsonObject().getJsonArray("items");
                break;
            default:
                LOG.error(
                    "Search returned status: " + resource.statusCode() + ". "
                    + "Was expecting 200 OK! Returning 0 found issues..."
                );
                results = Json.createArrayBuilder().build();
                break;
        }

        final List<Issue> found = new ArrayList<>();
        for(final JsonValue issue : results) {
            found.add(
                this.received((JsonObject) issue)
            );
        }
        return new FoundIssues(this, found);
    }

    @Override
    public Labels labels() {
        final URI labelsUri = URI.create(this.issuesUri.toString() + "/labels");
        return new GithubIssueLabels(labelsUri, this.resources);
    }

    @Override
    public Iterator<Issue> iterator() {
        throw new IllegalStateException(
            "You cannot iterate over all the issues in a repo."
        );
    }

}
