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

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Cached {@link Resource}.
 * A Resource is considered cached if it has a eTag header in its
 * response headers.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 * @see com.selfxdsd.api.storage.JsonStorage
 */
public interface CachedResource extends Resource {

    /**
     * Uri of this resource. This should be also considered the
     * storing key.
     *
     * @return URI.
     */
    URI uri();

    /**
     * ETag of this resource.
     *
     * @return String.
     */
    String etag();

    /**
     * Creation date of the cached resource.
     *
     * @return LocalDateTime.
     */
    LocalDateTime creationDate();

    /**
     * Factory method that creates a CachedResource from a regular
     * Resource.
     * If the regular can't be cached
     * (no ETag header present in its response headers) it returns null.
     *
     * @param uri URI associated with the resource.
     * @param resource Original Resource.
     * @return CachedResource or null.
     */
    static CachedResource fromResource(
        final URI uri,
        final Resource resource
    ) {
        final CachedResource cached;
        if (resource instanceof CachedResource) {
            final CachedResource candidate = (CachedResource) resource;
            if (!candidate.uri().equals(uri)) {
                cached = new FromResource(candidate, candidate.etag(), uri);
            } else {
                cached = candidate;
            }
        } else {
            final String eTag = resource
                .headers()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                    .equalsIgnoreCase("ETag"))
                .flatMap(entry -> entry.getValue().stream())
                .findFirst()
                .orElse(null);
            if (eTag != null) {
                cached = new FromResource(resource, eTag, uri);
            } else {
                cached = null;
            }
        }
        return cached;
    }


    /**
     * Cached Resource created from a regular Resource.
     * A Resource is considered cached if it has eTag header in its
     * response headers.
     */
    class FromResource implements CachedResource {

        /**
         * Original resource.
         */
        private final Resource original;

        /**
         * ETag.
         */
        private final String eTag;

        /**
         * URI.
         */
        private final URI uri;


        /**
         * Creation date.
         */
        private final LocalDateTime creationDate;

        /**
         * Ctr.
         *
         * @param original Resource.
         * @param eTag Etag extracted from original headers.
         * @param uri URI.
         */
        private FromResource(
            final Resource original,
            final String eTag,
            final URI uri
        ) {
            this.original = original;
            this.eTag = eTag;
            this.uri = uri;
            this.creationDate = LocalDateTime.now();
        }

        @Override
        public URI uri() {
            return this.uri;
        }

        @Override
        public String etag() {
            return this.eTag;
        }

        @Override
        public LocalDateTime creationDate() {
            return this.creationDate;
        }

        @Override
        public int statusCode() {
            return this.original.statusCode();
        }

        @Override
        public JsonObject asJsonObject() {
            return this.original.asJsonObject();
        }

        @Override
        public JsonArray asJsonArray() {
            return this.original.asJsonArray();
        }

        @Override
        public Map<String, List<String>> headers() {
            return this.original.headers();
        }

        @Override
        public Builder newBuilder() {
            return new Builder(
                this,
                (code, body, headers) -> new FromResource(
                    this.original.newBuilder()
                        .status(code)
                        .body(body.toString())
                        .headers(h -> headers)
                        .build(),
                    eTag,
                    uri
                )
            );
        }

        @Override
        public String toString() {
            return this.original.toString();
        }
    }
}