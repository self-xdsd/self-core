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

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Stars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Gitlab stars.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.42
 * @todo #1153:60min Provide an implementation for GitlabStars#isStarred().
 */
final class GitlabStars implements Stars {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
            GitlabStars.class
    );

    /**
     * Gitlab's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Gitlab's URI for staring a project.
     */
    private final URI starUri;

    /**
     * Gitlab repo.
     */
    private final Repo repo;

    /**
     * Ctor.
     *
     * @param resources Gitlab's JSON Resources.
     * @param starUri Gitlab's project star URI.
     * @param repo Gitlab repo
     */
    GitlabStars(
            final JsonResources resources,
            final URI starUri,
            final Repo repo
    ) {
        this.resources = resources;
        this.starUri = starUri;
        this.repo = repo;
    }

    @Override
    public boolean add() {
        LOG.debug(
            String.format(
                "Starring Gitlab repository [%s]",
                repo.fullName()
            )
        );
        final boolean starred;
        final Resource response = this.resources.post(
                starUri,
                JsonValue.EMPTY_JSON_OBJECT
        );
        if (response.statusCode() == HttpURLConnection.HTTP_CREATED) {
            starred = true;
            LOG.debug(
                String.format(
                    "Repo [%s] was successfully starred.",
                    repo.fullName()
                )
            );
        } else {
            if (response.statusCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                starred = true;
                LOG.debug(
                    String.format(
                        "Repo [%s] is already starred.",
                        repo.fullName()
                    )
                );
            } else {
                starred = false;
                LOG.error(
                    String.format(
                        "Unexpected status code [%d] when starring repo [%s].",
                        response.statusCode(),
                        repo.fullName()
                    )
                );
            }
        }
        return starred;
    }

    @Override
    public boolean added() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
