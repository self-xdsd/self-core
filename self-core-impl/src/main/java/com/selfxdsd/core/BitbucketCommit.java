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

import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Commit;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.net.URI;

/**
 * A Commit in Bitbucket.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 * @todo #1017:60min Start implement and test BitbucketComments used by
 *  BitbucketCommit.
 */
final class BitbucketCommit implements Commit {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketCommit.class
    );

    /**
     * Commit base uri.
     */
    private final URI commitUri;

    /**
     * Commit JSON as returned by Bitbucket's API.
     */
    private final JsonObject json;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param commitUri Commit base URI.
     * @param json Json Commit as returned by Bitbucket's API.
     * @param storage Storage.
     * @param resources Bitbucket's JSON Resources.
     */
    BitbucketCommit(
        final URI commitUri,
        final JsonObject json,
        final Storage storage,
        final JsonResources resources
    ) {
        this.commitUri = commitUri;
        this.json = json;
        this.storage = storage;
        this.resources = resources;
    }

    @Override
    public Comments comments() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * {@inheritDoc}
     * <br/>
     * Since Bitbucket 2.0 doesn't reveal the `username` anymore in their json
     * responses, we will extract the `account_id` as username from `author`
     * section.
     */
    @Override
    public String author() {
        return this.json
            .getJsonArray("values")
            .getJsonObject(0)
            .getJsonObject("author")
            .getString("account_id");
    }

    @Override
    public String shaRef() {
        return this.json
            .getJsonArray("values")
            .getJsonObject(0)
            .getString("hash");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}