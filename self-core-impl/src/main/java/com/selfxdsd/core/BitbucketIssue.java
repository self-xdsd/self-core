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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.net.URI;

/**
 * An Issue in a Bitbucket repository.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.69
 *
 * @todo #1013:60min Continue on the impl. & tests for BitbucketIssue class.
 */
final class BitbucketIssue implements Issue {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketIssue.class
    );

    /**
     * Issue base uri.
     */
    private final URI issueUri;

    /**
     * Issue JSON as returned by Github's API.
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
     * @param issueUri Issues base URI.
     * @param json Json Issue as returned by Github's API.
     * @param storage Storage.
     * @param resources Github's JSON Resources.
     */
    BitbucketIssue(
        final URI issueUri,
        final JsonObject json,
        final Storage storage,
        final JsonResources resources
    ) {
        this.issueUri = issueUri;
        this.json = json;
        this.storage = storage;
        this.resources = resources;
    }

    @Override
    public String issueId() {
        return String.valueOf(this.json.getInt("id"));
    }

    @Override
    public String provider() {
        return Provider.Names.BITBUCKET;
    }

    @Override
    public String role() {
        final String role;
        if(this.isPullRequest()) {
            role = Contract.Roles.REV;
        } else {
            role = Contract.Roles.DEV;
        }
        return role;
    }

    @Override
    public String repoFullName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String author() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String body() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String assignee() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean assign(final String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean unassign(final String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }

    @Override
    public Comments comments() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void reopen() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isPullRequest() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Estimation estimation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Labels labels() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
