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
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Issues in a Gitlab repository.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
final class GitlabIssues implements Issues {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabIssues.class
    );

    /**
     * Gitlab repo Issues base uri.
     */
    private final URI issuesUri;

    /**
     * Gitlab's JSON Resources.
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
     * @param resources Gitlab's JSON Resources.
     * @param issuesUri Issues base URI.
     * @param repo Parent repo.
     * @param storage Storage.
     */
    GitlabIssues(
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
        LOG.debug("Getting Gitlab issue with id " + issueId + "...");
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
            issue = new WithContributorLabel(
                new GitlabIssue(
                    issueUri,
                    jsonObject,
                    this.storage,
                    this.resources
                )
            );
        }
        return issue;
    }

    @Override
    public Issue received(final JsonObject issue) {
        return new WithContributorLabel(
            new GitlabIssue(
                URI.create(
                    this.issuesUri.toString() + "/" + issue.getInt("iid")
                ),
                issue,
                this.storage,
                this.resources
            )
        );
    }

    /**
     * {@inheritDoc}
     * <br>
     * When opening an Issue, Gitlab will create the specified labels if they
     * do not exist. However, they will all be blue.<br><br>
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
        final Resource resource = this.resources.post(
            this.issuesUri,
            Json.createObjectBuilder()
                .add("title", title)
                .add("description", body)
                .add(
                    "labels",
                    Arrays.stream(labels)
                        .collect(Collectors.joining(","))
                )
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
                new GitlabIssue(
                    URI.create(
                        this.issuesUri.toString() + "/"
                            + jsonObject.getInt("iid")
                    ),
                    jsonObject,
                    this.storage,
                    this.resources
                )
            );
        }
        return issue;
    }

    /**
     *{@inheritDoc}
     * <br/>
     * See following <a href="https://docs.gitlab.com/ee/api/issues.html#list-issues">
     *     documentation</a>.
     */
    @Override
    public Issues search(final String text, final String... labels) {
        final StringBuilder searchPath = new StringBuilder(
            this.issuesUri.toString()
        );
        searchPath.append("/?per_page=100");
        final boolean hasText = text != null && !text.isBlank();
        if (hasText) {
            searchPath.append("&search=");
            searchPath.append(URLEncoder.encode(text, StandardCharsets.UTF_8));
        }
        if (labels.length > 0) {
            searchPath.append("&labels=");
            searchPath.append(Arrays
                .stream(labels)
                .map(l -> URLEncoder.encode(l, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","))
            );
        }

        final URI search = URI.create(searchPath.toString());

        LOG.debug("Searching for Gitlab Issues at: " + search);
        final Resource resource = this.resources.get(search);

        JsonArray results;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_OK:
                LOG.debug("Search returned status 200 OK.");
                results = resource.asJsonArray();
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
        for (final JsonValue issue : results) {
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
