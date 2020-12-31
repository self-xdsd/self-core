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

import java.net.URI;

/**
 * Base class for Repo Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class RepoException extends SelfException {


    /**
     * Repo not found Self exception.
     */
    public static final class NotFound extends RepoException {

        /**
         * URI of the repo.
         */
        private final URI repo;

        /**
         * Response status code.
         */
        private final int status;

        /**
         * Ctor.
         * @param repo URI of the repo.
         * @param status Response status code.
         */
        public NotFound(final URI repo, final int status) {
            this.repo = repo;
            this.status = status;
        }

        @Override
        String getSelfMessage() {
            return "Repo [" + this.repo.toString() + "] not found. "
                + "Expected 200 OK, but got " + this.status + ". ";
        }
    }

    /**
     * Repo not found Self exception.
     */
    public static final class AlreadyActive extends RepoException {

        /**
         * Repo full name.
         */
        private final String fullName;

        /**
         * Ctor.
         * @param fullName Repo fullName.
         */
        public AlreadyActive(final String fullName) {
            this.fullName = fullName;
        }

        @Override
        String getSelfMessage() {
            return "Repo [" + this.fullName+ "] is already active.";
        }
    }

}
