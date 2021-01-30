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
 * A Commit in Github.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.31
 */
final class GithubCommit implements Commit {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubCommit.class
    );

    /**
     * Commit base uri.
     */
    private final URI commitUri;

    /**
     * Commit JSON as returned by Github's API.
     */
    private final JsonObject json;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param commitUri Commit base URI.
     * @param json Json Commit as returned by Github's API.
     * @param storage Storage.
     * @param resources Github's JSON Resources.
     */
    GithubCommit(
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
        return new GithubCommitComments(this.commitUri, this.resources);
    }

    @Override
    public String author() {
        final String username;
        final JsonObject author = this.json.getJsonObject("author");
        if(author == null) {
            username = "";
        } else {
            username = author.getString("login", "");
        }
        return username;
    }

    @Override
    public String shaRef() {
        return this.json.getString("sha");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
