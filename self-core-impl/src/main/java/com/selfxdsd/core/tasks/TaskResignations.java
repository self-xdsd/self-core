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

import com.selfxdsd.api.Resignation;
import com.selfxdsd.api.Resignations;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Resignations registered in a Task.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 */
public final class TaskResignations implements Resignations {

    /**
     * The task in question.
     */
    private final Task task;

    /**
     * The task's resignations.
     */
    private final Supplier<Stream<Resignation>> resignations;

    /**
     * Self storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param task The task.
     * @param resignations The task's resignations.
     * @param storage Self storage.
     */
    public TaskResignations(
        final Task task,
        final Supplier<Stream<Resignation>> resignations,
        final Storage storage
    ) {
        this.task = task;
        this.resignations = resignations;
        this.storage = storage;
    }

    @Override
    public Resignations ofTask(final Task task) {
        if(this.task.equals(task)) {
            return this;
        } else {
            throw new IllegalStateException(
                "Already seeing the resignations of Task #"
                + this.task.issueId() + " from project "
                + this.task.project().repoFullName() + " at "
                + this.task.project().provider() + "."
            );
        }
    }

    @Override
    public Resignation register(final Task task, final String reason) {
        if(!this.task.equals(task)) {
            throw new IllegalStateException(
                "Trying to resign a task different than #"
                    + this.task.issueId() + " from project "
                    + this.task.project().repoFullName() + " at "
                    + this.task.project().provider() + "."
            );
        }
        return this.storage.resignations().register(task, reason);
    }

    @Override
    public Iterator<Resignation> iterator() {
        return this.resignations.get().iterator();
    }
}
