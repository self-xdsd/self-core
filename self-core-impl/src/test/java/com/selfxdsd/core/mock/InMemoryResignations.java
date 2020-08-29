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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.Resignation;
import com.selfxdsd.api.Resignations;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory Resignations.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 * @todo #462:30min Implement and write tests for a Task's resignations.
 *  It will be a class named TaskResignations implementing interface
 *  Resignations.
 */
public final class InMemoryResignations implements Resignations {

    /**
     * Resignations "table".
     */
    private final Map<ResignationKey, Resignation> resignations;

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     *
     * @param storage Parent storage
     */
    public InMemoryResignations(final Storage storage) {
        this.storage = storage;
        this.resignations = new HashMap<>();
    }

    @Override
    public Resignations ofTask(final Task task) {
        return null;
    }

    @Override
    public Iterator<Resignation> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all Resignations in Self."
        );
    }

    /**
     * Resignation Primary Key, formed by the Task and the Contributor PK's
     * respectively.
     */
    private static class ResignationKey {

        /**
         * Full name of the Repo represented by the Project.
         */
        private final String repoFullName;
        /**
         * Contributor's username.
         */
        private final String contributorUsername;

        /**
         * Contributor/Project's provider.
         */
        private final String provider;

        /**
         * Issue's ID.
         */
        private final String issueId;

        /**
         * Constructor.
         *
         * @param repoFullName Fullname of the Repo represented by the project.
         * @param contributorUsername Contributor's username.
         * @param provider Contributor/Project's provider.
         * @param issueId Issue's is.
         */
        ResignationKey(
            final String repoFullName,
            final String contributorUsername,
            final String provider,
            final String issueId
        ) {
            this.repoFullName = repoFullName;
            this.contributorUsername = contributorUsername;
            this.provider = provider;
            this.issueId = issueId;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final InMemoryResignations.ResignationKey key =
                (InMemoryResignations.ResignationKey) object;
            //@checkstyle LineLength (5 lines)
            return this.repoFullName.equals(key.repoFullName)
                && this.contributorUsername.equals(key.contributorUsername)
                && this.provider.equals(key.provider)
                && this.issueId.equals(key.issueId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.repoFullName,
                this.contributorUsername,
                this.provider,
                this.issueId
            );
        }
    }
}
