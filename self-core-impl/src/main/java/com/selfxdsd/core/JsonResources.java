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

import com.selfxdsd.api.Resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * JSON Resources used by the Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public interface JsonResources {

    /**
     * Return an instance which has an accessToken for
     * making authenticated requests.
     * @param accessToken Access token.
     * @return JsonResources.
     */
    JsonResources authenticated(final AccessToken accessToken);

    /**
     * Get the Resource at the specified URI.
     * @param uri Resource location.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource get(final URI uri){
        return this.get(uri, Collections::emptyMap);
    }

    /**
     * Get the Resource at the specified URI.
     * @param uri Resource location.
     * @param headers HTTP Headers.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource get(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers
    );

    /**
     * Post a JsonObject to the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource post(
        final URI uri,
        final JsonValue body
    ){
        return this.post(uri, Collections::emptyMap, body);
    }

    /**
     * Post a JsonObject to the specified URI.
     * @param uri URI.
     * @param headers HTTP Headers.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource post(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    );


    /**
     * Patch a JsonObject at the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource patch(
        final URI uri,
        final JsonValue body
    ){
        return this.patch(uri, Collections::emptyMap, body);
    }

    /**
     * Patch a JsonObject at the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @param headers HTTP Headers.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource patch(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    );


    /**
     * Put a JsonObject at the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource put(
        final URI uri,
        final JsonValue body
    ){
        return this.put(uri, Collections::emptyMap, body);
    }

    /**
     * Put a JsonObject at the specified URI.
     * @param uri URI.
     * @param body JSON body of the request.
     * @param headers HTTP Headers.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource put(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    );

    /**
     * DELETE the specified resource.
     * @param uri URI.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    default Resource delete(
        final URI uri,
        final JsonValue body
    ){
        return this.delete(uri, Collections::emptyMap, body);
    }

    /**
     * DELETE the specified resource.
     * @param uri URI.
     * @param headers HTTP Headers.
     * @param body JSON body of the request.
     * @return Resource.
     * @throws IllegalStateException If IOException or InterruptedException
     *  occur while making the HTTP request.
     */
    Resource delete(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    );

    /**
     * JSON Resources obtained by making HTTP calls, using
     * the JDK.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.8
     */
    final class JdkHttp implements JsonResources {

        /**
         * Access token.
         */
        private final AccessToken accessToken;

        /**
         * Instructs http client to use {@link HttpClient.Version#HTTP_1_1}.
         * Use this flag if the integration test server doesn't support
         * HTTP_2.
         */
        private final boolean useOldHttpProtocol;

        /**
         * Ctor.
         */
        JdkHttp() {
            this(null, false);
        }

        /**
         * Ctor.
         * @param useOldHttpProtocol Instructs http client to use
         * {@link HttpClient.Version#HTTP_1_1}. Use this flag if
         * integration test server doesn't support HTTP_2.
         */
        JdkHttp(final boolean useOldHttpProtocol) {
            this(null, useOldHttpProtocol);
        }

        /**
         * Ctor.
         * @param accessToken Access token for authenticated requests.
         * @param useOldHttpProtocol Instructs http client to use
         * {@link HttpClient.Version#HTTP_1_1}. Use this flag if
         * integration test server doesn't support HTTP_2.
         */
        private JdkHttp(
            final AccessToken accessToken,
            final boolean useOldHttpProtocol
        ) {
            this.accessToken = accessToken;
            this.useOldHttpProtocol= useOldHttpProtocol;
        }

        @Override
        public JsonResources authenticated(final AccessToken accessToken) {
            return new JsonResources.JdkHttp(
                accessToken,
                this.useOldHttpProtocol
            );
        }

        @Override
        public Resource get(
            final URI uri,
            final Supplier<Map<String, List<String>>> headers
        ) {
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            HttpClient client = this.newHttpClient(exec);
            try {
                final HttpResponse<String> response = client.send(
                    this.request(
                        uri,
                        "GET",
                        headers.get(),
                        HttpRequest.BodyPublishers.noBody()
                    ),
                    HttpResponse.BodyHandlers.ofString()
                );
                return new JsonResponse(
                    response.statusCode(),
                    response.body(),
                    this.headers(response.headers())
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't GET [" + uri.toString() +"]",
                    ex
                );
            } finally {
                exec.shutdownNow();
                client = null;
                System.gc();
            }
        }

        @Override
        public Resource post(
            final URI uri,
            final Supplier<Map<String, List<String>>> headers,
            final JsonValue body
        ) {
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            HttpClient client = this.newHttpClient(exec);
            try {
                final HttpResponse<String> response = client.send(
                    this.request(
                        uri,
                        "POST",
                        headers.get(),
                        HttpRequest.BodyPublishers.ofString(
                            body.toString()
                        )
                    ),
                    HttpResponse.BodyHandlers.ofString()
                );
                return new JsonResponse(
                    response.statusCode(),
                    response.body(),
                    this.headers(response.headers())
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't POST " + body.toString()
                    + " to [" + uri.toString() +"]",
                    ex
                );
            } finally {
                exec.shutdownNow();
                client = null;
                System.gc();
            }
        }

        @Override
        public Resource patch(
            final URI uri,
            final Supplier<Map<String, List<String>>> headers,
            final JsonValue body
        ) {
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            HttpClient client = this.newHttpClient(exec);
            try {
                final HttpResponse<String> response = client.send(
                    this.request(
                        uri,
                        "PATCH",
                        headers.get(),
                        HttpRequest.BodyPublishers.ofString(
                            body.toString()
                        )
                    ),
                    HttpResponse.BodyHandlers.ofString()
                );
                return new JsonResponse(
                    response.statusCode(),
                    response.body(),
                    this.headers(response.headers())
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't PATCH " + body.toString()
                  + " at [" + uri.toString() +"]",
                    ex
                );
            } finally {
                exec.shutdownNow();
                client = null;
                System.gc();
            }
        }

        @Override
        public Resource put(
            final URI uri,
            final Supplier<Map<String, List<String>>> headers,
            final JsonValue body
        ) {
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            HttpClient client = this.newHttpClient(exec);
            try {
                final HttpResponse<String> response = client.send(
                    this.request(
                        uri,
                        "PUT",
                        headers.get(),
                        HttpRequest.BodyPublishers.ofString(
                            body.toString()
                        )
                    ),
                    HttpResponse.BodyHandlers.ofString()
                );
                return new JsonResponse(
                    response.statusCode(),
                    response.body(),
                    this.headers(response.headers())
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't PUT " + body.toString()
                  + " at [" + uri.toString() +"]",
                    ex
                );
            } finally {
                exec.shutdownNow();
                client = null;
                System.gc();
            }
        }

        @Override
        public Resource delete(
            final URI uri,
            final Supplier<Map<String, List<String>>> headers,
            final JsonValue body
        ) {
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            HttpClient client = this.newHttpClient(exec);
            try {
                final HttpResponse<String> response = client.send(
                    this.request(
                        uri,
                        "DELETE",
                        headers.get(),
                        HttpRequest.BodyPublishers.ofString(
                            body.toString()
                        )
                    ),
                    HttpResponse.BodyHandlers.ofString()
                );
                return new JsonResponse(
                    response.statusCode(),
                    response.body(),
                    this.headers(response.headers())
                );
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't DELETE " + body.toString()
                 + " at [" + uri.toString() +"]",
                    ex
                );
            } finally {
                exec.shutdownNow();
                client = null;
                System.gc();
            }
        }

        /**
         * Build and return the HTTP Request.
         * @param uri URI.
         * @param method Method.
         * @param headers HTTP Headers.
         * @param body Body.
         * @return HttpRequest.
         * @checkstyle LineLength (100 lines)
         */
        private HttpRequest request(
            final URI uri,
            final String method,
            final Map<String, List<String>> headers,
            final HttpRequest.BodyPublisher body
        ) {
            HttpRequest.Builder requestBuilder;
            if(this.accessToken != null) {
                requestBuilder = HttpRequest.newBuilder()
                    .uri(uri)
                    .method(method, body)
                    .header("Content-Type", "application/json")
                    .header(
                        this.accessToken.header(),
                        this.accessToken.value()
                    );
            } else {
                requestBuilder = HttpRequest.newBuilder()
                    .uri(uri)
                    .method(method, body)
                    .header("Content-Type", "application/json");
            }
            for(final Map.Entry<String, List<String>> header : headers.entrySet()) {
                requestBuilder = requestBuilder.header(
                    header.getKey(),
                    String.join(",", header.getValue())
                );
            }
            return requestBuilder.build();
        }

        /**
         * Creates a new http client.
         * @param executor ExecutorService to use internally and close on end.
         * @return HttpClient.
         */
        private HttpClient newHttpClient(final ExecutorService executor) {
            final HttpClient client;
            if (this.useOldHttpProtocol) {
                client = HttpClient
                    .newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10))
                    .executor(executor)
                    .build();
            } else {
                client = HttpClient
                    .newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10))
                    .executor(executor)
                    .build();
            }
            return client;
        }

        /**
         * Calling HttpHeaders.map() will return a Map<String, List<String>>,
         * BUT it will not split the header's value by comma. We have to do
         * that manually.
         * @param headers Headers map with values list split by comma.
         * @return Map.
         * @checkstyle LineLength (10 lines)
         * @see <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpHeaders.html">HttpHeaders.map() JavaDoc.</a>
         */
        private Map<String, List<String>> headers(final HttpHeaders headers) {
            final Map<String, List<String>> split = new HashMap<>();
            final Map<String, List<String>> original = headers.map();
            for(final Map.Entry<String, List<String>> header : original.entrySet()) {
                final List<String> splitList = new ArrayList<>();
                for(final String value : header.getValue()) {
                    Arrays.asList(value.split(",")).stream().forEach(
                        v -> splitList.add(v.trim())
                    );
                }
                split.put(header.getKey(), splitList);
            }
            return split;
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
         * Response headers.
         */
        private final Map<String, List<String>> headers;

        /**
         * Ctor.
         * @param statusCode Status code.
         * @param body Response Body.
         * @param headers Response Headers.
         */
        JsonResponse(final int statusCode,
                     final String body,
                     final Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
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

        @Override
        public String body() {
            return this.body;
        }

        @Override
        public Map<String, List<String>> headers() {
            return this.headers;
        }

        @Override
        public String toString() {
            return this.body;
        }
    }
}
