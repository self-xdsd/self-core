package com.selfxdsd.core;

import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Unit test for {@link ResourcePaging} implementations.
 * @author criske
 * @version $Id$
 * @since 0.0.84
 */
public final class ResourcePagingTestCase {

    /**
     * ResourcePaging.FromHeaders iterates all resources.
     */
    @Test
    public void fromHeadersShouldIteratesOk(){
        final MockJsonResources res = new MockJsonResources(
            req -> {
                final URI uri = req.getUri();
                final JsonObjectBuilder builder = Json.createObjectBuilder();
                final Map<String, List<String>> headers;
                if (uri.equals(URI.create("http://localhost"))) {
                    builder.add("page", 1);
                    headers = Map.of(
                        "Link",
                        List.of("<http://localhost?page=2> rel=\"next\"")
                    );
                } else if (uri.equals(URI.create("http://localhost?page=2"))) {
                    builder.add("page", 2);
                    headers = Map.of(
                        "Link",
                        List.of("<http://localhost?page=3> rel=\"next\"")
                    );
                } else {
                    builder.add("page", 3);
                    headers = Map.of();
                }
                return new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    builder.build(),
                    headers
                );
            }
        );

        final ResourcePaging paging = new ResourcePaging.FromHeaders(
            res,
            URI.create("http://localhost")
        );
        final List<JsonObject> pages = paging
            .stream()
            .map(Resource::asJsonObject)
            .collect(Collectors.toList());

        MatcherAssert.assertThat(
            pages,
            Matchers.equalTo(
                List.of(
                    Json.createObjectBuilder().add("page", 1).build(),
                    Json.createObjectBuilder().add("page", 2).build(),
                    Json.createObjectBuilder().add("page", 3).build()
                )
            )
        );
        MatcherAssert.assertThat(
            StreamSupport.stream(res.requests().spliterator(), false)
                .filter(req -> req.getHeaders().containsKey("Cache-Control")
                    && req.getHeaders().containsValue(List.of("no-cache"))
                ).collect(Collectors.toList()),
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * ResourcePaging.FromHeaders throws {@link NoSuchElementException} when
     * calling next link without checking with hastNext.
     */
    @Test(expected = NoSuchElementException.class)
    public void fromHeadersThrowsWhenThereIsNoNextLink(){
        final MockJsonResources res = new MockJsonResources(
            req -> {
                final JsonObjectBuilder builder = Json.createObjectBuilder();
                return new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    builder.build()
                );
            }
        );

        final ResourcePaging paging = new ResourcePaging.FromHeaders(
            res,
            URI.create("http://localhost")
        );

        final Iterator<Resource> iterator = paging.iterator();
        iterator.next();
        iterator.next();
    }

    /**
     * ResourcePaging.FromHeaders throws {@link IllegalStateException} and
     * interrupts pagination if the status code of one of Resources is not
     * {@link HttpURLConnection#HTTP_OK}.
     */
    @Test(expected = IllegalStateException.class)
    public void fromHeadersThrowsWhenStatusIsNotOk(){
        final MockJsonResources res = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );

        final ResourcePaging paging = new ResourcePaging.FromHeaders(
            res,
            URI.create("http://localhost")
        );

        paging.iterator().next();
    }

}