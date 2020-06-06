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
package com.selfxdsd.core.issues;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.storage.Storage;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;

/**
 * Issues in a Github repository.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubIssues implements Issues {

    /**
     * Github repo Issues base uri.
     */
    private URI issuesUri;

    /**
     * Self storage, in case we want to store something.
     */
    private Storage storage;

    /**
     * Ctor.
     *
     * @param issuesUri Issues base URI.
     * @param storage Storage.
     */
    public GithubIssues(final URI issuesUri, final Storage storage) {
        this.issuesUri = issuesUri;
        this.storage = storage;
    }

    @Override
    public Issue getById(final String issueId) {
        final URI issueUri = URI.create(issuesUri.toString() + "/" + issueId);
        JsonObject jsonObject = fromUri(issueUri, issueId);
        Issue issue = null;
        if(jsonObject != null){
            issue = new GithubIssue(issueUri, jsonObject, storage);
        }
        return issue;
    }

    @Override
    public Iterator<Issue> iterator() {
        throw new IllegalStateException(
            "You cannot iterate over all the issues in a repo."
        );
    }

    /**
     * Fetches a json object over the network.
     *
     * @param uri Provided URI.
     * @param issueId Issue id.
     * @return JsonObject or null if result status is 400 or 204
     * @throws IllegalStateException when something went wrong.
     */
    private JsonObject fromUri(final URI uri, final String issueId) {
        JsonObject jsonObject;
        try {
            final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
            final int status = response.statusCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    jsonObject = Json.createReader(
                        new StringReader(response.body())
                    ).readObject();
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                case HttpURLConnection.HTTP_NO_CONTENT:
                    jsonObject = null;
                    break;
                default:
                    throw new IllegalStateException("Could not get the issue "
                        + issueId);
            }
        } catch (final IOException | InterruptedException exception) {
            throw new IllegalStateException("Could not get the issue "
                + issueId);
        }

        return jsonObject;
    }
}
