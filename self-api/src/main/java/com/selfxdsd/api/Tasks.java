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
package com.selfxdsd.api;

/**
 * Tasks managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Tasks extends Iterable<Task> {

    /**
     * Get a Task by its ID.
     * @param issueId Issue ID from Github, Gitlab etc.
     * @param repoFullName Repo full name.
     * @param provider Provider name.
     * @return Task or null if not found.
     */
    Task getById(
        final String issueId,
        final String repoFullName,
        final String provider
    );

    /**
     * Register a new task.
     * @param issue Representing Issue.
     * @return Task.
     */
    Task register(final Issue issue);

    /**
     * Assign a Task to a given Contract.
     * @param task Task to be assigned.
     * @param contract Contract to receive the Task.
     * @param days Days until deadline.
     * @return Task.
     */
    Task assign(final Task task, final Contract contract, final int days);

    /**
     * Unassign a Task.
     * @param task Task to be unassigned.
     * @return Task.
     */
    Task unassign(Task task);

    /**
     * Get the tasks of a given Project.
     * @param repoFullName Full name of the Repo that the Project represents.
     * @param repoProvider Provider of the Repo that the Project represents.
     * @return Tasks.
     */
    Tasks ofProject(
        final String repoFullName,
        final String repoProvider
    );

    /**
     * Get the tasks of a given Contributor.
     * @param username Contributor's user name
     * @param provider Contributor's Provider
     * @return Tasks.
     */
    Tasks ofContributor(final String username,
                        final String provider);

    /**
     * Get the tasks of a given Contract.
     * @param id Contract's id.
     * @return Tasks.
     */
    Tasks ofContract(final Contract.Id id);

    /**
     * Get the unassigned tasks.
     * @return Tasks.
     */
    Tasks unassigned();

    /**
     * Remove a Task from storage.
     * @param task Task to be removed.
     * @return True if task is successfully removed.
     */
    boolean remove(final Task task);

}
