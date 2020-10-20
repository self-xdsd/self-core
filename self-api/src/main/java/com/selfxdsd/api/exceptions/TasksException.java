/**
 * Copyright (c) 2020,
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

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for Tasks Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class TasksException extends SelfException {

    /**
     * Self Exceptions for Tasks of Project.
     */
    public abstract static class OfProject extends TasksException {

        /**
         * Project full name.
         */
        private final String repoFullName;

        /**
         * Project provider.
         */
        private final String repoProvider;

        /**
         * Ctor.
         * @param repoFullName Project full name;
         * @param repoProvider Project provider.
         */
        protected OfProject(final String repoFullName,
                            final String repoProvider) {
            this.repoFullName = repoFullName;
            this.repoProvider = repoProvider;
        }

        @Override
        String getSelfMessage() {
            return "Project Tasks ["
                + this.repoFullName + ", "
                + this.repoProvider + "]";
        }

        /**
         * Self Exception for Tasks of Project register.
         */
        public static class Add extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project full name;
             * @param repoProvider Project provider.
             */
            public Add(final String repoFullName,
                          final String repoProvider) {
                super(repoFullName, repoProvider);
            }

            @Override
            String getSelfMessage() {
                return "The given Issue does not belong to the "
                    + super.getSelfMessage();
            }
        }

        /**
         * Self Exception for Tasks of Project not found.
         */
        public static class NotFound extends OfProject {

            /**
             * Ctor.
             * @param repoFullName Project full name;
             * @param repoProvider Project provider.
             */
            public NotFound(final String repoFullName,
                       final String repoProvider) {
                super(repoFullName, repoProvider);
            }

            @Override
            String getSelfMessage() {
                return "This task is not part of "
                    + super.getSelfMessage();
            }
        }

        /**
         * Self Exception for Tasks of Project list.
         */
        public static class List extends OfProject {

            /**
             * Ctor.
             *
             * @param repoFullName Project full name.
             * @param repoProvider Project provider.
             */
            public List(final String repoFullName,
                           final String repoProvider) {
                super(repoFullName, repoProvider);
            }

            @Override
            String getSelfMessage() {
                return "Already seeing the tasks of " + super.getSelfMessage();
            }
        }
    }

    /**
     * Self Exceptions for Contributors of Project.
     */
    public abstract static class OfContributor extends TasksException {

        /**
         * Contributor's username.
         */
        private final String username;

        /**
         * Contributor's provider.
         */
        private final String provider;

        /**
         * Ctor.
         * @param username Contributor's username.
         * @param provider Contributor's provider.
         */
        protected OfContributor(final String username,
                                final String provider) {
            this.username = username;
            this.provider = provider;
        }

        @Override
        String getSelfMessage() {
            return "Contributor Tasks ["
                + this.username + ", "
                + this.provider + "]";
        }

        /**
         * Self Exception for Tasks of Contributor not found.
         */
        public static class NotFound extends OfContributor {


            /**
             * Ctor.
             *
             * @param username Contributor's username.
             * @param provider Contributor's provider.
             */
            public NotFound(final String username, final String provider) {
                super(username, provider);
            }

            @Override
            String getSelfMessage() {
                return "This task is not assigned to " + super.getSelfMessage();
            }
        }

        /**
         * Self Exception for Tasks of Contributor list.
         */
        public static class List extends OfContributor {

            /**
             * Ctor.
             *
             * @param username Contributor's username.
             * @param provider Contributor's provider.
             */
            public List(final String username, final String provider) {
                super(username, provider);
            }

            @Override
            String getSelfMessage() {
                return "Already seeing the tasks of " + super.getSelfMessage();
            }
        }

        /**
         * Self Exception for unassigning Tasks of Contributor.
         */
        public static class Unassigned extends OfContributor{

            /**
             * Ctor.
             *
             * @param username Contributor's username.
             * @param provider Contributor's provider.
             */
            public Unassigned(final String username, final String provider) {
                super(username, provider);
            }

            @Override
            String getSelfMessage() {
                return "These are the tasks " + "of " + super.getSelfMessage()
                    + ", no unassigned tasks here.";
            }

        }

    }

    /**
     * Self Exceptions for Tasks of Contract.
     */
    public abstract static class OfContract extends TasksException {

        /**
         * Contract id.
         */
        private final Contract.Id contractId;

        /**
         * Ctor.
         * @param contractId Contract id.
         */
        protected OfContract(final Contract.Id contractId) {
            this.contractId = contractId;
        }

        @Override
        String getSelfMessage() {
            return "Contract Tasks ["
                + this.contractId.getContributorUsername() + ", "
                + this.contractId.getRepoFullName() + ", "
                + this.contractId.getProvider() + ", "
                + this.contractId.getRole() + ", "
                + "]";
        }

        /**
         * Self Exception for Tasks of Contract not found.
         */
        public static class NotFound extends OfContract {

            /**
             * Ctor.
             *
             * @param contractId Contract id.
             */
            public NotFound(final Contract.Id contractId) {
                super(contractId);
            }

            @Override
            String getSelfMessage() {
                return "This task is not assigned to " + super.getSelfMessage();
            }
        }

        /**
         * Self Exception for Tasks of Contract list.
         */
        public static class List extends OfContract {

            /**
             * Ctor.
             *
             * @param contractId Contract id.
             */
            public List(final Contract.Id contractId) {
                super(contractId);
            }

            @Override
            String getSelfMessage() {
                return "Already seeing the tasks of " + super.getSelfMessage();
            }
        }

    }

    /**
     * Self Exception for unassigned Tasks.
     */
    public static final class OfUnassigned extends TasksException {

        /**
         * Message.
         */
        private final String message;

        /**
         * Ctor.
         * @param message Message.
         */
        public OfUnassigned(final String message) {
            this.message = message;
        }

        @Override
        String getSelfMessage() {
            return this.message;
        }

    }

    /**
     * Self exception for a single Task.
     */
    public abstract static class Single extends TasksException {

        /**
         * Issue id.
         */
        private final String issueId;

        /**
         * Ctor.
         * @param issueId Issue id.
         */
        protected Single(final String issueId) {
            this.issueId = issueId;
        }


        @Override
        String getSelfMessage() {
            return "Task [" + this.issueId + "]";
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("/issueId", this.issueId)
                .build()
                .apply(super.json());
        }

        /**
         * Self exception for register a Task.
         */
        public static final class Add extends Single {

            /**
             * Extra message.
             */
            private final String message;

            /**
             * Ctor.
             * @param issueId Issue id.
             * @param message Extra message.
             */
            public Add(final String issueId,
                       final String message) {
                super(issueId);
                this.message = message;
            }


            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " " + this.message;
            }
        }

        /**
         * Self exception to assign a Task.
         */
        public static final class Assign extends Single {

            /**
             * Extra message.
             */
            private final String message;

            /**
             * Ctor.
             * @param issueId Issue id.
             * @param message Extra message.
             */
            public Assign(final String issueId,
                       final String message) {
                super(issueId);
                this.message = message;
            }


            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " " + this.message;
            }
        }

        /**
         * Self exception for not found Project.
         */
        public static final class NotFound extends Single {


            /**
             * Ctor.
             * @param issueId Issue id.
             */
            public NotFound(final String issueId) {
                super(issueId);
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " was not found.";
            }
        }
    }

}
