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
package com.selfxdsd.api.exceptions;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for Contributors Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class ContributorsException extends SelfException {

    /**
     * Self Exceptions for Contributors Project.
     */
    public abstract static class OfProject extends ContributorsException {

        /**
         * Project's repo full name.
         */
        private final String repoFullName;

        /**
         * Project's provider.
         */
        private final String provider;

        /**
         * Ctor.
         * @param repoFullName Project's repo full name.
         * @param provider Project's provider.
         */
        private OfProject(final String repoFullName, final String provider) {
            this.repoFullName = repoFullName;
            this.provider = provider;
        }

        @Override
        String getSelfMessage() {
            return "Project [" + this.repoFullName + ", " + this.provider +"]";
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("/repoFullName", this.repoFullName)
                .add("/provider", this.provider)
                .build()
                .apply(super.json());
        }

        /**
         * Add Contributor Project Self exception.
         */
        public static final class Add extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project's repo full name.
             * @param provider Project's provider.
             */
            public Add(final String repoFullName, final String provider) {
                super(repoFullName, provider);
            }

            @Override
            String getSelfMessage() {
                return "You can only register contributors working at "
                    + super.getSelfMessage() + " provider.";
            }
        }

        /**
         * Iterating Project's Contributors Self exception.
         */
        public static final class List extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project's repo full name.
             * @param provider Project's provider.
             */
            public List(final String repoFullName, final String provider) {
                super(repoFullName, provider);
            }

            @Override
            String getSelfMessage() {
                return "Already seeing the contributors of "
                    + super.getSelfMessage() + ".";
            }
        }

        /**
         * Contributor Project Election Self Exception.
         */
        public static final class Election extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project's repo full name.
             * @param provider Project's provider.
             */
            public Election(final String repoFullName, final String provider) {
                super(repoFullName, provider);
            }

            @Override
            String getSelfMessage() {
                return "Contributors project must match "
                    + " the task " + super.getSelfMessage() + ".";
            }
        }

    }


    /**
     * Self Exceptions for Contributors Provider.
     */
    public abstract static class OfProvider extends ContributorsException {

        /**
         * Provider.
         */
        private final String provider;

        /**
         * Ctor.
         * @param provider Provider.
         */
        protected OfProvider(final String provider) {
            this.provider = provider;
        }

        @Override
        String getSelfMessage() {
            return "Provider[" + this.provider + "]";
        }

        /**
         * Add Provider Contributor Self exception.
         */
        public static class Add extends OfProvider {

            /**
             * Ctor.
             * @param provider Provider.
             */
            public Add(final String provider) {
                super(provider);
            }

            @Override
            String getSelfMessage() {
                return "You can only register contributors of "
                    + super.getSelfMessage();
            }
        }

        /**
         * Iterating Provider's Contributors Self exception.
         */
        public static class List extends OfProvider {

            /**
             * Ctor.
             * @param provider Provider.
             */
            public List(final String provider) {
                super(provider);
            }

            @Override
            String getSelfMessage() {
                return "Already visited ProviderContributors of "
                    + super.getSelfMessage();
            }
        }


    }

    /**
     * Self exception for a single Contributor.
     */
    public abstract static class Single extends ContributorsException {

        /**
         * Contributor's user name.
         */
        private final String username;

        /**
         * Contributor's provider.
         */
        private final String provider;

        /**
         * Ctor.
         * @param username Contributor's user name.
         * @param provider Contributor's provider.
         */
        private Single(final String username, final String provider) {
            this.username = username;
            this.provider = provider;
        }

        @Override
        String getSelfMessage() {
            return "Contributor [" + this.username + "," + this.provider + "]";
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("/username", this.username)
                .add("/provider", this.provider)
                .build()
                .apply(super.json());
        }

        /**
         * Self exception for duplicate Contributor.
         */
        public static final class Add extends Single {

            /**
             * Ctor.
             * @param username Contributor's user name.
             * @param provider Contributor's provider.
             */
            public Add(final String username, final String provider) {
                super(username, provider);
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " " + "already exists.";
            }
        }

        /**
         * Self exception for not found Contributor.
         */
        public static final class NotFound extends Single {

            /**
             * Ctor.
             * @param username Contributor's user name.
             * @param provider Contributor's provider.
             */
            public NotFound(final String username, final String provider) {
                super(username, provider);
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " was not found.";
            }
        }
    }

    /**
     * Contributors election Self exception.
     */
    public static final class Election extends ContributorsException {

        @Override
        String getSelfMessage() {
            return "You can only elect a Contributor out of a "
                + "Project's contributors. Call #ofProject(...) first.";
        }
    }

    /**
     * Iterating all Contributors Self exception.
     */
    public static class List extends ContributorsException {

        @Override
        String getSelfMessage() {
            return "You can't iterate over all contributors. Use"
                + " ProviderContributors instead.";
        }
    }
}
