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
 * Active tasks of a Contract. This class <b>just represents</b>
 * the tasks. The actual filtering has to be done in an upper layer.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.6
 */
public final class ContractTasks implements Tasks {
    /**
     * Contract's id.
     */
    private final Contract.Id contractId;

    /**
     * Contract's tasks.
     */
    private final Supplier<Stream<Task>> tasks;

    /**
     * Storage used to save new tasks.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param contractId Contract's id.
     * @param tasks Contract's tasks.
     * @param storage Storage used to save new tasks.
     */
    public ContractTasks(final Contract.Id contractId,
                         final Supplier<Stream<Task>> tasks,
                         final Storage storage) {
        this.contractId = contractId;
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
        throw new UnsupportedOperationException("The tasks API doesn't support "
            + " yet registering a task with a contract attached.");
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        if (this.contractId.getRepoFullName().equalsIgnoreCase(repoFullName)
            && this.contractId.getProvider().equalsIgnoreCase(repoProvider)) {
            return this;
        } else {
            throw new TasksException.OfProject.List(
                this.contractId.getRepoFullName(),
                this.contractId.getProvider()
            );
        }
    }

    @Override
    public Tasks ofContributor(final String username,
                               final String provider) {
        if (this.contractId.getContributorUsername().equalsIgnoreCase(username)
            && this.contractId.getProvider().equalsIgnoreCase(provider)) {
            return this;
        } else {
            throw new TasksException.OfContributor.List(
                this.contractId.getContributorUsername(),
                this.contractId.getProvider()
            );
        }
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        if (this.contractId.equals(id)) {
            return this;
        } else {
            throw new TasksException.OfContract.List(this.contractId);
        }
    }

    @Override
    public Tasks unassigned() {
        throw new UnsupportedOperationException("These are the tasks "
            + " of contributor " + contractId.getContributorUsername()
            + " contract, no unassigned tasks here.");
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
            final String assignee;
            if (task.assignee() != null) {
                assignee = task.assignee().username();
            } else {
                assignee = "No assignee";
            }
            throw new TasksException.OfContract.NotFound(new Contract.Id(
                task.project().repoFullName(),
                assignee,
                task.project().provider(),
                task.role()
            ));
        }
        return this.storage.tasks().remove(task);
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.get().iterator();
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
        final boolean isOfContract = this.tasks.get()
            .anyMatch(t -> t.equals(task));
        if (!isOfContract) {
            final String assignee;
            if (task.assignee() != null) {
                assignee = task.assignee().username();
            } else {
                assignee = "No assignee";
            }
            throw new TasksException.OfContract.NotFound(new Contract.Id(
                task.project().repoFullName(),
                assignee,
                task.project().provider(),
                task.role()
            ));
        }
        return this.storage.tasks().unassign(task);
    }

    @Override
    public Task updateEstimation(final Task task, final int estimation) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
