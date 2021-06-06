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

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Comments of a Commit on Bitbucket.<br>
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
final class BitbucketCommitComments implements Comments {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketCommitComments.class
    );

    /**
     * Base Comments uri.
     */
    private final URI commentsUri;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     *
     * @param commentsUri Commit's comments URI.
     * @param resources Bitbucket's JSON Resources.
     */
    BitbucketCommitComments(
        final URI commentsUri,
        final JsonResources resources
    ) {
        this.commentsUri = commentsUri;
        this.resources = resources;
    }

    @Override
    public Comment post(final String body) {
        LOG.debug("Posting Commit Comment to: [" + this.commentsUri + "].");
        final Resource resource = this.resources.post(
            this.commentsUri,
            Json.createObjectBuilder()
                .add("content", Json.createObjectBuilder()
                    .add("raw", body)
                    .build())
                .build()
        );
        if (resource.statusCode() == HttpURLConnection.HTTP_CREATED) {
            return new BitbucketComment(resource.asJsonObject());
        } else {
            LOG.error(
                "Expected status 201 CREATED, but got: ["
                + resource.statusCode() + "]."
            );
            throw new IllegalStateException(
                "Bitbucket Commit Comment was not created. Status is "
                + resource.statusCode() + "."
            );
        }
    }

    @Override
    public Comment received(final JsonObject comment) {
        return new BitbucketComment(comment);
    }

    @Override
    public Iterator<Comment> iterator() {
        throw new UnsupportedOperationException(
            "Can't iterate over all the comments of a commit."
        );
    }
}