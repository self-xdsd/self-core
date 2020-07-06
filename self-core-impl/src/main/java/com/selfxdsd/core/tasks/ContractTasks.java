package com.selfxdsd.core.tasks;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;

import java.util.Iterator;
import java.util.List;

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
    private final List<Task> tasks;

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
                         final List<Task> tasks,
                         final Storage storage) {
        this.contractId = contractId;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        return this.tasks.stream()
            .filter(t -> t.issue().issueId().equals(issueId)
                && t.project().repoFullName().equals(repoFullName)
                && t.project().provider().equals(provider))
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
        if (this.contractId.getRepoFullName().equals(repoFullName)
            && this.contractId.getProvider().equals(repoProvider)) {
            return this;
        } else {
            throw new IllegalStateException("Already seeing the Tasks of "
                + "Contributor " + this.contractId.getContributorUsername()
                + ", can't find other Tasks here");
        }
    }

    @Override
    public Tasks ofContributor(final String username,
                               final String provider) {
        if (this.contractId.getContributorUsername().equals(username)
            && this.contractId.getProvider().equals(provider)) {
            return this;
        } else {
            throw new IllegalStateException("Already seeing the Tasks of "
                + "Contributor " + this.contractId.getContributorUsername()
                + ", can't find other Tasks here");
        }
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        if (this.contractId.equals(id)) {
            return this;
        } else {
            throw new IllegalStateException("These are the tasks of Contract: "
                + this.contractId + ". You cannot see other "
                + "Contract's tasks here.");
        }
    }

    @Override
    public Tasks unassigned() {
        throw new UnsupportedOperationException("These are the tasks "
            + " of contributor " + contractId.getContributorUsername()
            + " contract, no unassigned tasks here.");
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }
}
