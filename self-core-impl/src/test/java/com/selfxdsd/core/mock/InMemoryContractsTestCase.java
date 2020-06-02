package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Units tests for In-Memory Contracts.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryContractsTestCase {

    /**
     * Adds a contract.
     */
    @Test
    public void addContract() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
                .pick("github");
        final Project project = storage.projects()
            .register(
                this.mockRepo("john/test", "github"),
                projectManager
            );
        final Contributor contributor = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        final Contract contract = contracts.addContract(
            project.repoFullName(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        assertThat(contract.project().repoFullName(),
            equalTo(project.repoFullName()));
        assertThat(contract.project().contracts(),
            contains(contract));

        assertThat(contract.contributor().username(),
            equalTo("mihai"));
        assertThat(contract.contributor().provider(),
            equalTo("github"));
        assertThat(contract.contributor().contracts(),
            contains(contract));
    }

    /**
     * A Contributor can only have one contract/role with a Project.
     */
    @Test (expected = IllegalStateException.class)
    public void shouldNotCreateAnExistingContract() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
                .pick("github");
        final Project project = storage.projects()
            .register(
                this.mockRepo("john/test", "github"),
                projectManager
            );
        final Contributor mihai = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        contracts.addContract(
            project.repoFullName(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
        contracts.addContract(
            project.repoFullName(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
    }

    /**
     * A Contributor can have more contracts with a Project,
     * each contract for a given role.
     */
    @Test
    public void moreContractsDifferentRoles() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
            .pick("github");
        final Project project = storage.projects()
            .register(
                this.mockRepo("john/test", "github"),
                projectManager
            );
        final Contributor mihai = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        contracts.addContract(
            project.repoFullName(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV

        );
        contracts.addContract(
            project.repoFullName(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.REV
        );

        assertThat(
            contracts.ofProject(project.repoFullName(), project.provider()),
            iterableWithSize(2)
        );
        assertThat(
            contracts.ofContributor(mihai),
            iterableWithSize(2)
        );
    }


    /**
     * Method addContract(...) throws ISE if the specified Project
     * does not exist.
     */
    @Test(expected = IllegalStateException.class)
    public void addThrowsExceptionWhenProjectNotFound() {
        final Storage storage = new InMemory();
        final Contributor contributor = storage.contributors()
            .register("mihai", "github");
        storage.contracts().addContract(
            "john/test",
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
    }

    /**
     * Method addContract(...) throws ISE if the specified Contributor
     * does not exist.
     */
    @Test(expected = IllegalStateException.class)
    public void addThrowsExceptionWhenContributorNotFound() {
        final Storage storage = new InMemory();
        final Project project = storage.projects()
            .register(
                mock(Repo.class),
                storage.projectManagers().pick("github")
            );
        storage.contracts().addContract(
            "john/test",
            "jhon_doe",
            "github",
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
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
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }
}
