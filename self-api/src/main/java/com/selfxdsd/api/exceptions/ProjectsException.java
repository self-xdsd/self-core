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

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for Projects Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class ProjectsException extends SelfException {

    /**
     * Self exception for a single Project.
     */
    public abstract static class Single extends ProjectsException {

        /**
         * Repo full name.
         */
        private final String fullName;
        /**
         * Repo provider.
         */
        private final String provider;

        /**
         * Ctor.
         * @param fullName Repo full name.
         * @param provider Repo provider.
         */
        protected Single(final String fullName, final String provider) {
            this.fullName = fullName;
            this.provider = provider;
        }


        @Override
        String getSelfMessage() {
            return "Project [" + this.fullName + "," + this.provider + "]";
        }

        @Override
        public JsonObject json() {
            return Json.createPatchBuilder()
                .add("fullName", this.fullName)
                .add("provider", this.provider)
                .build()
                .apply(super.json());
        }

        /**
         * Self exception for register a Project.
         */
        public static final class Add extends Single {

            /**
             * Extra message.
             */
            private final String message;

            /**
             * Ctor.
             * @param fullName Repo full name.
             * @param provider Repo provider.
             * @param message Extra message.
             */
            public Add(final String fullName, final String provider,
                       final String message) {
                super(fullName, provider);
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
             * @param fullName Repo full name.
             * @param provider Repo provider.
             */
            public NotFound(final String fullName, final String provider) {
                super(fullName, provider);
            }

            @Override
            String getSelfMessage() {
                return super.getSelfMessage() + " was not found.";
            }
        }
    }

}
