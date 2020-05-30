package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Active tasks of a Contributor. This class <b>just represents</b>
 * the tasks. The actual filtering has to be done in an upper layer.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorTasks implements Tasks {

    /**
     * Contributor's user name.
     */
    private final String username;

    /**
     * Contributor's provider.
     */
    private final String provider;

    /**
     * The contributor's tasks.
     */
    private final List<Task> tasks;

    /**
     * Self storage, to save new contracts.
     */
    private final Storage storage;

    /**
     * Constructor.
     *
     * @param username Contributor's user name.
     * @param provider Contributor's provider.
     * @param tasks Contributor's tasks.
     * @param storage Self's storage, to save new tasks.
     */
    public ContributorTasks(final String username,
                            final String provider,
                            final List<Task> tasks,
                            final Storage storage) {
        this.username = username;
        this.provider = provider;
        this.tasks = new ArrayList<>(tasks);
        this.storage = storage;
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        return this.storage.tasks().getById(issueId, repoFullName, provider);
    }

    @Override
    public Task register(final Issue issue) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        return this.storage.tasks().ofProject(repoFullName, repoProvider);
    }

    @Override
    public Tasks ofContributor(final String username,
                               final String provider) {
        ContributorTasks tasks;
        if (this.username.equals(username)
            && this.provider.equals(provider)) {
            tasks = this;
        } else {
            List<Task> ofContributor = StreamSupport
                .stream(storage.tasks().spliterator(), false)
                .filter(t -> t.assignee().username().equals(username)
                    && t.assignee().provider().equals(provider))
                .collect(Collectors.toList());
            tasks = new ContributorTasks(username, provider, ofContributor,
                storage);
        }
        return tasks;
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }
}
