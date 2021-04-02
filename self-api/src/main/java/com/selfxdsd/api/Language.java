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
package com.selfxdsd.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Language spoken in a Project.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 * @checkstyle ReturnCount (100 lines)
 * @todo #1071:60min Continue writing unit tests
 *  for {@link Language#tryReplyFromLink(String)} in order to ensure complete
 *  test coverage.
 */
public abstract class Language {

    /**
     * Commands that the agent can understand, in a given language.
     */
    private final Properties commands;

    /**
     * Responses that the agent can give, in a given language.
     */
    private final Properties responses;

    /**
     * Constructor. These two files should be in self-pm, so we don't have
     * to release and rebuild self-core every time we want to add a new command
     * or reply.
     * @param commandsFileName Name of the file containing the commands.
     * @param responsesFileName Name of the file containing the replies.
     */
    public Language(
        final String commandsFileName,
        final String responsesFileName
    ) {
        this(new Properties(), new Properties());
        try {
            this.commands.load(
                this.getClass().getClassLoader()
                    .getResourceAsStream(commandsFileName)
            );
            this.responses.load(
                this.getClass().getClassLoader()
                    .getResourceAsStream(responsesFileName)
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Problem while loading the properties files of Language",
                ex
            );
        }
    }

    /**
     * Generic constructor from existing command and responses properties.
     * Should be used only in tests.
     * @param commands Commands properties.
     * @param responses Responses properties.
     */
    protected Language(
        final Properties commands,
        final Properties responses
    ){
        this.commands = commands;
        this.responses = responses;
    }

    /**
     * Categorize a command that the PM has received.
     * @param command Command text.
     * @return String category.
     */
    public final String categorize(final String command) {
        Set<Object> keys = this.commands.keySet();
        for(final Object key : keys) {
            String keyString = (String) key;
            String[] words = this.commands
                .getProperty(keyString, "")
                .split("\\^");
            boolean match = true;
            for(final String word : words) {
                if(!command.contains(word.trim())) {
                    match = false;
                }
            }
            if(match) {
                return keyString.split("\\.")[0];
            }
        }
        return "confused";
    }

    /**
     * Get the PMs reply.
     * @param key Key in the properties file.
     * @return String reply or null if nothing is found.
     */
    public final String reply(final String key) {
        final String reply = this.responses.getProperty(key);
        return tryReplyFromLink(reply);
    }

    /**
     * Try to follow the link from reply. If the reply is not a valid
     * link (not starting with the right protocol), it will fallback
     * to the reply as string.
     * <br>
     * Supported protocols:
     * <ul>
     *     <li>
     *         <b>Classpath</b> loads the file from resources:
     *         <br>
     *         <code>
     *             commands.comment=classpath:replies/commands.comment_en.md
     *         </code>
     *     </li>
     *     <li>
     *         <b>File</b> loads the file from file system by absolute path:
     *         <br>
     *         <code>
     *             commands.comment=file:///<full_path>/commands.comment_en.md
     *         </code>
     *     </li>
     *     <li>
     *         <b>Http(s)</b> is doing a http(s) request:
     *         <br>
     *         <code>
     *             commands.comment=http://my.docs.com/commands/comment
     *         </code>
     *     </li>
     * </ul>
     *
     * If something goes wrong like IO exception, bad request etc...,
     * it will return null.
     *
     * @param linkedReply Reply as link.
     * @return Actual reply fetched from link or null if something goes wrong.
     * @checkstyle CyclomaticComplexity (60 lines).
     * @checkstyle BooleanExpressionComplexity (20 lines).
     */
    private String tryReplyFromLink(final String linkedReply){
        final boolean isLinked = linkedReply != null
            && (linkedReply.startsWith("http://")
            || linkedReply.startsWith("https://")
            || linkedReply.startsWith("file://")
            || linkedReply.startsWith("classpath:")
        );
        String reply = null;
        if (isLinked) {
            URL url = null;
            if (linkedReply.startsWith("classpath:")) {
                if (linkedReply.length() > "classpath:".length()) {
                    final String path = linkedReply
                        .split("classpath:")[1];
                    url = this.getClass().getClassLoader().getResource(path);
                }
            } else {
                try {
                    url = new URL(linkedReply);
                } catch (final MalformedURLException exception) {
                    exception.printStackTrace();
                    //no-op
                }
            }
            if (url != null) {
                try {
                    URLConnection connection = url.openConnection();
                    try (
                        final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))
                    ) {
                        reply = reader.lines().collect(Collectors
                            .joining(System.lineSeparator()));
                    } catch (final IOException exception) {
                        exception.printStackTrace();
                        //no-op
                    }
                } catch (final IOException exception) {
                    exception.printStackTrace();
                    //no-op
                }
            }
        }else{
            //there is no link => fallback
            reply = linkedReply;
        }
        return reply;
    }

}
