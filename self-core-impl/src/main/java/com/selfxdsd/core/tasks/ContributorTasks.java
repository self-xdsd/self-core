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
    private final Supplier<Stream<Task>> tasks;

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
                            final Supplier<Stream<Task>> tasks,
                            final Storage storage) {
        this.username = username;
        this.provider = provider;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        return this.tasks.get()
            .filter(t -> t.issue().issueId().equals(issueId)
                && t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(provider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Task register(final Issue issue) {
        throw new UnsupportedOperationException("Not implemented yet");
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
        final boolean isOfContributor = task.assignee() != null
            && task.assignee().username().equals(this.username)
            && task.assignee().provider().equals(this.provider);
        if (!isOfContributor) {
            throw new IllegalStateException("This task was not assigned to"
                + " this contributor " + this.username + "/" + this.provider);
        }
        return this.storage.tasks().unassign(task);
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        final Supplier<Stream<Task>> ofProject = () -> tasks.get()
            .filter(t -> t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(provider));
        return new ProjectTasks(repoFullName, provider, ofProject, storage);
    }

    @Override
    public Tasks ofContributor(
        final String username,
        final String provider
    ) {
        if (this.username.equals(username)
            && this.provider.equals(provider)) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the tasks of a Contributor. "
                + "You cannot see the tasks of another Contributor here."
        );
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        final Supplier<Stream<Task>> tasksOf = () -> this.tasks
            .get()
            .filter(
                t -> t.project().repoFullName().equals(id.getRepoFullName())
                    && t.project().provider().equals(id.getProvider())
                    && t.assignee().username()
                    .endsWith(id.getContributorUsername())
                    && t.role().equals(id.getRole()));
        return new ContractTasks(id, tasksOf, this.storage);

    }

    @Override
    public Tasks unassigned() {
        throw new UnsupportedOperationException("These are the tasks "
            + "of contributor " + username + " from provider" + provider
            + ", no unassigned tasks here.");
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
    }
}
