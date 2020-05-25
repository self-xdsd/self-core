package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
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
            .register(mock(Repo.class), projectManager);
        final Contributor contributor = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        final Contract contract = contracts.addContract(
            project.projectId(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        assertThat(contract.project().projectId(),
            equalTo(project.projectId()));
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
    @Test
    public void shouldNotCreateAnExistingContract() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
                .pick("github");
        final Project project = storage.projects()
            .register(mock(Repo.class), projectManager);
        final Contributor mihai = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        contracts.addContract(
            project.projectId(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
        contracts.addContract(
            project.projectId(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV
        );

        assertThat(
            contracts.ofProject(project.projectId()),
            iterableWithSize(1)
        );
        assertThat(
            contracts.ofContributor(mihai),
            iterableWithSize(1)
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
            .register(mock(Repo.class), projectManager);
        final Contributor mihai = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        contracts.addContract(
            project.projectId(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.DEV

        );
        contracts.addContract(
            project.projectId(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.ONE,
            Contract.Roles.REV
        );

        assertThat(
            contracts.ofProject(project.projectId()),
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
            100,
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
            project.projectId(),
            "jhon_doe",
            "github",
            BigDecimal.ONE,
            Contract.Roles.DEV
        );
    }


}
