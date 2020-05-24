/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.issues;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;
import java.net.URI;

/**
 * An Issue in a Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #97:30min Write unit tests for this class
 *  and for GithubRepo.issues.
 */
public final class GithubIssue implements Issue {

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
     * Ctor.
     * @param issueUri Issues base URI.
     * @param json Json Issue as returned by Github's API.
     * @param storage Storage.
     */
    public GithubIssue(
        final URI issueUri,
        final JsonObject json,
        final Storage storage
    ) {
        this.issueUri = issueUri;
        this.json = json;
        this.storage = storage;
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
