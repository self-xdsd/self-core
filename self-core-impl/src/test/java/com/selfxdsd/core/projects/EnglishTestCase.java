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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Language;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit tests for {@link English}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public final class EnglishTestCase {

    /**
     * English can tell us if it's a "hello" command.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void categorizesHelloCommand() throws IOException {
        final Language english = new English();
        MatcherAssert.assertThat(
            english.categorize("@zoeself hello there, who are you?"),
            Matchers.equalTo("hello")
        );
    }

    /**
     * English can tell us if it's a "hello" command.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void categorizesUnknownCommand() throws IOException {
        final Language english = new English();
        MatcherAssert.assertThat(
            english.categorize("@zoeself Can you help me?"),
            Matchers.equalTo("confused")
        );
    }

    /**
     * English can return a known reply.
     */
    @Test
    public void returnsKnownReply() {
        final Language english = new English();
        MatcherAssert.assertThat(
            english.reply("hello.comment"),
            Matchers.startsWith(
                "Hi @%s! I'm the Project Manager of this project."
            )
        );
    }

    /**
     * English returns null if the reply is not found.
     */
    @Test
    public void returnsNullForUnknownReply() {
        final Language english = new English();
        MatcherAssert.assertThat(
            english.reply("bla.comment"),
            Matchers.nullValue()
        );
    }

}
