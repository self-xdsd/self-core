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

import com.selfxdsd.core.mock.MockJsonResources;
import com.selfxdsd.core.mock.MockJsonResources.MockResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.selfxdsd.core.mock.MockJsonResources.MockRequest;

/**
 * Unit tests for {@link CachingJsonResources}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.79
 */
public final class CachingJsonResourcesTestCase {

    /**
     * Should ignore cache when Etag header is not set in Resource response.
     */
    @Test
    public void shouldIgnoreCache(){
        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockResource(200, JsonValue.NULL)
        );
        final JsonResources cacheResources = new CachingJsonResources(
            resources, storage
        );

        cacheResources.get(URI.create("/"));

        Mockito
            .verify(storage, Mockito.never())
            .store(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            );
    }

    /**
     * Should store in cache if Etag header is present.
     */
    @Test
    public void shouldStoreInCacheWhenEntriesNotFound() {
        final JsonStorage storage = new JsonStorage.InMemory();
        final URI uri = URI.create("/");
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();
        final MockResource resource = new MockResource(200, body,
            Map.of("ETag", List.of("etag-123"))
        );
        final MockJsonResources resources = new MockJsonResources(
            req -> resource
        );
        final JsonResources cacheResources = new CachingJsonResources(
            resources, storage
        );

        final Resource result = cacheResources.get(uri);
        MatcherAssert.assertThat(result, Matchers.equalTo(resource));
        MatcherAssert.assertThat(storage.getEtag(uri),
            Matchers.equalTo("etag-123"));
        MatcherAssert.assertThat(storage.getResourceBody(uri),
            Matchers.equalTo(body.toString()));
    }

    /**
     * Should get Resource from json storage if the remote resource is not
     * changed.
     */
    @Test
    public void shouldGetFromCacheIfRemoteNotChanged(){
        final URI uri = URI.create("/");
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();

        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final AccessToken token = new AccessToken.Github("token-123");
        final MockJsonResources resources = new MockJsonResources(
            token,
            req -> new MockResource(
                HttpURLConnection.HTTP_NOT_MODIFIED,
                JsonValue.NULL
            )
        );
        final JsonResources cacheResources = new CachingJsonResources(
            resources, storage
        );

        Mockito.when(storage.getEtag(uri)).thenReturn("etag-123");
        Mockito.when(storage.getResourceBody(uri)).thenReturn(body.toString());

        final Resource result = cacheResources.get(
            uri,
            () -> Map.of("foo", List.of("bar"))
        );

        final MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getHeaders().get("If-None-Match").get(0),
            Matchers.equalTo("etag-123")
        );
        MatcherAssert.assertThat(
            req.getHeaders().get("Authorization").get(0),
            Matchers.equalTo("token token-123")
        );
        MatcherAssert.assertThat(
            req.getHeaders().get("foo").get(0),
            Matchers.equalTo("bar")
        );
        MatcherAssert.assertThat(result.statusCode(), Matchers.is(
            HttpURLConnection.HTTP_OK
        ));
        MatcherAssert.assertThat(result.toString(), Matchers.equalTo(
            body.toString()
        ));
    }

    /**
     * Should get Resource from remote if the remote resource has
     * changed.
     */
    @Test
    public void shouldGetFromRemoteIfRemoteHasChanged(){
        final URI uri = URI.create("/");
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();
        final JsonValue newBody =  Json.createObjectBuilder()
            .add("hello", "universe")
            .build();

        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockResource(
                HttpURLConnection.HTTP_OK,
                newBody,
                Map.of("ETag", List.of("etag-124"))
            )
        );
        final JsonResources cacheResources = new CachingJsonResources(
            resources, storage
        );

        Mockito.when(storage.getEtag(uri)).thenReturn("etag-123");
        Mockito.when(storage.getResourceBody(uri)).thenReturn(body.toString());

        final Resource result = cacheResources.get(uri);
        final MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getHeaders().get("If-None-Match").get(0),
            Matchers.equalTo("etag-123")
        );
        MatcherAssert.assertThat(result.statusCode(), Matchers.is(
            HttpURLConnection.HTTP_OK
        ));
        MatcherAssert.assertThat(result.toString(), Matchers.equalTo(
            newBody.toString()
        ));
        
        Mockito.verify(storage).store(uri, "etag-124", newBody.toString());
    }

    /**
     * Should get Resource from remote if etag is present but 
     * resource is missing.
     */
    @Test
    public void shouldGetFromRemoteIfResourceEntryIsMissing(){
        final URI uri = URI.create("/");
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();
        
        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final MockJsonResources resources = new MockJsonResources(
            req -> {
                final boolean hasCheckHeader = req.getHeaders()
                    .containsKey("If-None-Match");
                final MockResource res;
                if (hasCheckHeader) {
                    res = new MockResource(
                        HttpURLConnection.HTTP_NOT_MODIFIED,
                        JsonValue.NULL,
                        Map.of("ETag", List.of("etag-123"))
                    );
                } else {
                    res = new MockResource(
                        HttpURLConnection.HTTP_OK,
                        body,
                        Map.of("ETag", List.of("etag-123"))
                    );
                }
                return res;
            }
        );
        final JsonResources cacheResources = new CachingJsonResources(
            resources, storage
        );

        Mockito.when(storage.getEtag(uri)).thenReturn("etag-123");

        final Resource result = cacheResources.get(uri);
        MatcherAssert.assertThat(
            resources.requests().first().getHeaders().get("If-None-Match"),
            Matchers.equalTo(List.of("etag-123"))
        );
        MatcherAssert.assertThat(result.statusCode(), Matchers.is(
            HttpURLConnection.HTTP_OK
        ));
        MatcherAssert.assertThat(result.toString(), Matchers.equalTo(
            body.toString()
        ));

        Mockito.verify(storage).store(uri, "etag-123", body.toString());
    }
}