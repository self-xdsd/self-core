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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Active tasks in a Project. This class <b>just represents</b>
 * the tasks. The actual filtering has to be done in an upper layer.
 * 
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectTasks implements Tasks {

    /**
     * Full name of the Repo represented by the Project.
     */
    private final String repoFullName;

    /**
     * Provider of the Repo represented by the Project.
     */
    private final String provider;

    /**
     * The project's stream tasks supplier.
     */
    private final Supplier<Stream<Task>> tasks;

    /**
     * Self storage, to save new tasks.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param repoFullName Full name of the Repo represented by the Project.
     * @param provider Provider of the Repo represented by the Project.
     * @param tasks Project's tasks stream supplier.
     * @param storage Self's storage, to save new contracts.
     */
    public ProjectTasks(
        final String repoFullName,
        final String provider,
        final Supplier<Stream<Task>> tasks,
        final Storage storage
    ) {
        this.repoFullName = repoFullName;
        this.provider = provider;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public Task getById(
        final String issueId,
        final String repoFullName,
        final String provider
    ) {
        return this.tasks.get().filter(
            task -> {
                final Issue issue = task.issue();
                return issue.issueId().equals(issueId)
                    && issue.repoFullName().equals(repoFullName)
                    && issue.provider().equals(provider);
            }
        ).findFirst().orElse(null);
    }

    @Override
    public Task register(final Issue issue) {
        if(!this.repoFullName.equals(issue.repoFullName())
            || !this.provider.equals(issue.provider())) {
            throw new IllegalArgumentException(
                "The given Issue does not belong to the Repo"
              + " represented by Project " + this.repoFullName
              + " at " + this.provider + "."
            );
        } else {
            return this.storage.tasks().register(issue);
        }
    }

    @Override
    public Tasks ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        if(this.repoFullName.equals(repoFullName)
            && this.provider.equals(repoProvider)) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the tasks of Project " + this.repoFullName
          + ", operating at " + this.provider + "."
        );
    }

    @Override
    public Tasks ofContributor(final String username, final String provider) {
        final Supplier<Stream<Task>> ofContributor = () -> tasks
            .get()
            .filter(t -> t.assignee() != null
                && t.assignee().username().equals(username)
                && t.assignee().provider().equals(provider));
        return new ContributorTasks(username, provider, ofContributor, storage);
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        final Supplier<Stream<Task>> tasksOf = () -> this.tasks
            .get()
            .filter(
                t -> t.project().repoFullName().equals(id.getRepoFullName())
            && t.project().provider().equals(id.getProvider())
            && t.assignee().username().endsWith(id.getContributorUsername())
            && t.role().equals(id.getRole()));
        return new ContractTasks(id, tasksOf, this.storage);
    }

    @Override
    public Tasks unassigned() {
        final Supplier<Stream<Task>> unassigned = () -> tasks.get()
            .filter(t -> t.assignee() == null
                && t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(provider));
        return new UnassignedTasks(unassigned, storage);
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
    }
}
