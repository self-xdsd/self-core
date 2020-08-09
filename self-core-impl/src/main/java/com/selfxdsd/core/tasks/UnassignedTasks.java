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
            .filter(t -> t.issue().issueId().equals(issueId)
                && t.issue().repoFullName().equals(repoFullName)
                && t.issue().provider().equals(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Task register(final Issue issue) {
        return this.storage.tasks().register(issue);
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
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
    }
}
