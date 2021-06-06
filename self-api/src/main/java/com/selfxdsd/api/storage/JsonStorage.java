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
package com.selfxdsd.api.storage;

import com.selfxdsd.api.ConditionalResource;

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
     * Get conditional resource by its uri.
     * @param uri URI.
     * @return Resource or null if not found.
     */
    ConditionalResource getResource(final URI uri);

    /**
     * Store the resource.
     * @param resource Resource.
     * @return Stored Resource.
     */
    ConditionalResource storeResource(ConditionalResource resource);

    /**
     * In memory JsonStorage.
     */
    final class InMemory implements JsonStorage {

        /**
         * Storage map.
         */
        private final Map<URI, ConditionalResource> storage =
            new ConcurrentHashMap<>();

        @Override
        public ConditionalResource getResource(final URI uri) {
            return storage.get(uri);
        }

        @Override
        public ConditionalResource storeResource(
            final ConditionalResource resource
        ) {
            return storage.put(resource.uri(), resource);
        }
    }
}