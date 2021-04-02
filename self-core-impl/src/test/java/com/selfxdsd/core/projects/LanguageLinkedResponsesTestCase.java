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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Language;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Unit tests for {@link Language} linked responses.
 * @author criske
 * @version $Id$
 * @since 0.0.72
 */
public final class LanguageLinkedResponsesTestCase {

    /**
     * Language can follow a resource linked from responses properties entry
     * value.
     */
    @Test
    public void shouldReadReplyFromClasspathLink() {
        final Language english = new English();
        MatcherAssert.assertThat(
            english.reply("commands.comment"),
            Matchers.startsWith(
                "* ``Hello`` -- any comment addressed"
            )
        );
    }

    /**
     * Language can follow a file linked from responses properties entry value.
     * @throws IOException if something goes wrong.
     * @checkstyle JavadocType (40 lines).
     * @checkstyle JavadocMethod (40 lines).
     */
    @Test
    public void shouldReadReplyFromFileLink() throws IOException {
        final Path file = Files.createTempFile("self-lang-comment-link",
            "test");
        Files.writeString(file, "* ``Hello`` -- any comment addressed");
        final Properties commands = new Properties();
        commands.putIfAbsent(
            "commands.comment",
            "file:///" + file.toAbsolutePath()
        );
        class TestLanguage extends Language {
            TestLanguage() {
                super(new Properties(), commands);
            }
        }
        final Language language = new TestLanguage();
        MatcherAssert.assertThat(
            language.reply("commands.comment"),
            Matchers.startsWith(
                "* ``Hello`` -- any comment addressed"
            )
        );
        Files.delete(file);
    }

    /**
     * Language can follow a http link from responses properties entry value.
     * @checkstyle JavadocType (40 lines).
     * @checkstyle JavadocMethod (40 lines).
     */
    @Test
    public void shouldReadReplyFromHttpLink() {
        final Properties commands = new Properties();
        commands.putIfAbsent(
            "commands.comment",
            "https://raw.githubusercontent.com/self-xdsd/self-docs/"
                + "master/projectmanager.md"
        );
        class TestLanguage extends Language {
            TestLanguage() {
                super(new Properties(), commands);
            }
        }
        final Language language = new TestLanguage();
        MatcherAssert.assertThat(
            language.reply("commands.comment"),
            Matchers.startsWith("---")
        );
    }
}