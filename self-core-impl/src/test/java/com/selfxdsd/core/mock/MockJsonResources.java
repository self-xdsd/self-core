package com.selfxdsd.core.mock;

import com.selfxdsd.core.JsonResources;
import com.selfxdsd.core.Resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.URI;
import java.util.function.Function;

/**
 * A mock implementation of {@link JsonResources} used to unit test
 * {@link com.selfxdsd.api.Provider} requests.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class MockJsonResources implements JsonResources {

    /**
     * Access token for authenticated requests.
     */
    private final String accessToken;
    /**
     * Callback used by tests to simulate a Resource response.
     */
    private final Function<MockRequest, MockResource> onRequest;

    /**
     * Ctor.
     * @param accessToken Access token for authenticated requests.
     * @param onRequest Callback used by tests to simulate a Resource response.
     */
    public MockJsonResources(final String accessToken,
                             final
                             Function<MockRequest, MockResource> onRequest) {
        this.accessToken = accessToken;
        this.onRequest = onRequest;
    }

    /**
     * Ctor.
     * @param onRequest Callback used by tests to simulate a Resource response.
     */
    public MockJsonResources(final
                             Function<MockRequest, MockResource> onRequest) {
        this("", onRequest);
    }

    @Override
    public JsonResources authenticated(final String accessToken) {
        throw new UnsupportedOperationException("Use the appropriate "
            + "MockJsonResources constructor to mock "
            + "an authenticated JsonResources.");
    }

    @Override
    public Resource get(final URI uri) {
        final MockRequest request = new MockRequest(
            "GET",
            uri,
            JsonValue.NULL,
            accessToken
        );
        return onRequest.apply(request);
    }

    @Override
    public Resource post(final URI uri, final JsonObject body) {
        final MockRequest request = new MockRequest(
            "POST",
            uri,
            body,
            accessToken
        );
        return onRequest.apply(request);
    }

    @Override
    public Resource patch(final URI uri, final JsonObject body) {
        final MockRequest request = new MockRequest(
            "PATCH",
            uri,
            body,
            accessToken
        );
        return onRequest.apply(request);
    }

    /**
     * Encapsulates the elements of a {@link MockJsonResources}
     * request.
     */
    public static final class MockRequest {
        /**
         * Http method.
         */
        private final String method;
        /**
         * Request URI.
         */
        private final URI uri;
        /**
         * Request body.
         */
        private final JsonValue body;
        /**
         * Access token for authenticated requests.
         */
        private final String accessToken;

        /**
         * Ctor.
         * @param method Http method.
         * @param uri Request URI.
         * @param body Request body.
         * @param accessToken Access token for authenticated requests.
         */
        private MockRequest(final String method,
                            final URI uri,
                            final JsonValue body,
                            final String accessToken) {
            this.method = method;
            this.uri = uri;
            this.body = body;
            this.accessToken = accessToken;
        }

        /**
         * Http method.
         * @return String.
         */
        public String getMethod() {
            return method;
        }

        /**
         * Request URI.
         * @return URI.
         */
        public URI getUri() {
            return uri;
        }

        /**
         * Response body.
         * @return JsonValue
         */
        public JsonValue getBody() {
            return body;
        }

        /**
         * Access token for authenticated requests.
         * @return String.
         */
        public String getAccessToken() {
            return accessToken;
        }
    }

    /**
     * A mocked {@link Resource} response.
     */
    public static final class MockResource implements Resource {

        /**
         * Status code.
         */
        private final int statusCode;
        /**
         * Response body.
         */
        private final JsonValue body;

        /**
         * Ctor.
         * @param statusCode Status code.
         * @param body Response body.
         */
        public MockResource(final int statusCode, final JsonValue body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public JsonObject asJsonObject() {
            final JsonObject jsonObject;
            if (body instanceof JsonObject) {
                jsonObject = (JsonObject) body;
            } else {
                jsonObject = Json.createObjectBuilder().build();
            }
            return jsonObject;
        }

        @Override
        public JsonArray asJsonArray() {
            final JsonArray jsonArray;
            if (body instanceof JsonArray) {
                jsonArray = (JsonArray) body;
            } else {
                jsonArray = Json.createArrayBuilder().build();
            }
            return jsonArray;
        }
    }
}
