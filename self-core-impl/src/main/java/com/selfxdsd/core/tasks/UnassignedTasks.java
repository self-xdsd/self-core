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
 * Unassigned Tasks. This class <b>just represents</b>
 * the tasks. The actual filtering has to be done in an upper layer.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.5
 */
public final class UnassignedTasks implements Tasks {

    /**
     * The unassigned tasks.
     */
    private final Supplier<Stream<Task>> tasks;

    /**
     * Self storage, to save new unassigned tasks.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param tasks Unassigned tasks.
     * @param storage Storage.
     */
    public UnassignedTasks(final Supplier<Stream<Task>> tasks,
                           final Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        return this.tasks
            .get()
            .filter(t -> t.issueId().equals(issueId)
                && t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Task register(final Issue issue) {
        return this.storage.tasks().register(issue);
    }

    @Override
    public Task assign(
        final Task task,
        final Contract contract,
        final int days
    ) {
        return this.storage.tasks().assign(task, contract, days);
    }

    @Override
    public Task unassign(final Task task) {
        throw new UnsupportedOperationException("Can't unassign a task from "
            + "UnassignedTasks. These tasks are already unassigned.");
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        final Supplier<Stream<Task>> ofProject = () -> tasks.get()
            .filter(t -> t.assignee() == null
                && t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(repoProvider));
        return new ProjectTasks(repoFullName, repoProvider, ofProject, storage);
    }

    @Override
    public Tasks ofContributor(final String username,
                               final String provider) {
        throw new UnsupportedOperationException("Contributors can't have "
            + " unassigned tasks");
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        throw new UnsupportedOperationException("Contracts can't have "
            + " unassigned tasks");
    }

    @Override
    public Tasks unassigned() {
        return this;
    }

    @Override
    public boolean remove(final Task task) {
        boolean contains = this.getById(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider()) != null;
        if (!contains) {
            throw new IllegalStateException("Task is not part of"
                + " UnassignedTasks.");
        }
        return this.storage.tasks().remove(task);
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
    }
}
