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
package com.selfxdsd.core;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.api.User;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Base implementation of {@link com.selfxdsd.api.Repo}.
 * "Rt" stands for "Runtime"
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
abstract class BaseRepo implements Repo {

    /**
     * Owner of this repository.
     */
    private final User owner;

    /**
     * This repo's info in JSON.
     */
    private JsonObject json;

    /**
     * URI pointing to this repo.
     */
    private final URI uri;

    /**
     * Storage used for activation.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param owner Owner of this repo.
     * @param repo URI Pointing to this repo.
     * @param storage Storage used for activation.
     */
    BaseRepo(final User owner, final URI repo, final Storage storage) {
        this.owner = owner;
        this.uri = repo;
        this.storage = storage;
    }

    @Override
    public User owner() {
        return this.owner;
    }

    @Override
    public JsonObject json() {
        if(this.json == null) {
            try {
                final HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                        HttpRequest.newBuilder()
                            .uri(this.uri)
                            .header("Content-Type", "application/json")
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    );
                final int status = response.statusCode();
                if(status == HttpURLConnection.HTTP_OK) {
                    this.json =  Json.createReader(
                        new StringReader(response.body())
                    ).readObject();
                } else {
                    throw new IllegalStateException(
                        "Unexpected response when fetching [" + this.uri +"]. "
                            + "Expected 200 OK, but got " + status + "."
                    );
                }
            } catch (final IOException | InterruptedException ex) {
                throw new IllegalStateException(
                    "Couldn't fetching repo + [" + this.uri.toString() +"]",
                    ex
                );
            }
        }
        return this.json;
    }

    /**
     * Get the Storage.
     * @return Storage.
     */
    Storage storage() {
        return this.storage;
    }

    /**
     * Get the URI.
     * @return URI.
     */
    URI repoUri() {
        return this.uri;
    }

    @Override
    public abstract Project activate();

    @Override
    public String provider() {
        return owner().provider().name();
    }

}
