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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * JSON Resources used by the Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 * @todo #237:30min Add other methods such as delete, patch etc
 *  and continue abstracting the HTTP calls away from the Provider's
 *  implementations (Issues, Comments etc). Make sure to offer alternatives
 *  with accessToken as well. After that is done, we should add a mock
 *  implementation of JsonResources, which we will use in writing unit
 *  tests for the providers.
 */
interface JsonResources {

    /**
     * Get the Resource at the specified URI.
     * @param uri Resource location.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource get(final URI uri) {
        return this.get(uri, "");
    }

    /**
     * Get the Resource at the specified URI.
     * @param uri Resource location.
     * @param accessToken Access token for requests that
     *  require authentication.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource get(final URI uri, final String accessToken);

    /**
     * Post a JsonObject to the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @param accessToken Access token for authentication.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource post(
        final URI uri,
        final JsonObject body,
        final String accessToken
    );

    /**
     * JSON Resources obtained by making HTTP calls, using
     * the JDK.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.8
     */
    final class JdkHttp implements JsonResources {
        @Override
        public Resource get(final URI uri, final String accessToken) {
            try {
                final HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                        HttpRequest.newBuilder()
                            .uri(uri)
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .header("Content-Type", "application/json")
                            .header("Authentication", "token " + accessToken)
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    );
                return new JsonResponse(
                    response.statusCode(), response.body()
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't GET [" + uri.toString() +"]",
                    ex
                );
            }
        }

        @Override
        public Resource post(
            final URI uri,
            final JsonObject body,
            final String accessToken
        ) {
            try {
                final HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                        HttpRequest.newBuilder()
                            .uri(uri)
                            .method(
                                "POST",
                                HttpRequest.BodyPublishers.ofString(
                                    body.toString()
                                )
                            )
                            .header("Content-Type", "application/json")
                            .header("Authentication", "token " + accessToken)
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    );
                return new JsonResponse(
                    response.statusCode(), response.body()
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't POST " + body.toString()
                  + " to [" + uri.toString() +"]",
                    ex
                );
            }
        }
    }

    /**
     * Response as JSON.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.8
     */
    final class JsonResponse implements Resource {

        /**
         * Response status code.
         */
        final int statusCode;

        /**
         * Response body (expected to be a JSON).
         */
        final String body;

        /**
         * Ctor.
         * @param statusCode Status code.
         * @param body Response Body.
         */
        JsonResponse(final int statusCode, final String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return this.statusCode;
        }

        @Override
        public JsonObject asJsonObject() {
            return Json.createReader(
                new StringReader(this.body)
            ).readObject();
        }

        @Override
        public JsonArray asJsonArray() {
            return Json.createReader(
                new StringReader(this.body)
            ).readArray();
        }
    }
}
