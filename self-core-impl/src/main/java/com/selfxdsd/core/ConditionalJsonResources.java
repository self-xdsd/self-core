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

import com.selfxdsd.api.storage.JsonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Cacheable JSON Resources used by Provider.
 * @author criske
 * @version $Id$
 * @since 0.0.79
 */
public final class ConditionalJsonResources implements JsonResources {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        ConditionalJsonResources.class
    );


    /**
     * JsonResources delegate.
     */
    private final JsonResources delegate;


    /**
     * Json storage.
     */
    private final JsonStorage jsonStorage;

    /**
     * Ctor.
     * @param delegate JsonResources delegate.
     */
    public ConditionalJsonResources(final JsonResources delegate) {
        this(delegate, new JsonStorage.InMemory());
    }

    /**
     * Ctor.
     * @param delegate JsonResources delegate.
     * @param jsonStorage JSON storage.
     */
    public ConditionalJsonResources(final JsonResources delegate,
                                    final JsonStorage jsonStorage) {
        this.delegate = delegate;
        this.jsonStorage = jsonStorage;
    }


    @Override
    public JsonResources authenticated(final AccessToken accessToken) {
        return new ConditionalJsonResources(
            this.delegate.authenticated(accessToken),
            this.jsonStorage
        );
    }

    @Override
    public Resource get(final URI uri) {
        return this.conditionalGet(uri, Collections::emptyMap);
    }

    @Override
    public Resource get(final URI uri,
                        final Supplier<Map<String, List<String>>> headers) {
        return this.conditionalGet(uri, headers);
    }

    @Override
    public Resource post(final URI uri,
                         final JsonValue body) {
        return this.post(uri, Collections::emptyMap, body);
    }

    @Override
    public Resource post(final URI uri,
                         final Supplier<Map<String, List<String>>> headers,
                         final JsonValue body) {
        return this.delegate.post(uri, headers, body);
    }

    @Override
    public Resource patch(final URI uri, final JsonValue body) {
        return this.delegate.patch(uri, body);
    }

    @Override
    public Resource put(final URI uri, final JsonValue body) {
        return this.delegate.put(uri, body);
    }

    @Override
    public Resource delete(final URI uri, final JsonValue body) {
        return this.delegate.delete(uri, body);
    }

    /**
     * Try to get Resource from json storage cache by URI, otherwise fetch
     * from remote and then store the Resource.
     * @param uri URI.
     * @param headers Current Headers.
     * @return Cached or remote Resource.
     */
    private Resource conditionalGet(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers
    ) {
        final Resource resource;
        final String etag = this.jsonStorage.getEtag(uri);
        if (etag != null) {
            final Resource remoteResource = this.delegate
                .get(uri, this.ifNoneMatch(headers, etag));
            final int status = remoteResource.statusCode();

            if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
                final String cachedBody = this.jsonStorage.getResourceBody(uri);
                if (cachedBody != null) {
                    LOG.debug(
                        "Remote resource body for {} was not modified."
                            + " Getting the resource body from json storage.",
                        uri
                    );
                    resource = remoteResource
                        .newBuilder()
                        .status(HttpURLConnection.HTTP_OK)
                        .body(cachedBody)
                        .build();
                } else {
                    LOG.debug(
                        "Remote resource for {} was not modified"
                            + " but resource body is missing from json storage."
                            + " Getting the resource from remote.",
                        uri
                    );
                    resource = this.delegate.get(uri, headers);
                    this.storeInCache(uri, resource);
                }
            } else {
                LOG.debug(
                    "Remote resource body for {} was modified or "
                        + " has an unexpected status code.",
                    uri
                );
                resource = remoteResource;
                this.storeInCache(uri, resource);
            }
        } else {
            resource = this.delegate.get(uri, headers);
            this.storeInCache(uri, resource);
        }
        return resource;
    }

    /**
     * Store the Resource in json storage only if Etag header is present
     * and resource's status is HttpURLConnection.OK.
     * @param uri URI.
     * @param resource Resource.
     */
    private void storeInCache(final URI uri, final Resource resource) {
        final List<String> etag = resource
            .headers()
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey()
                .equalsIgnoreCase("ETag"))
            .flatMap(entry -> entry.getValue().stream())
            .collect(Collectors.toList());
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            if (!etag.isEmpty()) {
                LOG.debug(
                    "Storing remote resource body for {} with ETag {}",
                    uri,
                    etag.get(0)
                );
                this.jsonStorage.store(uri, etag.get(0), resource.toString());
            } else {
                LOG.debug(
                    "ETag header not found for {}. Caching is skipped.",
                    uri
                );
            }
        }
    }

    /**
     * Append "If-None-Match" header to current headers.
     * @param headers Headers.
     * @param etag Etag.
     * @return Updated headers.
     */
    private Supplier<Map<String, List<String>>> ifNoneMatch(
        final Supplier<Map<String, List<String>>> headers,
        final String etag
    ){
        return () -> {
            final Map<String, List<String>> updatedHeaders =
                new HashMap<>(headers.get());
            updatedHeaders.put("If-None-Match", List.of(etag));
            return updatedHeaders;
        };
    }
}