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

import com.selfxdsd.api.Commit;
import com.selfxdsd.api.Commits;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Commits in a Bitbucket Repo.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
final class BitbucketCommits implements Commits {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketCommits.class
    );

    /**
     * Bitbucket repo Commits base uri.
     */
    private final URI commitsUri;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Bitbucket's JSON Resources.
     * @param commitsUri Commits base URI.
     * @param storage Storage.
     */
    BitbucketCommits(
        final JsonResources resources,
        final URI commitsUri,
        final Storage storage
    ) {
        this.resources = resources;
        this.commitsUri = commitsUri;
        this.storage = storage;
    }

    @Override
    public Commit getCommit(final String ref) {
        LOG.debug(
            "Getting commit [" + ref + "] from ["
            + this.commitsUri + "..."
        );
        final URI commitUri = this.createCommitUri(ref);
        final Resource resource = this.resources.get(commitUri);
        final JsonObject jsonObject;
        switch (resource.statusCode()) {
            case HttpURLConnection.HTTP_OK:
                jsonObject = resource.asJsonObject();
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                jsonObject = null;
                break;
            default:
                LOG.error(
                    "Could not get the commit " + ref + ". "
                    + "Received status code: " + resource.statusCode() + "."
                );
                throw new IllegalStateException(
                    "Could not get the commit " + ref + ". "
                    + "Received status code: " + resource.statusCode() + "."
                );
        }
        final Commit commit;
        if(jsonObject != null){
            LOG.debug(String.format("Commit [%s] found!", ref));
            commit = new BitbucketCommit(
                commitUri,
                jsonObject,
                this.storage,
                this.resources
            );
        } else {
            LOG.debug("Commit [" + ref + "] not found, returning null.");
            commit = null;
        }
        return commit;
    }

    @Override
    public Commit latest() {
        final Resource resource = this.resources
            .get(URI.create(this.commitsUri + "?pagelen=1"));
        final Commit latest;
        if (resource.statusCode() == HttpURLConnection.HTTP_OK) {
            final JsonObject latestCommitJson = resource
                .asJsonObject()
                .getJsonArray("values")
                .getJsonObject(0);
            latest = new BitbucketCommit(
                this.createCommitUri(latestCommitJson.getString("hash")),
                latestCommitJson,
                this.storage,
                this.resources
            );
        } else {
            throw new IllegalStateException(
                "Fetch repo commits returned " + resource.statusCode()
                + ", expected 200 OK. URI is [" + this.commitsUri.toString()
                + "]."
            );
        }
        return latest;
    }

    @Override
    public Iterator<Commit> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all the Commits in a Repo."
        );
    }

    /**
     * Creates the URI for a Bitbucket commit based on ref (hash).
     * @param ref Hash.
     * @return URI.
     */
    private URI createCommitUri(final String ref) {
        final String commitsUriStr = this.commitsUri.toString();
        // Uri for commit is /<owner>/<name>/commit/<hash>
        // we only need to drop the "s" from "commits" part of commitsUriStr
        // and add the hash/ref at the end.
        final String commitUriStr = commitsUriStr
            .substring(0, commitsUriStr.length() -1) + "/" +ref;
        return URI.create(commitUriStr);
    }
}