package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unassigned Tasks. This class <b>just represents</b>
 * the tasks. The actual filtering has to be done in an upper layer.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.5
 * @todo #190:30min Implement and test unassigned() for ProjectTasks
 *  and InMemoryTasks, using UnassignedTasks.
 */
public final class UnassignedTasks implements Tasks {

    /**
     * The unassigned tasks.
     */
    private final List<Task> tasks;

    /**
     * Self storage, to save new unassigned tasks.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param tasks Unassigned tasks.
     * @param storage Storage.
     */
    public UnassignedTasks(final List<Task> tasks,
                           final Storage storage) {
        this.tasks = new ArrayList<>(tasks);
        this.storage = storage;
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        return this.tasks
            .stream()
            .filter(t -> t.issue().issueId().equals(issueId)
                && t.issue().repoFullName().equals(repoFullName)
                && t.issue().provider().equals(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Task register(final Issue issue) {
        Task registered = this.storage.tasks().register(issue);
        tasks.add(registered);
        return registered;
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        final List<Task> ofProject = tasks.stream()
            .filter(t -> t.assignee() == null
                    && t.project().repoFullName().equals(repoFullName)
                    && t.project().provider().equals(repoProvider))
            .collect(Collectors.toList());
        return new ProjectTasks(repoFullName, repoProvider, ofProject, storage);
    }

    @Override
    public Tasks ofContributor(final String username,
                               final String provider) {
        throw new UnsupportedOperationException("Contributors can't have "
            + " unassigned tasks");
    }

    @Override
    public Tasks unassigned() {
        return this;
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }
}
