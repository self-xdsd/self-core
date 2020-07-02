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
package com.selfxdsd.api;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Language spoken in a Project.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 * @checkstyle ReturnCount (100 lines)
 */
public abstract class Language {

    /**
     * Commands that the agent can understand, in a given language.
     */
    private final Properties commands = new Properties();

    /**
     * Responses that the agent can give, in a given language.
     */
    private final Properties responses = new Properties();

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
        return "unknown";
    }

    /**
     * Get the PMs reply.
     * @param key Key in the properties file.
     * @return String reply or null if nothing is found.
     */
    public final String reply(final String key) {
        return this.responses.getProperty(key);
    }

}
