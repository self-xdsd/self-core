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
import com.selfxdsd.api.SelfTodos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Restful self-todos.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.33
 */
final class RestfulSelfTodos implements SelfTodos {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        RestfulSelfTodos.class
    );


    /**
     * Self-todos's URI.
     */
    private final URI uri;

    /**
     * JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     */
    RestfulSelfTodos() {
        this(
            URI.create("http://localhost:8282"),
            new JsonResources.JdkHttp()
        );
    }

    /**
     * Ctor.
     * @param baseUri Base URI of self-todos.
     * @param resources Json Resources.
     */
    RestfulSelfTodos(final URI baseUri, final JsonResources resources) {
        this.uri = baseUri;
        this.resources = resources;
    }

    @Override
    public void post(final Project project, final String push) {
        final String provider = project.provider();
        final String repoFullName = project.repoFullName();
        final URI pdd = URI.create(
            this.uri.toString() + "/pdd/" + provider + "/" + repoFullName
        );
        LOG.debug(
            "Posting PUSH event from " + repoFullName + " at " + provider + " "
            + "to self-todos at [" + pdd.toString() + "]..."
        );
        final Resource response = this.resources.post(
            pdd,
            Json.createReader(new StringReader(push)).readObject()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_OK) {
            LOG.debug("Post successful!");
        } else {
            LOG.warn(
                "Post received response status code " + response.statusCode()
            );
        }
    }
}
