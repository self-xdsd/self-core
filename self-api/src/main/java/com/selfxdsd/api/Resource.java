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
package com.selfxdsd.api;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Resource returned by the Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public interface Resource {

    /**
     * Status code.
     * @return Integer.
     */
    int statusCode();

    /**
     * This resource as JsonObject.
     * @return JsonObject.
     */
    JsonObject asJsonObject();

    /**
     * This resource as JsonArray.
     * @return JsonArray.
     */
    JsonArray asJsonArray();

    /**
     * Returns the resource as simple String.
     * @return String.
     */
    String body();

    /**
     * Resource headers.
     * @return Map of headers.
     */
    Map<String, List<String>> headers();

    /**
     * Returns this resource's etag() if it exists.
     * @return String or null.
     */
    default String etag() {
        return this.headers()
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey()
                .equalsIgnoreCase("ETag"))
            .flatMap(entry -> entry.getValue().stream())
            .findFirst()
            .orElse(null);
    }

    /**
     * Abstract builder for a new Resource based on this resource.
     * @return Builder.
     */
    Builder newBuilder();

    /**
     * Abstract builder for a Resource.
     */
    class Builder {

        /**
         * Status code.
         */
        private int statusCode;

        /**
         * Body.
         */
        private JsonValue body;

        /**
         * Headers.
         */
        private Map<String, List<String>> headers;

        /**
         * Abstract factory.
         */
        private final Factory factory;

        /**
         * Ctor.
         * @param source Original Resource.
         * @param factory Resource factory.
         */
        public Builder(final Resource source, final Factory factory) {
            this.statusCode = source.statusCode();
            final String sourceBody = source.toString();
            if (sourceBody == null || sourceBody.isBlank()
                || "null".equalsIgnoreCase(sourceBody)) {
                this.body = JsonValue.NULL;
            } else {
                this.body = Json.createReader(
                    new StringReader(sourceBody)
                ).read();
            }
            this.headers = source.headers();
            this.factory = factory;
        }

        /**
         * Set new status code.
         * @param statusCode Status code.
         * @return Builder.
         */
        public Builder status(final int statusCode){
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Set new body as json object or array.
         * @param body Body.
         * @return Builder.
         */
        public Builder body(final JsonStructure body){
            this.body = body;
            return this;
        }

        /**
         * Set new body from a string json object or array.
         * @param body Body.
         * @return Builder.
         */
        public Builder body(final String body){
            return this.body(Json.createReader(new StringReader(body)).read());
        }

        /**
         * Set new headers possibly based on previous headers.
         * @param headers Headers.
         * @return Builder.
         */
        public Builder headers(
            final UnaryOperator<Map<String, List<String>>> headers
        ){
            this.headers = headers.apply(this.headers);
            return this;
        }

        /**
         * Creates a new Resource.
         * @return Resource.
         */
        public Resource build() {
            return this.factory
                .create(this.statusCode, this.body, this.headers);
        }

        /**
         * Abstract factory for Builder's Resource.
         */
        @FunctionalInterface
        public interface Factory {

            /**
             * Creates a new Resource.
             * @param statusCode Status code.
             * @param body Body.
             * @param headers Headers.
             * @return Resource.
             */
            Resource create(
                final int statusCode,
                final JsonValue body,
                final Map<String, List<String>> headers
            );

        }
    }
}
