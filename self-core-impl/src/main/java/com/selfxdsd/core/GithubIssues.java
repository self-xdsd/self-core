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

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
     * Parent Repo.
     */
    private final Repo repo;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Github's JSON Resources.
     * @param issuesUri Issues base URI.
     * @param repo Parent Repo.
     * @param storage Storage.
     */
    GithubIssues(
        final JsonResources resources,
        final URI issuesUri,
        final Repo repo,
        final Storage storage
    ) {
        this.resources = resources;
        this.issuesUri = issuesUri;
        this.repo = repo;
        this.storage = storage;
    }

    @Override
    public Issue getById(final String issueId) {
        return new WithContributorLabel(
            new GithubIssue(
                URI.create(
                    this.issuesUri.toString() + "/" + issueId
                ),
                this.storage,
                this.resources
            )
        );
    }

    @Override
    public Issue received(final JsonObject issue) {
        return new WithContributorLabel(
            new GithubIssue(
                URI.create(
                    this.issuesUri.toString() + "/" + issue.getInt("number")
                ),
                () -> issue,
                this.storage,
                this.resources
            )
        );
    }

    /**
     * {@inheritDoc}
     * <br>
     * When opening an Issue, Github will create the specified labels if they
     * do not exist. However, they will all be grey.<br><br>
     *
     * Since we cannot specify the labels' color when opening an Issue,
     * we first add them to the repository (where we can specify the color).
     */
    @Override
    public Issue open(
        final String title,
        final String body,
        final String... labels
    ) {
        this.repo.labels().add(labels);
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
            issue = new WithContributorLabel(
                new GithubIssue(
                    URI.create(
                        this.issuesUri.toString() + "/"
                        + jsonObject.getInt("number")
                    ),
                    () -> jsonObject,
                    this.storage,
                    this.resources
                )
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
            query = "q=" + URLEncoder.encode(text,  StandardCharsets.UTF_8)
                + "+repo:" + repoFullName;
        } else {
            query = "q=repo:" + repoFullName;
        }

        for(final String label : labels) {
            query = query + "+label:" + URLEncoder
                .encode(label, StandardCharsets.UTF_8);
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
    public Iterator<Issue> iterator() {
        throw new IllegalStateException(
            "You cannot iterate over all the issues in a repo."
        );
    }

}
