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
            "DEV"
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
     * When a contract is already created a second one should not
     * be created.
     */
    @Test
    public void shouldNotCreateAnExistingContract() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage.projectManagers()
                .pick("github");
        final Project project = storage.projects()
            .register(mock(Repo.class), projectManager);
        final Contributor contributor = storage.contributors()
            .register("mihai", "github");
        final Contracts contracts = storage.contracts();

        contracts.addContract(
            project.projectId(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            "DEV"
        );
        contracts.addContract(
            project.projectId(),
            contributor.username(),
            contributor.provider(),
            BigDecimal.ONE,
            "DEV"
        );

        assertThat(contracts, iterableWithSize(1));
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
            "DEV"
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
            "DEV"
        );
    }


}
