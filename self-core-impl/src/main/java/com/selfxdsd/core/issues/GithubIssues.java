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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Issues in a Github repository.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @todo #98 Extract the utility class `GithubIssues.JsonFetcher` into its
 *  own file and then refactor `BaseRepo#json()` to use it.
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
     * Temporary cache until {@link Storage} has that feature.
     */
    private final Map<Integer, Issue> cachedIssues = new HashMap<>();

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
    public Issue getById(final int issueId) {
        Issue issue = cachedIssues.get(issueId);
        if (issue == null) {
            JsonObject jsonObject;
            final URI issueUri = URI.create(issuesUri.toString() + "/"
                + issueId);
            try {
                jsonObject = JsonFetcher.fromUri(issueUri);
            } catch (final IllegalStateException
                | IOException
                | InterruptedException ex) {
                jsonObject = null;
            }
            if (jsonObject != null) {
                issue = new GithubIssue(issueUri, jsonObject, storage);
                cachedIssues.put(issueId, issue);
            }
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
     * Utility class used for fetching json objects over the network.
     * @author criske
     * @version $Id$
     * @since 0.0.1
     */
    private static final class JsonFetcher {

        /**
         * Private constructor.
         */
        private JsonFetcher() {
            throw new UnsupportedOperationException();
        }

        /**
         * Fetches a json object over the network.
         *
         * @param uri Provided URI.
         * @return JsonObject
         * @throws IllegalStateException when the resource was not found.
         * @throws IOException when there are network issues.
         * @throws InterruptedException  when call was interrupted.
         */
        public static JsonObject fromUri(final URI uri)
            throws IllegalStateException, IOException, InterruptedException {
            final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
            final int status = response.statusCode();
            if (status == HttpURLConnection.HTTP_OK) {
                return Json.createReader(
                    new StringReader(response.body())
                ).readObject();
            } else {
                throw new IllegalStateException(
                    "Unexpected response when fetching [" + uri + "]. "
                        + "Expected 200 OK, but got " + status + "."
                );
            }
        }
    }
}
