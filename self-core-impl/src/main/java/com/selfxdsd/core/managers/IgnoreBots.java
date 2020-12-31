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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.ProjectManagers;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Conversation where the PM ignores comments coming from other
 * PMs and known bots.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.25
 */
public final class IgnoreBots implements Conversation {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        IgnoreBots.class
    );

    /**
     * Other known bots, besides our PMs.
     */
    private final List<KnownBot> others = Arrays.asList(
        new KnownBot("rultor"),
        new KnownBot("0pdd"),
        new KnownBot("0crat"),
        new KnownBot("dependabot"),
        new KnownBot("coveralls")
    );

    /**
     * Next conversation.
     */
    private final Conversation next;

    /**
     * Ctor.
     * @param next Next in the conversation chain.
     */
    public IgnoreBots(final Conversation next) {
        this.next = next;
    }

    @Override
    public Step start(final Event event) {
        final Step steps;
        final String author = event.comment().author();
        final String provider = event.project().provider();
        if(this.others.contains(new KnownBot(author, provider))) {
            steps = ignore -> LOG.debug(
                "Comment comes from @" + author + " at " + provider
                + ", who is a known chatbot. Ignoring."
            );
        } else {
            final ProjectManagers managers = event
                .project().storage().projectManagers();
            if(managers.getByUsername(author, provider) != null) {
                steps = ignore -> LOG.debug(
                    "Comment comes from @" + author + " at " + provider
                    + ", who is a known chatbot PM in Self. Ignoring."
                );
            } else {
                steps = this.next.start(event);
            }
        }
        return steps;
    }

    /**
     * Known chatbot.
     */
    private static final class KnownBot {

        /**
         * Username.
         */
        private final String username;

        /**
         * Provider (github, gitlab etc).
         */
        private final String provider;

        /**
         * Ctor. By default, it will be a Github bot.
         * @param username Username.
         */
        KnownBot(final String username) {
            this(username, Provider.Names.GITHUB);
        }

        /**
         * Ctor.
         * @param username Username.
         * @param provider Provider.
         */
        KnownBot(final String username, final String provider) {
            this.username = username;
            this.provider = provider;
        }

        /**
         * Get the username.
         * @return String.
         */
        String username() {
            return this.username;
        }

        /**
         * Get the provider.
         * @return String.
         */
        String provider() {
            return this.provider;
        }

        @Override
        public boolean equals(final Object other) {
            final boolean result;
            if (this == other) {
                result = true;
            } else if (other == null || getClass() != other.getClass()) {
                result = false;
            } else {
                final KnownBot knownBot = (KnownBot) other;
                result = Objects.equals(username, knownBot.username)
                    && Objects.equals(provider, knownBot.provider);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, provider);
        }
    }
}
