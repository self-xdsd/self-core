package com.selfxdsd.core.mock;


import com.selfxdsd.core.AccessToken;
import com.selfxdsd.core.JsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

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
        final AccessToken token = mockAccessToken("header", "tk123");
        final JsonResources res = new MockJsonResources(token, r ->
            new MockResource(200, Json
                .createObjectBuilder()
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken().value())
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
        final AccessToken token = mockAccessToken("header", "tk123");
        final JsonResources res = new MockJsonResources(token, r ->
            new MockResource(200, Json
                .createObjectBuilder((JsonObject) r.getBody())
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken().value())
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
        final AccessToken token = mockAccessToken("header", "tk123");
        final JsonResources res = new MockJsonResources(token, r ->
            new MockResource(200, Json
                .createObjectBuilder((JsonObject) r.getBody())
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken().value())
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
     * Simulates PUT request.
     * @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void simulatesPutRequest() {
        final URI uri = URI.create("/");
        final AccessToken token = mockAccessToken("header", "tk123");
        final JsonResources res = new MockJsonResources(token, r ->
            new MockResource(200, Json
                .createObjectBuilder((JsonObject) r.getBody())
                .add("method", r.getMethod())
                .add("uri", r.getUri().toString())
                .add("token", r.getAccessToken().value())
                .build())
        );
        final JsonObject patchObject = res.put(uri,
            Json.createObjectBuilder()
                .add("message", "Hello")
                .build()
        ).asJsonObject();
        MatcherAssert.assertThat(patchObject.getString("method"),
            Matchers.equalTo("PUT"));
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
        final AccessToken token = mockAccessToken("header", "tk123");
        final JsonResources res = new MockJsonResources(token, r ->
            new MockResource(200,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("method", r.getMethod())
                        .add("uri", r.getUri().toString())
                        .add("token", r.getAccessToken().value()))
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
        final AccessToken token = mockAccessToken("header", "token");
        new MockJsonResources(r -> new MockResource(400,
            JsonValue.NULL)).authenticated(token);
    }

    /**
     * JsonResources requests history can be tested.
     *  @checkstyle RegexpSingleline (20 lines)
     */
    @Test
    public void requestHistoryCanBeTested() {
        final MockJsonResources resources = new MockJsonResources(r ->
            new MockResource(200, JsonValue.NULL));

        resources.get(URI.create("/"));
        resources.post(URI.create("/"), JsonValue.NULL);
        resources.put(URI.create("/"), JsonValue.NULL);

        MatcherAssert.assertThat(resources.requests(),
            Matchers.iterableWithSize(3));
        MatcherAssert.assertThat(resources.requests().first().getMethod(),
            Matchers.equalTo("GET"));
        MatcherAssert.assertThat(resources.requests().atIndex(1).getMethod(),
            Matchers.equalTo("POST"));
        MatcherAssert.assertThat(resources.requests().last().getMethod(),
            Matchers.equalTo("PUT"));
    }

    /**
     * Mocks an access token.
     * @param header Header name.
     * @param value Token value.
     * @return String.
     */
    private AccessToken mockAccessToken(final String header,
                                        final String value){
        final AccessToken token = Mockito.mock(AccessToken.class);
        Mockito.when(token.header()).thenReturn(header);
        Mockito.when(token.value()).thenReturn(value);
        return token;
    }
}
