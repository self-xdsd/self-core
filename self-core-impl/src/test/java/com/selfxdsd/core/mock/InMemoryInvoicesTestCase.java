package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit tests for {@link InMemoryInvoices}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public final class InMemoryInvoicesTestCase {

    /**
     * Adds invoice.
     */
    @Test
    public void addsInvoice(){
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
            .pick(Provider.Names.GITHUB);
        final Project project = storage
            .projects()
            .register(
                this.mockRepo("john/test", Provider.Names.GITHUB),
                projectManager
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);

        final Contracts contracts = storage.contracts();

        final Contract contract = contracts.addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor), Duration.ofHours(1));
        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor), Duration.ofHours(2));

        MatcherAssert.assertThat(
            "It should be one active invoice",
            contract.invoices(),
            Matchers.iterableWithSize(1));

        List<InvoiceTask> tasks = contract.invoices().tasks(1);
        MatcherAssert.assertThat(tasks,
            Matchers.iterableWithSize(2));

    }


    /**
     * Adds invoice to a contract.
     */
    @Test
    public void addsInvoiceToAContract(){
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
            .pick(Provider.Names.GITHUB);
        final Project project = storage
            .projects()
            .register(
                this.mockRepo("john/test", Provider.Names.GITHUB),
                projectManager
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);

        final Contracts contracts = storage.contracts();

        final Contract contract = contracts.addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        final Contract.Id contractId = new Contract.Id(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            Contract.Roles.DEV
        );

        storage.invoices().ofContract(contractId).add(this.mockAssignedTask(
            "123", storage, project, contributor), Duration.ofHours(1));

        MatcherAssert.assertThat(
            "It should be one active invoice",
            contract.invoices().ofContract(contractId),
            Matchers.iterableWithSize(1));

        List<InvoiceTask> tasks = contract.invoices()
            .ofContract(contractId).tasks(1);
        MatcherAssert.assertThat(tasks,
            Matchers.iterableWithSize(1));

    }


    /**
     * Adds task to invoice and proceed to pay.
     */
    @Test
    public void paysInvoice(){
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
            .pick(Provider.Names.GITHUB);
        final Project project = storage
            .projects()
            .register(
                this.mockRepo("john/test", Provider.Names.GITHUB),
                projectManager
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);

        final Contracts contracts = storage.contracts();

        final Contract contract = contracts.addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor), Duration.ofHours(1));

        ((InMemoryInvoices) storage.invoices()).pay(1);

        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor), Duration.ofHours(2));

        MatcherAssert.assertThat("Invoice should be paid",
            storage.invoices().isPaid(1));
        MatcherAssert.assertThat(
            "It should be to invoices one paid and o active",
            contract.invoices(), Matchers.iterableWithSize(2));

    }


    /**
     * Mock a Repo for test.
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(final String fullName, final String provider) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        Mockito.when(prov.repo(Mockito.anyString())).thenReturn(repo);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }

    /**
     * Mock an Issue for test.
     * @param issueId ID.
     * @param repoFullName Repo fullname.
     * @param provider Provider.
     * @param role Role.
     * @return Issue.
     */
    private Issue mockIssue(
        final String issueId,
        final String repoFullName,
        final String provider,
        final String role) {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn(issueId);
        Mockito.when(issue.repoFullName()).thenReturn(repoFullName);
        Mockito.when(issue.provider()).thenReturn(provider);
        Mockito.when(issue.role()).thenReturn(role);
        return issue;
    }

    /**
     * Mocks an assigned task.
     * @param issueId Issue id.
     * @param storage Storage
     * @param project Project
     * @param assignee Assignee
     * @return Assigned Task.
     */
    private Task mockAssignedTask(final String issueId,
                                  final Storage storage,
                                  final Project project,
                                  final Contributor assignee){
        final Task task = storage.tasks().register(this.mockIssue(
            issueId,
            project.repoFullName(),
            project.provider(),
            Contract.Roles.DEV
        ));
        //This could not be mocked with Mockito. It complains that "Task"
        //has missing return values in its object graph.
        return new Task() {
            @Override
            public String role() {
                return task.role();
            }

            @Override
            public Issue issue() {
                return task.issue();
            }

            @Override
            public Project project() {
                return task.project();
            }

            @Override
            public Contributor assignee() {
                return assignee;
            }

            @Override
            public LocalDateTime assignmentDate() {
                return LocalDateTime.now();
            }

            @Override
            public LocalDateTime deadline() {
                return task.deadline();
            }
        };
    }

}
