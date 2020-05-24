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
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.storage.Storage;

import java.net.URI;
import java.util.Iterator;

/**
 * Issues in a Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #97 Implement and unit test method json() here. It
 *  should call Github's API to fetch the Issue json and then
 *  use it to return a GithubIssue. If the API returns 404 or 204,
 *  then the method should return null.
 */
public final class GithubIssues implements Issues {

    /**
     * Github repo Issues base uri.
     */
    private URI issuesUri;

    /**
     * Self storage, in case we want to store something.
     */
    private Storage storage;

    /**
     * Ctor.
     * @param issuesUri Issues base URI.
     * @param storage Storage.
     */
    public GithubIssues(final URI issuesUri, final Storage storage) {
        this.issuesUri = issuesUri;
        this.storage = storage;
    }

    @Override
    public Issue getById(final int issueId) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Issue> iterator() {
        throw new IllegalStateException(
            "You cannot iterate over all the issues in a repo."
        );
    }
}
