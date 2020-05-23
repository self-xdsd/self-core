package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Units tests for In-Memory Contracts.
 *
 * @author criske
 * @version $Id$ it.
 * @since 0.0.1
 */
public final class InMemoryContractsTestCase {

    /**
     * Adds a contract.
     */
    @Test
    public void addContract() {
        Storage storage = new InMemory();
        ProjectManager projectManager = storage.projectManagers().pick();
        Project project = storage.projects()
            .register(mock(Repo.class), projectManager);
        InMemoryContracts contracts = (InMemoryContracts) storage.contracts();

        Contract contract = contracts.addContract(project.projectId(),
            1, BigDecimal.ONE, "DEV");

        assertThat(contract.project().projectId(),
            equalTo(project.projectId()));
        assertThat(contract.project().contracts(),
            contains(contract));

        assertThat(contract.contributor().contributorId(),
            equalTo(1));
        assertThat(contract.contributor().contracts(),
            contains(contract));
    }

    /**
     * Throw exception when creating a contract with invalid project
     * or contributor.
     */
    @SuppressWarnings("RedundantOperationOnEmptyContainer")
    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenProjectOrContributorNotFound() {
        Storage storage = mock(Storage.class);
        Projects projects = mock(Projects.class);
        InMemoryContracts contracts = new InMemoryContracts(storage);

        when(storage.projects()).thenReturn(projects);
        when(projects.spliterator())
            .thenReturn(List.<Project>of().spliterator());

        contracts.addContract(1,
            1, BigDecimal.ONE, "DEV");
    }

}
