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

import com.selfxdsd.api.CachedResource;
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.storage.JsonStorage;
import com.selfxdsd.core.mock.MockJsonResources;
import com.selfxdsd.core.mock.MockJsonResources.MockResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.selfxdsd.core.mock.MockJsonResources.MockRequest;

/**
 * Unit tests for {@link ConditionalJsonResources}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.79
 * @checkstyle JavaNCSS (1000 lines)
 * @checkstyle ExecutableStatementCount (1000 lines)
 * @checkstyle MethodLength (1000 lines)
 */
public final class ConditionalJsonResourcesTestCase {

    /**
     * Should ignore cache when Etag header is not set in Resource response.
     */
    @Test
    public void shouldIgnoreCache(){
        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockResource(200, JsonValue.NULL)
        );
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );

        cacheResources.get(URI.create("/"));

        Mockito
            .verify(storage, Mockito.never())
            .storeResource(
                Mockito.any(),
                Mockito.any()
            );
    }

    /**
     * Should ignore cache when headers contains Cache-Control: no-cache.
     */
    @Test
    public void shouldIgnoreCacheWhenCacheControlIsPresent() {
        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();
        final MockResource resource = new MockResource(200, body,
            Map.of("eTaG", List.of("etag-123"))
        );
        final MockJsonResources resources = new MockJsonResources(
            req -> resource
        );
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );

        cacheResources.get(
            URI.create("/"),
            () -> Map.of("Cache-Control", List.of("no-cache"))
        );

        Mockito
            .verify(storage, Mockito.never())
            .storeResource(
                Mockito.any(),
                Mockito.any()
            );
    }

    /**
     * Should store in cache if Etag header is present. It also should
     * ignore the header key casing when searching for etag header.
     */
    @Test
    public void shouldStoreInCacheWhenEntriesNotFound() {
        final JsonStorage storage = new JsonStorage.InMemory();
        final URI uri = URI.create("/");
        final JsonValue body = Json.createObjectBuilder()
            .add("hello", "world")
            .build();
        final MockResource resource = new MockResource(200, body,
            Map.of("eTaG", List.of("etag-123"))
        );
        final MockJsonResources resources = new MockJsonResources(
            req -> resource
        );
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );

        final Resource result = cacheResources.get(uri);
        final CachedResource stored = storage.getResource(uri);
        MatcherAssert.assertThat(result, Matchers.equalTo(resource));
        MatcherAssert.assertThat(stored.etag(),
            Matchers.equalTo("etag-123"));
        MatcherAssert.assertThat(stored.asJsonObject().toString(),
            Matchers.equalTo(body.toString()));
    }

    /**
     * Should get Resource from json storage if the remote resource is not
     * changed.
     */
    @Test
    public void shouldGetFromCacheIfRemoteNotChanged(){
        final URI uri = URI.create("/");
        final JsonValue body = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("hello", "world")
                .build())
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
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );

        final Resource resource = new MockResource(
            HttpURLConnection.HTTP_OK,
            body,
            Map.of("ETag", List.of("etag-123"))
        );
        Mockito.when(storage.getResource(uri)).thenReturn(
            new CachedResource() {
                @Override
                public URI uri() {
                    return uri;
                }

                @Override
                public String etag() {
                    return resource.etag();
                }

                @Override
                public LocalDateTime creationDate() {
                    return LocalDateTime.now();
                }

                @Override
                public int statusCode() {
                    return resource.statusCode();
                }

                @Override
                public JsonObject asJsonObject() {
                    return resource.asJsonObject();
                }

                @Override
                public JsonArray asJsonArray() {
                    return resource.asJsonArray();
                }

                @Override
                public String body() {
                    return resource.body();
                }

                @Override
                public Map<String, List<String>> headers() {
                    return resource.headers();
                }

                @Override
                public Builder newBuilder() {
                    return resource.newBuilder();
                }
            }
        );

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
        MatcherAssert.assertThat(
            result.asJsonArray().toString(),
            Matchers.equalTo(body.toString())
        );
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
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );
        final Resource resource = new MockResource(
            HttpURLConnection.HTTP_OK,
            body,
            Map.of("ETag", List.of("etag-123"))
        );
        Mockito.when(storage.getResource(uri)).thenReturn(
            new CachedResource() {
                @Override
                public URI uri() {
                    return uri;
                }

                @Override
                public String etag() {
                    return resource.etag();
                }

                @Override
                public LocalDateTime creationDate() {
                    return LocalDateTime.now();
                }

                @Override
                public int statusCode() {
                    return resource.statusCode();
                }

                @Override
                public JsonObject asJsonObject() {
                    return resource.asJsonObject();
                }

                @Override
                public JsonArray asJsonArray() {
                    return resource.asJsonArray();
                }

                @Override
                public String body() {
                    return resource.body();
                }

                @Override
                public Map<String, List<String>> headers() {
                    return resource.headers();
                }

                @Override
                public Builder newBuilder() {
                    return resource.newBuilder();
                }
            }
        );
        Mockito.when(storage.updateResource(Mockito.any(), Mockito.any()))
            .thenAnswer(
                invocation -> {
                    Resource res = (Resource) invocation.getArguments()[1];
                    return new CachedResource() {
                        @Override
                        public URI uri() {
                            return (URI) invocation.getArguments()[0];
                        }

                        @Override
                        public String etag() {
                            return res.etag();
                        }

                        @Override
                        public LocalDateTime creationDate() {
                            return LocalDateTime.now();
                        }

                        @Override
                        public int statusCode() {
                            return res.statusCode();
                        }

                        @Override
                        public JsonObject asJsonObject() {
                            return res.asJsonObject();
                        }

                        @Override
                        public JsonArray asJsonArray() {
                            return res.asJsonArray();
                        }

                        @Override
                        public String body() {
                            return res.body();
                        }

                        @Override
                        public Map<String, List<String>> headers() {
                            return res.headers();
                        }

                        @Override
                        public Builder newBuilder() {
                            return res.newBuilder();
                        }
                    };
                }
            );

        final Resource result = cacheResources.get(uri);
        final MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getHeaders().get("If-None-Match").get(0),
            Matchers.equalTo("etag-123")
        );
        MatcherAssert.assertThat(result.statusCode(), Matchers.is(
            HttpURLConnection.HTTP_OK
        ));
        MatcherAssert.assertThat(
            result.asJsonObject().toString(),
            Matchers.equalTo(newBody.toString())
        );

        final ArgumentCaptor<Resource> captor = ArgumentCaptor
            .forClass(Resource.class);
        Mockito.verify(storage).updateResource(Mockito.any(), captor.capture());
        final Resource storing = captor.getValue();
        MatcherAssert.assertThat(
            storing.toString(), Matchers.equalTo(newBody.toString())
        );
        MatcherAssert.assertThat(
            storing.etag(), Matchers.equalTo("etag-124")
        );
    }

    /**
     * Should ignore getting from cache if remote check is not modified or not
     * ok. It'll forward the remote resource instead.
     */
    @Test
    public void shouldIgnoreCacheIfRemoteCodeIsNotOkOrNotModified(){
        final URI uri = URI.create("/");

        final JsonStorage storage = Mockito.mock(JsonStorage.class);
        
        final AccessToken token = new AccessToken.Github("token-123");
        final MockJsonResources resources = new MockJsonResources(
            token,
            req -> new MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, storage
        );

        final Resource result = cacheResources.get(uri);

        MatcherAssert.assertThat(result.statusCode(), Matchers.is(
            HttpURLConnection.HTTP_NOT_FOUND
        ));
    }

    /**
     * CachingJsonResources should delegate POST http methods with headers.
     */
    @Test
    public void shouldDelegatePostHttpMethodWithHeaders() {
        final URI uri = URI.create("/");
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, Mockito.mock(JsonStorage.class)
        );
        Supplier<Map<String, List<String>>> emptyMap = Collections::emptyMap;
        cacheResources.post(uri, emptyMap, JsonValue.NULL);

        Mockito.verify(resources).post(uri, emptyMap, JsonValue.NULL);
    }

    /**
     * CachingJsonResources should delegate POST http method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldDelegatePostHttpMethod() {
        final URI uri = URI.create("/");
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, Mockito.mock(JsonStorage.class)
        );
        cacheResources.post(uri, JsonValue.NULL);

        Mockito.verify(resources).post(
            org.mockito.Matchers.eq(uri),
            org.mockito.Matchers.any(Supplier.class),
            org.mockito.Matchers.eq(JsonValue.NULL));
    }

    /**
     * CachingJsonResources should delegate PATCH http method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldDelegatePatchHttpMethod() {
        final URI uri = URI.create("/");
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, Mockito.mock(JsonStorage.class)
        );
        cacheResources.patch(uri, JsonValue.NULL);

        Mockito.verify(resources).patch(
            org.mockito.Matchers.eq(uri),
            org.mockito.Matchers.any(Supplier.class),
            org.mockito.Matchers.eq(JsonValue.NULL));
    }

    /**
     * CachingJsonResources should delegate PUT http method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void shouldDelegatePutHttpMethod() {
        final URI uri = URI.create("/");
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, Mockito.mock(JsonStorage.class)
        );
        cacheResources.put(uri, JsonValue.NULL);


        Mockito.verify(resources).put(
            org.mockito.Matchers.eq(uri),
            org.mockito.Matchers.any(Supplier.class),
            org.mockito.Matchers.eq(JsonValue.NULL));

    }

    /**
     * CachingJsonResources should delegate DELETE http method.
     */
    @Test
    public void shouldDelegateDeleteHttpMethod() {
        final URI uri = URI.create("/");
        final JsonResources resources = Mockito.mock(JsonResources.class);
        final JsonResources cacheResources = new ConditionalJsonResources(
            resources, Mockito.mock(JsonStorage.class)
        );
        cacheResources.delete(uri, JsonValue.NULL);

        Mockito.verify(resources).delete(
            org.mockito.Matchers.eq(uri),
            org.mockito.Matchers.any(Supplier.class),
            org.mockito.Matchers.eq(JsonValue.NULL));
    }

    /**
     * CachingJsonResources should create new instance when authenticated.
     */
    @Test
    public void shouldCreateNewCachingJsonResourcesOnAuth() {
        final JsonResources cacheResources = new ConditionalJsonResources(
            Mockito.mock(JsonResources.class)
        );
        final JsonResources authorized = cacheResources.authenticated(
            new AccessToken.Github("123")
        );
        MatcherAssert.assertThat(authorized, Matchers.instanceOf(
            ConditionalJsonResources.class
        ));
        MatcherAssert.assertThat(
            authorized,
            Matchers.not(Matchers.equalTo(cacheResources))
        );
    }
}