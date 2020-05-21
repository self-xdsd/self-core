package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link InMemoryContracts}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryContractsTestCase {


    /**
     * Adds a contract with a project and contributor id.
     * Contract must associated to that project and contributor.
     */
    @Test
    public void addContract() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers().pick();
        int projectId = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        final InMemoryContracts contracts = new InMemoryContracts(storage);
        contracts.addContract(projectId, 10);

        assertThat(contracts, iterableWithSize(1));
    }

    /**
     * Should throw IllegalStateException when adding a contract with
     * a non existing id.
     */
    @Test(expected = IllegalStateException.class)
    public void throwExceptionIfProjectIsNotInStorage() {
        new InMemoryContracts(new InMemory())
            .addContract(100, 10);
    }

    /**
     * After adding a contract, its project and contributor.
     * must contain that contract
     */
    @Test
    public void contractShouldBeLinkedToProjectAndContributor() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers().pick();
        final InMemoryContracts contracts = new InMemoryContracts(storage);

        int projectId = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        contracts.addContract(projectId, 10);
        Contracts contractsForProject = contracts.ofProject(projectId);

        Contract foundContract = contractsForProject.iterator().next();
        assertThat(foundContract.project().projectId(), equalTo(projectId));
        assertThat(foundContract.contributor().contributorId(),
            equalTo(10));

        assertThat(toList(contractsForProject),
            equalTo(toList(foundContract.project().contracts())));
        assertThat(foundContract.contributor().contracts(),
            iterableWithSize(1));

    }

    /**
     * Should return the correct number of contracts for each project.
     */
    @Test
    public void projectContracts() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers().pick();
        final InMemoryContracts contracts = new InMemoryContracts(storage);

        int projectIdOne = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        int projectIdTwo = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        contracts.addContract(projectIdOne, 10);
        contracts.addContract(projectIdOne, 11);
        contracts.addContract(projectIdTwo, 10);

        assertThat(contracts.ofProject(projectIdOne), iterableWithSize(2));
        assertThat(contracts.ofProject(projectIdTwo), iterableWithSize(1));
        assertThat(contracts.ofProject(3), iterableWithSize(0));
    }

    /**
     * Should return the correct number of contracts for a project id
     * from any point in the object graph.
     */
    @Test
    public void projectContractsAccessedFromAnywhere() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers().pick();
        final InMemoryContracts contracts = new InMemoryContracts(storage);

        int projectIdOne = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        int projectIdTwo = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        contracts.addContract(projectIdOne, 10);
        contracts.addContract(projectIdOne, 11);
        contracts.addContract(projectIdTwo, 10);

        Contracts contractsForProject = contracts.ofProject(1)
            .ofProject(projectIdTwo)
            .ofProject(projectIdOne)
            .ofProject(projectIdTwo)
            .ofProject(projectIdOne)
            .ofProject(projectIdOne)
            .ofProject(projectIdOne);

        assertThat(contractsForProject, iterableWithSize(2));
    }

    /**
     * Should return the correct number of contracts for each contributor.
     */
    @Test
    public void contributorContracts() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers().pick();
        final InMemoryContracts contracts = new InMemoryContracts(storage);

        int projectIdOne = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        int projectIdTwo = storage
            .projects()
            .register(mock(Repo.class), projectManager)
            .projectId();

        contracts.addContract(projectIdOne, 10);
        contracts.addContract(projectIdOne, 11);
        contracts.addContract(projectIdTwo, 10);

        final Contributor contribTen = toList(contracts.ofProject(projectIdOne))
            .get(0)
            .contributor();
        assertThat(
            "Contributor 10 should have 2 contracts",
            contribTen.contracts(), iterableWithSize(2));

        final Contributor contribEleven = toList(
            contracts.ofProject(projectIdOne))
            .get(1)
            .contributor();
        assertThat("Contributor 11 should have 1 contract",
            contribEleven.contracts(), iterableWithSize(1));
    }

    /**
     * Helper that converts Contracts to a list.
     *
     * @param contracts Contracts iterable
     * @return List of contracts
     */
    private List<Contract> toList(final Contracts contracts) {
        return StreamSupport
            .stream(contracts.spliterator(), false)
            .collect(Collectors.toList());
    }
}
