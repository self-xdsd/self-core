package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.exceptions.TasksException;
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
 * @checkstyle LineLength (200 lines)
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
    public Task getById(
        final String issueId,
        final String repoFullName,
        final String provider,
        final boolean isPullRequest
    ) {
        return this.tasks.get()
            .filter(t -> t.issueId().equals(issueId)
                && t.project().repoFullName().equalsIgnoreCase(repoFullName)
                && t.project().provider().equalsIgnoreCase(provider)
                && t.isPullRequest() == isPullRequest)
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
            && task.assignee().username().equalsIgnoreCase(this.username)
            && task.assignee().provider().equalsIgnoreCase(this.provider);
        if (!isOfContributor) {
            throw new TasksException.OfContributor
                .NotFound(this.username, this.provider);
        }
        return this.storage.tasks().unassign(task);
    }

    @Override
    public Task updateEstimation(final Task task, final int estimation) {
        final boolean isOfContributor = task.assignee() != null
            && task.assignee().username().equalsIgnoreCase(this.username)
            && task.assignee().provider().equalsIgnoreCase(this.provider);
        if (!isOfContributor) {
            throw new TasksException.OfContributor
                .NotFound(this.username, this.provider);
        }
        return this.storage.tasks().updateEstimation(task, estimation);
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        final Supplier<Stream<Task>> ofProject = () -> tasks.get()
            .filter(t -> t.project().repoFullName().equalsIgnoreCase(repoFullName)
                && t.project().provider().equalsIgnoreCase(provider));
        return new ProjectTasks(repoFullName, provider, ofProject, storage);
    }

    @Override
    public Tasks ofContributor(
        final String username,
        final String provider
    ) {
        if (this.username.equalsIgnoreCase(username)
            && this.provider.equalsIgnoreCase(provider)) {
            return this;
        }
        throw new TasksException.OfContributor.List(username, provider);
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        final Supplier<Stream<Task>> tasksOf = () -> this.tasks
            .get()
            .filter(
                t -> t.project().repoFullName().equalsIgnoreCase(id.getRepoFullName())
                    && t.project().provider().equalsIgnoreCase(id.getProvider())
                    && t.assignee().username()
                    .endsWith(id.getContributorUsername())
                    && t.role().equals(id.getRole()));
        return new ContractTasks(id, tasksOf, this.storage);

    }

    @Override
    public Tasks unassigned() {
        throw new TasksException.OfContributor.Unassigned(
            this.username,
            this.provider
        );
    }

    @Override
    public boolean remove(final Task task) {
        boolean contains = this.getById(
            task.issueId(),
            task.project().repoFullName(),
            task.project().provider(),
            task.isPullRequest()
        ) != null;
        if (!contains) {
            throw new TasksException.OfContributor.NotFound(
                this.username,
                this.provider
            );
        }
        return this.storage.tasks().remove(task);
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
    }
}
