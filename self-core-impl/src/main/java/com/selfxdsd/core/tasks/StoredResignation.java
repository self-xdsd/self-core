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
package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Resignation;
import com.selfxdsd.api.Task;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Task Resignation stored and managed by Self.
 * @author criske
 * @version $Id$
 * @since 0.0.21
 */
public final class StoredResignation implements Resignation {

    /**
     * Task which holds this resignation.
     */
    private final Task task;

    /**
     * Contributor that has resigned from Task.
     */
    private final Contributor contributor;

    /**
     * Timestamp of resignation.
     */
    private final LocalDateTime timestamp;

    /**
     * Reason of resignation.
     */
    private final String reason;

    /**
     * Ctor.
     * @param task Task which holds this resignation.
     * @param contributor Contributor that has resigned from Task.
     * @param timestamp Timestamp of resignation.
     * @param reason Reason of resignation.
     */
    public StoredResignation(final Task task,
                             final Contributor contributor,
                             final LocalDateTime timestamp,
                             final String reason) {
        this.task = task;
        this.contributor = contributor;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public Task task() {
        return this.task;
    }

    @Override
    public Contributor contributor() {
        return this.contributor;
    }

    @Override
    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    @Override
    public String reason() {
        return this.reason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            contributor.username());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Resignation)) {
            return false;
        }
        final Resignation other = (Resignation) obj;
        return this.contributor.username()
            .equals(other.contributor().username()) && this.task
            .equals(other.task());
    }
}
