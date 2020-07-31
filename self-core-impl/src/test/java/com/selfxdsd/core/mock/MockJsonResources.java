package com.selfxdsd.core.mock;

import com.selfxdsd.core.AccessToken;
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
 * <br/>
 * Here is an example of how this class could be used to test a
 * Github Provider.
 * <br/>
 * It handles most of the provider requests (repo and invitations).
 * <pre>
 * final JsonResources resources = new MockJsonResources(r -> {
 *    final MockResource response;
 *    if (r.getMethod().equals("GET")) {
 *        String uri = r.getUri().toString();
 *        if (uri.contains("repos/myname")) {
 *            if (uri.contains("myrepo")) {
 *                response = new MockResource(200, Json
 *                    .createObjectBuilder()
 *                    .add("id", 1)
 *                    .add("full-name", "myname/myrepo")
 *                    .build());
 *            } else {
 *                //repo not found
 *                response = new MockResource(404, JsonValue.NULL);
 *            }
 *        } else if (uri.contains("user/repository_invitations")) {
 *            if (r.getAccessToken() == null) {
 *                //not authorized, require authentication
 *                response = new MockResource(401, JsonValue.NULL);
 *            } else {
 *                response = new MockResource(200, Json
 *                    .createArrayBuilder()
 *                    .add(Json
 *                        .createObjectBuilder()
 *                        .add("id", 1)
 *                        .add("full-name", "myname/myrepo")
 *                        .build())
 *                    .build());
 *            }
 *        } else {
 *            //uri mapping not found
 *            response = new MockResource(404, JsonValue.NULL);
 *        }
 *
 *    } else {
 *        //unsupported http method
 *        response = new MockResource(405, JsonValue.NULL);
 *    }
 *    return response;
 *});
 *
 *final Provider github = new Github(user, storage, resources);
 *final Repo repo = github.repo("myrepo");
 *final Invitations invitations = github.invitations();
 *...
 * </pre>
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class MockJsonResources implements JsonResources {

    /**
     * Access token for authenticated requests.
     */
    private final AccessToken accessToken;
    /**
     * Callback used by tests to simulate a Resource response.
     */
    private final Function<MockRequest, MockResource> onRequest;

    /**
     * Ctor.
     * @param accessToken Access token for authenticated requests.
     * @param onRequest Callback used by tests to simulate a Resource response.
     */
    public MockJsonResources(final AccessToken accessToken,
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
        this(null, onRequest);
    }

    @Override
    public JsonResources authenticated(final AccessToken accessToken) {
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

    @Override
    public Resource put(final URI uri, final JsonObject body) {
        final MockRequest request = new MockRequest(
            "PUT",
            uri,
            body,
            this.accessToken
        );
        return this.onRequest.apply(request);
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
        private final AccessToken accessToken;

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
                            final AccessToken accessToken) {
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
         * @return AccessToken.
         */
        public AccessToken getAccessToken() {
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
