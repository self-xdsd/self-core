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
 * @todo #226:30min Add other methods such as post, patch etc
 *  and continue abstracting the HTTP calls away from the Provider's
 *  implementations (Issues, Comments etc). After that is done, we
 *  should add a mock implementation of JsonResources, which we will
 *  use in writing unit tests for the providers.
 * @todo #226:30min Bring in Grizzly in-memory HTTP Server (see project
 *  jcabi-github), so we can write integration tests for class JdkHttp.
 */
interface JsonResources {

    /**
     * Get the Resource at the specified URI.
     * @param uri Resource location.
     * @return Resource.
     */
    Resource get(final URI uri);

    /**
     * JSON Resources obtained by making HTTP calls, using
     * the JDK.
     */
    final class JdkHttp implements JsonResources {
        @Override
        public Resource get(final URI uri) {
            try {
                final HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                        HttpRequest.newBuilder()
                            .uri(uri)
                            .header("Content-Type", "application/json")
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    );
                return new Resource() {
                    @Override
                    public int statusCode() {
                        return response.statusCode();
                    }

                    @Override
                    public JsonObject asJsonObject() {
                        return Json.createReader(
                            new StringReader(response.body())
                        ).readObject();
                    }

                    @Override
                    public JsonArray asJsonArray() {
                        return Json.createReader(
                            new StringReader(response.body())
                        ).readArray();
                    }
                };
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't GET + [" + uri.toString() +"]",
                    ex
                );
            }
        }
    }
}
