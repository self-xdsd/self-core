/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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

import com.selfxdsd.api.Self;
import com.selfxdsd.api.Storage;
import com.selfxdsd.api.User;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URL;

/**
 * Unit tests for {@link GithubSelf}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubSelfTestCase {

    /**
     * GithubSelf can authenticate a given user.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void authenticatesUser() throws Exception {
        final Storage storage = new InMemory();
        final Self self = new GithubSelf(
            "amihaiemil", "amihaiemil@gmail.com",
            new URL("https://gravatar.com/amihaiemil"), "gh123token",
            storage
        );
        final User amihaiemil = self.authenticated();
        MatcherAssert.assertThat(
            amihaiemil.username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            storage.users(), Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.users().user("amihaiemil", "github").username(),
            Matchers.equalTo("amihaiemil")
        );
    }

}
