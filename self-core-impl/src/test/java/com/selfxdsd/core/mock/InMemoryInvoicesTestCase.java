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
                projectManager,
                "whtoken123"
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);
        final Contract contract = storage.contracts().addContract(
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

        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(1));
        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(2));

        MatcherAssert.assertThat(
            "It should be one active invoice",
            contract.invoices(),
            Matchers.iterableWithSize(1));

        MatcherAssert.assertThat(contract.invoices().tasks(1),
            Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(contract.invoices()
            .ofContract(contractId)
            .tasks(1),  Matchers.iterableWithSize(2));

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
                projectManager,
                "whtoken123"
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);

        final Contract contract = storage.contracts().addContract(
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
            "123", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(1));

        MatcherAssert.assertThat(
            "It should be one active invoice",
            contract.invoices().ofContract(contractId),
            Matchers.iterableWithSize(1));

        MatcherAssert.assertThat(contract.invoices()
            .ofContract(contractId).tasks(1),
            Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(contract.invoices().tasks(1),
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
                projectManager,
                "whtoken123"
            );
        final Contributor contributor = storage
            .contributors()
            .register("mihai", Provider.Names.GITHUB);
        final Contract contract = storage.contracts().addContract(
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
        final Invoices invoicesOfContract = contract.invoices()
            .ofContract(contractId);

        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(1));
        ((InMemoryInvoices) storage.invoices()).pay(1);
        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(2));

        MatcherAssert.assertThat("Invoice should be paid",
            storage.invoices().isPaid(1),
            Matchers.is(true));
        MatcherAssert.assertThat("Invoice of contract should be paid",
            invoicesOfContract.isPaid(1),
            Matchers.is(true));
        MatcherAssert.assertThat(
            "It should be two invoices one paid and one active",
            contract.invoices(),
            Matchers.iterableWithSize(2));

    }

    /**
     * Throws IllegalStateException if task is not assigned.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIfTaskNotAssigned(){
        final Storage storage = new InMemory();
        storage.invoices().add(Mockito.mock(Task.class), Duration.ZERO);
    }

    /**
     * Throws IllegalStateException if paid invoice is not part of contract.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIfPaidInvoiceIsNotPartOfContract(){
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
        //adding two contracts
        storage.contracts().addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
        storage.contracts().addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.ARCH
        );
        final Contract.Id contractIdOne = new Contract.Id(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            Contract.Roles.DEV
        );
        final Contract.Id contractIdTwo = new Contract.Id(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            Contract.Roles.ARCH
        );
        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(1));
        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor, Contract.Roles.ARCH),
            Duration.ofHours(2));

        storage.invoices().ofContract(contractIdTwo).isPaid(1);
    }

    /**
     * Throws IllegalStateException if invoice tasks are not part of contract.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIfInvoiceTasksNotPartOfContract(){
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
        //adding two contracts
        storage.contracts().addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
        storage.contracts().addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.ARCH
        );
        final Contract.Id contractIdTwo = new Contract.Id(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            Contract.Roles.ARCH
        );
        storage.invoices().add(this.mockAssignedTask(
            "123", storage, project, contributor, Contract.Roles.DEV),
            Duration.ofHours(1));
        storage.invoices().add(this.mockAssignedTask(
            "124", storage, project, contributor, Contract.Roles.ARCH),
            Duration.ofHours(2));

        storage.invoices().ofContract(contractIdTwo).tasks(1);
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
     * @param role Role
     * @return Assigned Task.
     */
    private Task mockAssignedTask(final String issueId,
                                  final Storage storage,
                                  final Project project,
                                  final Contributor assignee,
                                  final String role){
        final Task task = storage.tasks().register(this.mockIssue(
            issueId,
            project.repoFullName(),
            project.provider(),
            role
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
