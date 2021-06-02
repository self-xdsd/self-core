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

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Resource Pagination. Implementations should extract the next page link
 * either from headers or from body, depending on the Provider's API.
 *
 * <br/>
 * Usage for Github and Gitlab resources (for Bitbucket will not work because
 * next page link is inside json body not in response headers - well, it will
 * work but will not advance to the next page, so other implementation is
 * needed).
 * <br/>
 * Example to get all repos for a user that span over multiple pages.
 * <br/>
 * <pre>
 *     JsonResources res = ...
 *     Uri url = Uri.create("https://api.github.com/users/john/repos);
 *     ResourcePaging pages = new ResourcePaging.FromHeaders(res, url);
 *     for(Resource resource: pages){
 *         //do something with the json resource.
 *         //note: status code is guaranteed to be HTTP_OK otherwise pagination
 *         //throws exception.
 *     }
 *     //or using streams.
 *     pages.stream().forEach(resource -> {
 *        //do something with the json resource.
 *     })
 * </pre>
 *
 * The above example is equivalent to manually call:
 * <br/>
 * <pre>
 *    res.get(https://api.github.com/users/john/repos)
 *    res.get(https://api.github.com/users/john/repos?page=2)
 *    res.get(https://api.github.com/users/john/repos?page=3)
 *    //..
 *    res.get(https://api.github.com/users/john/repos?page=n)
 * </pre>
 * If there is only one page or "Link" is not present in response headers.
 * the above code will be equivalent with:
 * <pre>
 *    res.get(https://api.github.com/users/john/repos)
 * </pre>
 * @author criske
 * @version $Id$
 * @since 0.0.84
 */
interface ResourcePaging extends Iterable<Resource> {

    /**
     * Paging as stream.
     * <br/>
     * It assumes that this is a lazy stream so calling parallel() operator on
     * this might not work as it should.
     * <br/>
     * One way to make parallel working is to know how many pages there are.
     * It's up for implementations to figure this out if they needed.
     * @return Stream.
     */
    default Stream<Resource> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Resource pagination based on headers. This should be applied
     * to resources from Github and Gitlab.
     *
     * Extracts the next page from "Link" header entry.
     */
    class FromHeaders implements ResourcePaging {

        /**
         * Logger.
         */
        private static final Logger LOG = LoggerFactory.getLogger(
            FromHeaders.class
        );

        /**
         * Initial URI link.
         */
        private final URI initial;

        /**
         * Resources.
         */
        private final JsonResources resources;

        /**
         * Ctor.
         * @param resources Resources.
         * @param initial Initial URI link.
         */
        FromHeaders(final JsonResources resources, final URI initial) {
            this.resources = resources;
            this.initial = initial;
        }

        @Override
        public Iterator<Resource> iterator() {
            return new FromHeaderIterator();
        }

        /**
         * Iterator implementation.
         */
        private class FromHeaderIterator implements Iterator<Resource> {

            /**
             * Next link.
             */
            private URI next;

            /**
             * Has iteration started?
             */
            private boolean started;

            @Override
            public boolean hasNext() {
                return !started || next != null;
            }

            @Override
            public Resource next() {
                final URI link;
                if (next == null && !started) {
                    link = initial;
                } else {
                    link = this.next;
                }
                if (link == null) {
                    throw new NoSuchElementException("There is no next link");
                }
                LOG.debug("Fetching page from: {}", link);
                final Resource resource = resources.get(
                    link,
                    () -> Map.of("Cache-Control", List.of("no-cache"))
                );
                if (resource.statusCode() != HttpURLConnection.HTTP_OK) {
                    throw new IllegalStateException(
                        String.format(
                            "Couldn't get resource from %s."
                                + " Expected status 200 OK but got %d",
                            link,
                            resource.statusCode()
                        )
                    );
                }
                next = this.nextLink(resource.headers());
                LOG.debug("Next page is: {}", next);
                started = true;
                return resource;
            }

            /**
             * Extract next link.
             * @param headers Headers.
             * @return Uri or null if link was not found.
             */
            private URI nextLink(
                final Map<String, List<String>> headers
            ) {
                List<String> links = headers.getOrDefault(
                    "Link",
                    headers.get("link")
                );
                final URI next;
                if (links == null) {
                    next = null;
                } else {
                    next = links
                        .stream()
                        .filter(value -> value.contains("rel=\"next\""))
                        .map(value -> {
                            final int start = value.indexOf('<') + 1;
                            final int end = value.indexOf('>');
                            return URI.create(value.substring(start, end));
                        })
                        .findFirst()
                        .orElse(null);
                }
                return next;
            }
        }
    }
}