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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.ProjectsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.tasks.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * In-memory Tasks for test purposes.
 *
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
     *
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
    public Task getById(
        final String issueId,
        final String repoFullName,
        final String provider,
        final boolean isPullRequest
    ) {
        return this.tasks.get(
            new TaskKey(issueId, repoFullName, provider, isPullRequest)
        );
    }

    @Override
    public Task register(final Issue issue) {
        final Project project = this.storage.projects().getProjectById(
            issue.repoFullName(), issue.provider()
        );
        if (project == null) {
            throw new ProjectsException.Single.NotFound(
                issue.repoFullName(),
                issue.provider()
            );
        } else {
            final Task newTask = new StoredTask(
                project,
                issue.issueId(),
                issue.role(),
                issue.estimation().minutes(),
                issue.isPullRequest(),
                this.storage
            );
            this.tasks.put(
                new TaskKey(
                    issue.issueId(),
                    issue.repoFullName(),
                    issue.provider(),
                    issue.isPullRequest()
                ),
                newTask
            );
            return newTask;
        }
    }

    @Override
    public Task assign(
        final Task task,
        final Contract contract,
        final int days
    ) {
        final TaskKey key = new TaskKey(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            task.isPullRequest()
        );
        final LocalDateTime assignmentDate = LocalDateTime.now();
        final Task assigned = new StoredTask(
            contract,
            key.issueId,
            this.storage,
            assignmentDate,
            assignmentDate.plusDays(days),
            task.estimation(),
            task.isPullRequest()
        );
        this.tasks.put(key, assigned);
        return assigned;
    }

    @Override
    public Task unassign(final Task task) {
        final TaskKey key = new TaskKey(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            task.isPullRequest()
        );
        final Task unassigned = new StoredTask(
            task.project(),
            key.issueId,
            task.role(),
            task.estimation(),
            task.isPullRequest(),
            this.storage
        );
        this.tasks.put(key, unassigned);
        return unassigned;
    }

    @Override
    public Task updateEstimation(final Task task, final int estimation) {
        final TaskKey key = new TaskKey(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            task.isPullRequest()
        );
        final Task updated = new StoredTask(
            task.project(),
            key.issueId,
            task.role(),
            estimation,
            task.isPullRequest(),
            this.storage
        );
        this.tasks.put(key, updated);
        return updated;
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        final Supplier<Stream<Task>> tasksOf = () -> tasks.values()
            .stream()
            .filter(t -> t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(repoProvider));
        return new ProjectTasks(repoFullName,
                repoProvider,
                tasksOf,
                storage);
    }

    @Override
    public Tasks ofContributor(final String username, final String provider) {
        final Supplier<Stream<Task>> tasksOf = () -> tasks.values()
            .stream()
            .filter(t -> t.assignee().username().equals(username)
                && t.assignee().provider().equals(provider));
        return new ContributorTasks(username, provider, tasksOf, storage);
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        final Supplier<Stream<Task>> tasksOf = () -> tasks.values()
            .stream()
            .filter(
                t -> t.project().repoFullName().equals(id.getRepoFullName())
            && t.project().provider().equals(id.getProvider())
            && t.assignee().username().endsWith(id.getContributorUsername())
            && t.role().equals(id.getRole()));
        return new ContractTasks(id, tasksOf, this.storage);
    }

    @Override
    public Tasks unassigned() {
        final Supplier<Stream<Task>> unassigned = () -> tasks.values()
            .stream()
            .filter(t -> t.assignee() == null);
        return new UnassignedTasks(unassigned, storage);
    }

    @Override
    public boolean remove(final Task task) {
        final TaskKey key = new TaskKey(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            task.isPullRequest()
        );
        return tasks.remove(key) != null;
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
         * Repo full name.
         */
        private final String repoFullName;

        /**
         * Provider.
         */
        private final String provider;

        /**
         * Is it a PR?
         */
        private final boolean isPullRequest;

        /**
         * Constructor.
         *
         * @param issueId Given Issue ID.
         * @param repoFullName Repo full name.
         * @param provider Given provider.
         * @param isPullRequest Is it a PR?
         */
        TaskKey(
            final String issueId,
            final String repoFullName,
            final String provider,
            final boolean isPullRequest
        ) {
            this.issueId = issueId;
            this.repoFullName = repoFullName;
            this.provider = provider;
            this.isPullRequest = isPullRequest;
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
                && this.provider.equals(taskKey.provider)
                && this.repoFullName.equals(taskKey.repoFullName)
                && this.isPullRequest == taskKey.isPullRequest;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.issueId,
                this.provider,
                this.repoFullName,
                this.isPullRequest
            );
        }
    }
}
