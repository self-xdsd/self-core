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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Integration tests for {@link com.selfxdsd.core.JsonResources.JdkHttp}.
 * We start an in-memory HTTP Server, send the requests and make assertions
 * on what the queries that the server has received.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public final class JdkHttpITCase {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final RandomPort resource = new RandomPort();

    /**
     * We can GET a JsonObject from the server with no access token.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void getJsonObjectOkNoAuth() throws IOException {
        final JsonObject json = Json.createObjectBuilder()
            .add("from", "server")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    json.toString()
                )
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true);
            final Resource response = resources.get(container.home());
            MatcherAssert.assertThat(
                response.asJsonObject(),
                Matchers.equalTo(json)
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_OK)
            );
        }
    }

    /**
     * We can GET a JsonObject from the server with no access token and
     * request having other headers.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void getJsonObjectOkNoAuthWithOtherHeaders() throws IOException {
        final JsonObject json = Json.createObjectBuilder()
            .add("from", "server")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    json.toString()
                )
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true);
            final Resource response = resources.get(
                container.home(),
                () -> Map.of(
                    "my-header-1", List.of("hello1"),
                    "my-header-2", List.of("hello2")
                )
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                response.asJsonObject(),
                Matchers.equalTo(json)
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_OK)
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-1").get(0),
                Matchers.equalTo("hello1")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-2").get(0),
                Matchers.equalTo("hello2")
            );
        }
    }

    /**
     * JdkHttp can POST a JsonObject to the specified URI.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void postJsonObjectWithAuth() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "post")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.post(
                container.home(), body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("POST")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
        }
    }

    /**
     * JdkHttp can POST a JsonObject to the specified URI along with headers.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void postJsonObjectWithAuthAndOtherHeaders() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "post")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.post(
                container.home(),
                () -> Map.of(
                    "my-header-1", List.of("hello1"),
                    "my-header-2", List.of("hello2")
                ),
                body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("POST")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-1").get(0),
                Matchers.equalTo("hello1")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-2").get(0),
                Matchers.equalTo("hello2")
            );
        }
    }

    /**
     * JdkHttp can PATCH a JsonObject at the specified URI.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void patchJsonObjectWithAuth() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "patch")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.patch(
                container.home(), body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_NO_CONTENT)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("PATCH")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
        }
    }

    /**
     * JdkHttp can PATCH a JsonObject at the specified URI along with headers.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void patchJsonObjectWithAuthAndOtherHeaders() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "patch")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.patch(
                container.home(),
                () -> Map.of(
                    "my-header-1", List.of("hello1"),
                    "my-header-2", List.of("hello2")
                ),
                body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_NO_CONTENT)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("PATCH")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-1").get(0),
                Matchers.equalTo("hello1")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-2").get(0),
                Matchers.equalTo("hello2")
            );
        }
    }

    /**
     * JdkHttp can PUT a JsonObject at the specified URI.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void putJsonObjectWithAuth() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "put")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.put(
                container.home(), body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("PUT")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
        }
    }

    /**
     * JdkHttp can PUT a JsonObject at the specified URI along with headers.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void putJsonObjectWithAuthAndOtherHeaders() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "put")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.put(
                container.home(),
                () -> Map.of(
                    "my-header-1", List.of("hello1"),
                    "my-header-2", List.of("hello2")
                ),
                body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("PUT")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-1").get(0),
                Matchers.equalTo("hello1")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-2").get(0),
                Matchers.equalTo("hello2")
            );
        }
    }

    /**
     * JdkHttp can DELETE a JsonObject at the specified URI.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void deleteJsonObjectWithAuth() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "delete")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.delete(
                container.home(), body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("DELETE")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
        }
    }

    /**
     * JdkHttp can DELETE a JsonObject at the specified URI with headers.
     *
     * We assert the Response status and also take the request (MkQuery)
     * that the server has received and make assertions on it -- it should
     * contain the JsonObject we specified, as well the Authentication header.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void deleteJsonObjectWithAuthAndOtherHeaders() throws IOException {
        final JsonObject body = Json.createObjectBuilder()
            .add("test", "delete")
            .build();
        try(
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED)
            ).start(this.resource.port())
        ) {
            final JsonResources resources = new JsonResources.JdkHttp(true)
                .authenticated(new AccessToken.Github("123token456"));
            final Resource response = resources.delete(
                container.home(),
                () -> Map.of(
                    "my-header-1", List.of("hello1"),
                    "my-header-2", List.of("hello2")
                ),
                body
            );
            MatcherAssert.assertThat(
                response.statusCode(),
                Matchers.equalTo(HttpURLConnection.HTTP_CREATED)
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo("DELETE")
            );
            MatcherAssert.assertThat(
                request.body(),
                Matchers.equalTo(body.toString())
            );
            MatcherAssert.assertThat(
                request.headers().get("Authorization").get(0),
                Matchers.equalTo("token 123token456")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-1").get(0),
                Matchers.equalTo("hello1")
            );
            MatcherAssert.assertThat(
                request.headers().get("My-Header-2").get(0),
                Matchers.equalTo("hello2")
            );
        }
    }
}
