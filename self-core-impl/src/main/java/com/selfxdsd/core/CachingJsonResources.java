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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonValue;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Cacheable JSON Resources used by Provider.
 * @author criske
 * @version $Id$
 * @since 0.0.79
 */
public final class CachingJsonResources implements JsonResources {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        CachingJsonResources.class
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
     * @param jsonStorage JSON storage.
     */
    public CachingJsonResources(final JsonResources delegate,
                                final JsonStorage jsonStorage) {
        this.delegate = delegate;
        this.jsonStorage = jsonStorage;
    }

    /**
     * Ctor.
     * @param delegate JsonResources delegate.
     */
    public CachingJsonResources(final JsonResources delegate) {
        this(delegate, new JsonStorage.InMemory());
    }


    @Override
    public JsonResources authenticated(final AccessToken accessToken) {
        return new CachingJsonResources(
            this.delegate.authenticated(accessToken),
            this.jsonStorage
        );
    }

    @Override
    public Resource get(final URI uri) {
        return this.tryGetFromCache(uri, Collections::emptyMap);
    }

    @Override
    public Resource get(final URI uri,
                        final Supplier<Map<String, List<String>>> headers) {
        return this.tryGetFromCache(uri, headers);
    }

    @Override
    public Resource post(final URI uri,
                         final JsonValue body) {
        return this.delegate.post(uri, body);
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
    private Resource tryGetFromCache(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers
    ) {
        final Resource resource;
        String cachedEtag = this.jsonStorage.getEtag(uri);
        if (cachedEtag != null) {
            final Supplier<Map<String, List<String>>> updatedHeaders =
                this.updateHeaders(headers, cachedEtag);
            final Resource remoteResource = this.delegate
                .get(uri, updatedHeaders);
            final int status = remoteResource.statusCode();

            if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
                final String cachedBody = this.jsonStorage.getResourceBody(uri);
                if (cachedBody != null) {
                    LOG.debug(
                        "Remote resource body for {} was not modified."
                            + " Getting the resource body from json storage.",
                        uri
                    );
                    resource = this.updateResource(remoteResource, cachedBody);
                } else {
                    LOG.debug(
                        "Remote resource for {} was not modified"
                            + " but resource body is missing from json storage."
                            + " Getting the resource from remote.",
                        uri
                    );
                    resource = this.delegate.get(uri, headers);
                    this.tryStoreInCache(uri, resource);
                }
            } else {
                LOG.debug(
                    "Remote resource body for {} was modified or "
                        + " has an unexpected status code.",
                    uri
                );
                resource = remoteResource;
                this.tryStoreInCache(uri, resource);
            }
        } else {
            resource = this.delegate.get(uri, headers);
            this.tryStoreInCache(uri, resource);
        }
        return resource;
    }

    /**
     * Store the Resource in json storage only if Etag header is present
     * and resource's status is HttpURLConnection.OK.
     * @param uri URI.
     * @param resource Resource.
     */
    private void tryStoreInCache(final URI uri, final Resource resource) {
        final List<String> etag = resource.headers().get("ETag");
        if (resource.statusCode() == HttpURLConnection.HTTP_OK
            && (etag != null && !etag.isEmpty())) {
            LOG.debug(
                "Storing remote resource body for {} with ETag {}",
                uri,
                etag.get(0)
            );
            this.jsonStorage.store(uri, etag.get(0), resource.toString());
        }
    }

    /**
     * Update Resource with a new status and body but keeping its headers.
     * @param resource Resource.
     * @param body New body as JSON.
     * @return Updated Resource.
     */
    private Resource updateResource(
        final Resource resource,
        final String body
    ) {
        return resource.newInstance(
            HttpURLConnection.HTTP_OK,
            Json.createReader(new StringReader(body)).readObject(),
            resource.headers()
        );
    }

    /**
     * Append "If-None-Match" header to current headers.
     * @param headers Headers.
     * @param etag Etag.
     * @return Updated headers.
     */
    private Supplier<Map<String, List<String>>> updateHeaders(
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