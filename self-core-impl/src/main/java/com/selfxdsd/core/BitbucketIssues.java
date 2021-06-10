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

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Issues in a Bitbucket repository.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.69
 *
 * @todo #1013:60min Continue the implementation & tests of open()
 *  and search() methods.
 */
final class BitbucketIssues implements Issues {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketIssues.class
    );

    /**
     * Bitbucket repo issues base uri.
     */
    private final URI issuesUri;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Parent Repo.
     */
    private final Repo repo;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Bitbucket's JSON Resources.
     * @param issuesUri Issues base URI.
     * @param repo Parent repo.
     * @param storage Storage.
     */
    BitbucketIssues(
        final JsonResources resources,
        final URI issuesUri,
        final Repo repo,
        final Storage storage
    ) {
        this.resources = resources;
        this.issuesUri = issuesUri;
        this.repo = repo;
        this.storage = storage;
    }

    @Override
    public Issue getById(final String issueId) {
        LOG.debug("Getting Bitbucket issue with id " + issueId + "...");
        final URI issueUri = URI.create(
            this.issuesUri.toString() + "/" + issueId
        );
        final Resource resource = this.resources.get(issueUri);
        JsonObject jsonObject;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_OK:
                jsonObject = resource.asJsonObject();
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
            case HttpURLConnection.HTTP_GONE:
                jsonObject = null;
                break;
            default:
                throw new IllegalStateException(
                    "Could not get the issue " + issueId + ". "
                        + "Received status code: " + resource.statusCode()
                );
        }
        Issue issue = null;
        if(jsonObject != null){
            issue = new BitbucketIssue(
                issueUri,
                jsonObject,
                this.storage,
                this.resources
            );
        }
        return issue;
    }

    @Override
    public Issue received(final JsonObject issue) {
        return new BitbucketIssue(
            URI.create(
                this.issuesUri.toString() + "/" + issue.getInt("id")
            ),
            issue,
            this.storage,
            this.resources
        );
    }

    @Override
    public Issue open(
        final String title,
        final String body,
        final String... labels
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Issues search(final String text, final String... labels) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterator<Issue> iterator() {
        throw new IllegalStateException(
            "You cannot iterate over all the issues in a repo."
        );
    }

}
