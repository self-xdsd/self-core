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

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON Storage for cached JsonResources.
 * @author criske
 * @version $Id$
 * @since 0.0.79
 */
public interface JsonStorage {

    /**
     * Return the ETag by URI.
     * @param uri URI.
     * @return ETag or null if not found.
     */
    String getEtag(final URI uri);

    /**
     * Get a cached resource body in JSON format by URI.
     * @param uri URI.
     * @return JsonValue or null if not found.
     */
    String getResourceBody(final URI uri);

    /**
     * Stores a resource from URI along with its etag in json format.
     * @param uri URI.
     * @param etag ETag.
     * @param resource Resource in JSON.
     */
    void store(final URI uri, final String etag, final String resource);

    /**
     * In memory JsonStorage.
     */
    final class InMemory implements JsonStorage {

        /**
         * Storage map.
         */
        private final Map<URI, Value> storage = new ConcurrentHashMap<>();

        @Override
        public String getEtag(final URI uri) {
            final Value value = this.storage.get(uri);
            String etag = null;
            if (value != null) {
                etag = value.etag;
            }
            return etag;
        }

        @Override
        public String getResourceBody(final URI uri) {
            final Value value = this.storage.get(uri);
            String resource = null;
            if (value != null) {
                resource = value.resource;
            }
            return resource;
        }

        @Override
        public void store(
            final URI uri,
            final String etag,
            final String resource
        ) {
            this.storage.put(uri, new Value(etag, resource));
        }

        /**
         * JsonStorage value wrapper.
         */
        private static final class Value {
            /**
             * Etag.
             */
            private final String etag;
            /**
             * Resource as JSON.
             */
            private final String resource;

            /**
             * Ctor.
             * @param etag Etag.
             * @param resource Resource as JSON.
             */
            private Value(final String etag, final String resource) {
                this.etag = etag;
                this.resource = resource;
            }

        }

    }
}