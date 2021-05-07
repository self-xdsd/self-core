package com.selfxdsd.core.mock;

import com.selfxdsd.core.AccessToken;
import com.selfxdsd.core.JsonResources;
import com.selfxdsd.core.Resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A mock implementation of {@link JsonResources} used to unit test
 * {@link com.selfxdsd.api.Provider} requests. It also offers a way to test
 * the requests history by using {@link MockJsonResources#requests()}.
 * <br/>
 * Here is an example of how this class could be used to test a
 * Github Provider.
 * <br/>
 * It handles most of the provider requests (repo and invitations).
 * <pre>
 * final MockJsonResources resources = new MockJsonResources(r -> {
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
 *final MockRequests requests = resources.requests();
 * //...test requests and resource responses.
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
     * Request history.
     */
    private final MockRequests requests;

    /**
     * Ctor.
     * @param accessToken Access token for authenticated requests.
     * @param onRequest Callback used by tests to simulate a Resource response.
     */
    public MockJsonResources(final AccessToken accessToken,
                             final
                             Function<MockRequest, MockResource> onRequest) {
        this.requests = new MockRequests();
        this.accessToken = accessToken;
        this.onRequest = (req) -> onRequest.apply(this.requests.add(req));
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
    public Resource get(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers
    ) {
        final MockRequest request = new MockRequest(
            "GET",
            uri,
            headers.get(),
            JsonValue.NULL,
            accessToken
        );
        return onRequest.apply(request);
    }

    @Override
    public Resource post(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    ) {
        final MockRequest request = new MockRequest(
            "POST",
            uri,
            headers.get(),
            body,
            accessToken
        );
        return onRequest.apply(request);
    }

    @Override
    public Resource patch(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    ) {
        final MockRequest request = new MockRequest(
            "PATCH",
            uri,
            headers.get(),
            body,
            accessToken
        );
        return onRequest.apply(request);
    }

    @Override
    public Resource put(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body) {
        final MockRequest request = new MockRequest(
            "PUT",
            uri,
            headers.get(),
            body,
            this.accessToken
        );
        return this.onRequest.apply(request);
    }

    @Override
    public Resource delete(
        final URI uri,
        final Supplier<Map<String, List<String>>> headers,
        final JsonValue body
    ) {
        final MockRequest request = new MockRequest(
            "DELETE",
            uri,
            headers.get(),
            body,
            this.accessToken
        );
        return this.onRequest.apply(request);
    }

    /**
     * Get the requests history.
     * @return MockRequests.
     */
    public MockRequests requests(){
        return this.requests;
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
         * HTTP Headers.
         */
        private final Map<String, List<String>> headers;

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
         * @param headers HTTP Headers.
         * @param body Request body.
         * @param accessToken Access token for authenticated requests.
         */
        private MockRequest(final String method,
                            final URI uri,
                            final Map<String, List<String>> headers,
                            final JsonValue body,
                            final AccessToken accessToken) {
            this.method = method;
            this.uri = uri;
            this.headers = new HashMap<>(headers);
            if (accessToken != null) {
                this.headers.put(
                    accessToken.header(),
                    List.of(accessToken.value())
                );
            }
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

        /**
         * Request headers.
         * @return Map.
         */
        public Map<String, List<String>> getHeaders() {
            return Collections.unmodifiableMap(this.headers);
        }
    }

    /**
     * Tracks the request history of {@link MockJsonResources}.
     */
    public static final class MockRequests implements Iterable<MockRequest> {

        /**
         * List that keeps the request history.
         */
        private final List<MockRequest> requests;

        /**
         * Private ctor.
         */
        private MockRequests(){
            this.requests= new ArrayList<>();
        }

        /**
         * Adds new request to the history.
         * @param request Request.
         * @return Added Request.
         */
        private MockRequest add(final MockRequest request){
            requests.add(request);
            return request;
        }

        /**
         * The Request at the specified index.
         * @param index Zero based index.
         * @return MockRequest.
         */
        public MockRequest atIndex(final int index) {
            return requests.get(index);
        }

        /**
         * First request ever made.
         * @return MockRequest
         */
        public MockRequest first() {
            return requests.get(0);
        }

        /**
         * Last request made.
         * @return MockRequest
         */
        public MockRequest last() {
            return requests.get(requests.size() - 1);
        }

        @Override
        public Iterator<MockRequest> iterator() {
            return requests.iterator();
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
         * Response headers.
         */
        private final Map<String, List<String>> headers;

        /**
         * Ctor.
         * @param statusCode Status code.
         * @param body Response body.
         */
        public MockResource(final int statusCode, final JsonValue body) {
            this(statusCode, body, Collections.emptyMap());
        }

        /**
         * Ctor.
         * @param statusCode Status code.
         * @param body Response body.
         * @param headers Response headers.
         */
        public MockResource(final int statusCode,
                            final JsonValue body,
                            final Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = Collections.unmodifiableMap(headers);
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

        @Override
        public Map<String, List<String>> headers() {
            return this.headers;
        }

        @Override
        public Builder newBuilder() {
            return new Builder(this, MockResource::new);
        }

        @Override
        public String toString() {
            return this.body.toString();
        }
    }
}
