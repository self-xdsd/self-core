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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GitlabCollaborator}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0p.0.49
 */
public final class GitlabCollaboratorTestCase {

    /**
     * Gitlab collaborator can return its id.
     */
    @Test
    public void canReturnItsId() {
        MatcherAssert.assertThat(
            new GitlabCollaborator(
                Json.createObjectBuilder().add("id", 1).build()
            ).collaboratorId(),
            Matchers.equalTo(1)
        );
    }

    /**
     * Gitlab collaborator can return its username.
     */
    @Test
    public void canReturnItsUsername() {
        MatcherAssert.assertThat(
            new GitlabCollaborator(
                Json.createObjectBuilder().add("username", "alilo").build()
            ).username(),
            Matchers.equalTo("alilo")
        );
    }

    /**
     * Gitlab collaborator can return its name.
     */
    @Test
    public void canReturnItsName() {
        MatcherAssert.assertThat(
            new GitlabCollaborator(
                Json.createObjectBuilder().add("name", "Ali Fellahi").build()
            ).name(),
            Matchers.equalTo("Ali Fellahi")
        );
    }

    /**
     * Gitlab collaborator can return its original json.
     */
    @Test
    public void canReturnItsJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        MatcherAssert.assertThat(
            new GitlabCollaborator(json).json(),
            Matchers.is(json)
        );
    }
}