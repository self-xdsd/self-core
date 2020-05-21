package com.selfxdsd.core.mock;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        final InMemoryContracts contracts = new InMemoryContracts(null);

        final Contract contract = mock(Contract.class);
        final Project project = mock(Project.class);
        when(project.projectId()).thenReturn(1);
        final Contributor contributor = mock(Contributor.class);
        when(contributor.contributorId()).thenReturn(10);
        when(contract.project()).thenReturn(project);
        when(contract.contributor()).thenReturn(contributor);

        contracts.addContract(1, 10);

        assertThat(contracts, contains(contract));
    }

    /**
     * After adding a contract, its project and contributor.
     * must contain that contract
     */
    @Test
    public void contractShouldBeLinkedToProjectAndContributor() {
        final InMemoryContracts contracts = new InMemoryContracts(null);
        contracts.addContract(1, 10);
        Contracts contractsForProject = contracts.ofProject(1);

        Contract foundContract = contractsForProject.iterator().next();
        assertThat(foundContract.project().projectId(), equalTo(1));
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
        final InMemoryContracts contracts = new InMemoryContracts(null);
        contracts.addContract(1, 10);
        contracts.addContract(1, 11);

        contracts.addContract(2, 10);

        assertThat(contracts.ofProject(1), iterableWithSize(2));
        assertThat(contracts.ofProject(2), iterableWithSize(1));
        assertThat(contracts.ofProject(3), iterableWithSize(0));
    }

    /**
     * Should return the correct number of contracts for a project id
     * from any point in the object graph.
     */
    @Test
    public void projectContractsAccessedFromAnywhere(){
        final InMemoryContracts contracts = new InMemoryContracts(null);
        contracts.addContract(1, 10);
        contracts.addContract(1, 11);
        contracts.addContract(2, 10);

        Contracts contractsForProject = contracts.ofProject(1)
            .ofProject(1)
            .ofProject(1)
            .ofProject(1)
            .ofProject(1)
            .ofProject(1);

        assertThat(contractsForProject, iterableWithSize(2));
    }

    /**
     * Should return the correct number of contracts for each contributor.
     */
    @Test
    public void contributorContracts() {
        final InMemoryContracts contracts = new InMemoryContracts(null);
        contracts.addContract(1, 10);
        contracts.addContract(1, 11);
        contracts.addContract(2, 10);

        final Contributor contribTen = toList(contracts.ofProject(1))
            .get(0)
            .contributor();
        assertThat(
            "Contributor 10 should have 2 contracts",
            contribTen.contracts(), iterableWithSize(2));

        final Contributor contribEleven = toList(contracts.ofProject(1))
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
