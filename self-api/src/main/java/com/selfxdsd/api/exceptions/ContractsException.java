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
package com.selfxdsd.api.exceptions;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contributor;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for Contracts Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class ContractsException extends SelfException {

    /**
     * Self Exceptions for Contracts Project.
     */
    public abstract static class OfProject extends ContractsException {

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
            return "Project " + this.repoFullName
                +", operating at " + this.provider;
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
         * Add Contract Project Self exception.
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
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot register another Project's "
                    + "contracts here. ";
            }
        }

        /**
         * Update Contract Project Self exception.
         */
        public static final class Update extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project's repo full name.
             * @param provider Project's provider.
             */
            public Update(final String repoFullName, final String provider) {
                super(repoFullName, provider);
            }

            @Override
            String getSelfMessage() {
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot update another Project's "
                    + "contract here. ";
            }
        }

        /**
         * Delete Contract Project Self exception.
         */
        public static final class Delete extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project's repo full name.
             * @param provider Project's provider.
             */
            public Delete(final String repoFullName, final String provider) {
                super(repoFullName, provider);
            }

            @Override
            String getSelfMessage() {
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot delete (or mark for removal) another"
                    +  " Project's Contract here. ";
            }
        }

        /**
         * Iterating Project's Contracts Self exception.
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
                return "Already seeing the contracts of "
                    + super.getSelfMessage() + ".";
            }
        }
    }

    /**
     * Self Exceptions for Contracts Contributor.
     */
    public abstract static class OfContributor extends ContractsException {

        /**
         * Contributor.
         */
        private final Contributor contributor;

        /**
         * Ctor.
         * @param contributor Contributor.
         */
        private OfContributor(final Contributor contributor) {
            this.contributor = contributor;
        }

        @Override
        String getSelfMessage() {
            return "Contributor " + this.contributor.username()
                +", working at " + this.contributor.provider();
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("/username", this.contributor.username())
                .add("/provider", this.contributor.provider())
                .build()
                .apply(super.json());
        }

        /**
         * Add Contract Project Self exception.
         */
        public static final class Add extends OfContributor {

            /**
             * Ctor.
             * @param contributor Contributor.
             */
            public Add(final Contributor contributor) {
                super(contributor);
            }

            @Override
            String getSelfMessage() {
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot register another Contributor's "
                    + "contracts here. ";
            }
        }

        /**
         * Update Contract Self exception.
         */
        public static final class Update extends OfContributor {

            /**
             * Ctor.
             * @param contributor Contributor.
             */
            public Update(final Contributor contributor) {
                super(contributor);
            }

            @Override
            String getSelfMessage() {
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot update another Contributor's "
                    + "contract here. ";
            }
        }

        /**
         * Delete Contract Self exception.
         */
        public static final class Delete extends OfContributor {

            /**
             * Ctor.
             * @param contributor Contributor.
             */
            public Delete(final Contributor contributor) {
                super(contributor);
            }

            @Override
            String getSelfMessage() {
                return "These are the Contracts of " + super.getSelfMessage()
                    + ". You cannot delete (or mark for removal) another "
                    + "Contributor's contract here. ";
            }
        }

        /**
         * Iterating Project's Contracts Self exception.
         */
        public static final class List extends OfContributor {

            /**
             * Ctor.
             * @param contributor Contributor.
             */
            public List(final Contributor contributor) {
                super(contributor);
            }

            @Override
            String getSelfMessage() {
                return "Already seeing the contracts of "
                    + super.getSelfMessage() + ".";
            }
        }

    }

    /**
     * Self exception for a single Contract.
     */
    public abstract static class Single extends ContractsException {

        /**
         * Contract's id.
         */
        private final Contract.Id contractId;

        /**
         * Ctor.
         * @param contractId Contract's id.
         */
        private Single(final Contract.Id contractId) {
            this.contractId = contractId;
        }

        @Override
        String getSelfMessage() {
            return "Contract ["
                + this.contractId.getContributorUsername() + ","
                + this.contractId.getRepoFullName() + ","
                + this.contractId.getProvider() + ","
                + this.contractId.getRole() + ","
                + "]";
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("/id", Json.createObjectBuilder()
                    .add("contributorUsername", this.contractId
                        .getContributorUsername())
                    .add("repoFullName", this.contractId
                        .getRepoFullName())
                    .add("provider", this.contractId.getProvider())
                    .add("role", this.contractId.getRole()).build())
                .build()
                .apply(super.json());
        }

        /**
         * Self exception for duplicate Contract.
         */
        public static final class Add extends Single {

            /**
             * Message.
             */
            private final String message;

            /**
             * Ctor.
             * @param contractId Contract id.
             * @param message Message.
             */
            public Add(final Contract.Id contractId,
                       final String message) {
                super(contractId);
                this.message = message;
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " " + this.message;
            }
        }

        /**
         * Self exception for not found Contract.
         */
        public static final class NotFound extends Single {

            /**
             * Ctor.
             * @param contractId Contract id.
             */
            public NotFound(final Contract.Id contractId) {
                super(contractId);
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " was not found.";
            }
        }
    }

}
