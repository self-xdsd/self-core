package com.selfxdsd.core.mock;


import com.selfxdsd.core.JsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.URI;

import static com.selfxdsd.core.mock.MockJsonResources.MockResource;

/**
 * Unit tests for {@link MockJsonResources}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class MockJsonResourcesTestCase {

    /**
     * Simulates GET request.
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void simulatesGetRequest() {
        final URI uri = URI.create("/");
        final JsonResources res = new MockJsonResources("tk123", r ->
            new MockResource(200, Json
                .createObjectBuilder()
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken())
                .build())
        );
        final JsonObject getObject = res.get(uri).asJsonObject();
        MatcherAssert.assertThat(getObject.getString("method"),
            Matchers.equalTo("GET"));
        MatcherAssert.assertThat(getObject.getString("uri"),
            Matchers.equalTo(uri.toString()));
        MatcherAssert.assertThat(getObject.getString("token"),
            Matchers.equalTo("tk123"));
    }

    /**
     * Simulates POST request.
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void simulatesPostRequest() {
        final URI uri = URI.create("/");
        final JsonResources res = new MockJsonResources("tk123", r ->
            new MockResource(200, Json
                .createObjectBuilder((JsonObject) r.getBody())
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken())
                .build())
        );
        final JsonObject postObject = res.post(uri,
            Json.createObjectBuilder()
                .add("message", "Hello")
                .build()
        ).asJsonObject();
        MatcherAssert.assertThat(postObject.getString("method"),
            Matchers.equalTo("POST"));
        MatcherAssert.assertThat(postObject.getString("uri"),
            Matchers.equalTo(uri.toString()));
        MatcherAssert.assertThat(postObject.getString("token"),
            Matchers.equalTo("tk123"));
        MatcherAssert.assertThat(postObject.getString("message"),
            Matchers.equalTo("Hello"));
    }

    /**
     * Simulates PATCH request.
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void simulatesPatchRequest() {
        final URI uri = URI.create("/");
        final JsonResources res = new MockJsonResources("tk123", r ->
            new MockResource(200, Json
                .createObjectBuilder((JsonObject) r.getBody())
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken())
                .build())
        );
        final JsonObject patchObject = res.patch(uri,
            Json.createObjectBuilder()
                .add("message", "Hello")
                .build()
        ).asJsonObject();
        MatcherAssert.assertThat(patchObject.getString("method"),
            Matchers.equalTo("PATCH"));
        MatcherAssert.assertThat(patchObject.getString("uri"),
            Matchers.equalTo(uri.toString()));
        MatcherAssert.assertThat(patchObject.getString("token"),
            Matchers.equalTo("tk123"));
        MatcherAssert.assertThat(patchObject.getString("message"),
            Matchers.equalTo("Hello"));
    }

    /**
     * Simulates JsonArray Resource response.
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void simulatesJsonArrayResourceResponse() {
        final URI uri = URI.create("/");
        final JsonResources res = new MockJsonResources("tk123", r ->
            new MockResource(200,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("method", r.getMethod())
                        .add("uri", r.getUri().toString())
                        .add("token", r.getAccessToken()))
                    .build())

        );

        MatcherAssert.assertThat(res.get(uri).asJsonArray(),
            Matchers.iterableWithSize(1));
    }

    /***
     * Falls back to empty JsonObject if Resource body response
     * is not a JsonObject when calling asJsonObject().
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void fallsBackToEmptyJsonObjectResponse() {
        final JsonResources res = new MockJsonResources(r ->
            new MockResource(200, JsonValue.NULL));
        MatcherAssert.assertThat(res.get(URI.create("/")).asJsonObject().size(),
            Matchers.is(0));
    }

    /***
     * Falls back to empty JsonArray if Resource body response
     * is not a JsonArray when calling asJsonArray().
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void fallsBackToEmptyJsonArrayResponse() {
        final JsonResources res = new MockJsonResources(r ->
            new MockResource(200, JsonValue.NULL));
        MatcherAssert.assertThat(res.get(URI.create("/")).asJsonArray().size(),
            Matchers.is(0));
    }

    /**
     * Throws when trying constructing with authenticated().
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenConstructingWithAuthenticated() {
        new MockJsonResources(r -> new MockResource(400,
            JsonValue.NULL)).authenticated("token");
    }
}
