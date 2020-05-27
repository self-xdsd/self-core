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

import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory Tasks for test purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryTasks implements Tasks {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Tasks "table".
     */
    private final Map<InMemoryTasks.TaskKey, Task> tasks = new HashMap<>();

    /**
     * Ctor.
     * @param storage Parent storage.
     */
    public InMemoryTasks(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.values().iterator();
    }

    @Override
    public Task getById(final String issueId, final String provider) {
        return this.tasks.get(new TaskKey(issueId, provider));
    }

    /**
     * A Task's primary key.
     */
    public static final class TaskKey {

        /**
         * Issue ID.
         */
        private final String issueId;

        /**
         * Provider.
         */
        private final String provider;

        /**
         * Constructor.
         * @param issueId Given Issue ID.
         * @param provider Given provider.
         */
        TaskKey(final String issueId, final String provider) {
            this.issueId = issueId;
            this.provider = provider;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final TaskKey taskKey = (TaskKey) object;
            return this.issueId.equals(taskKey.issueId)
                && this.provider.equals(taskKey.provider);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.issueId, this.provider);
        }
    }
}
