package com.selfxdsd.core.contracts;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

/**
 * Unit tests for {@link StoredContract}.
 *
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredContractTestCase {

    /**
     * StoredContract returns its id.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Contract contract = new StoredContract(
            project,
            Mockito.mock(Contributor.class),
            BigDecimal.ONE, "DEV",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(contract.project(), Matchers.is(project));
    }

    /**
     * StoredContract returns its contributor.
     */
    @Test
    public void returnsContributor() {
        final Contributor contributor = Mockito.mock(Contributor.class);
        final Contract contract = new StoredContract(
            Mockito.mock(Project.class),
            contributor,
            BigDecimal.ONE, "DEV",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contract.contributor(),
            Matchers.is(contributor));
    }

    /**
     * The Project is read from the Storage if not given as ctor parameter.
     */
    @Test
    public void lazyLoadsProject() {
        final Project project = Mockito.mock(Project.class);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("john/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final Contract contract = new StoredContract(
            new Contract.Id(
                "john/test",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            ),
            BigDecimal.valueOf(10000),
            storage
        );

        MatcherAssert.assertThat(contract.project(), Matchers.is(project));
    }

    /**
     * The Contributor is read from the Storage if not given as ctor parameter.
     */
    @Test
    public void lazyLoadsContributor() {
        final Contributor mihai = Mockito.mock(Contributor.class);
        final Contributors all = Mockito.mock(Contributors.class);
        Mockito.when(
            all.getById("mihai", Provider.Names.GITHUB)
        ).thenReturn(mihai);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contributors()).thenReturn(all);

        final Contract contract = new StoredContract(
            new Contract.Id(
                "john/test",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            ),
            BigDecimal.valueOf(10000),
            storage
        );

        MatcherAssert.assertThat(contract.contributor(), Matchers.is(mihai));
    }

    /**
     * StoredContract returns its hourly rate.
     */
    @Test
    public void returnsHourlyRate() {
        final Contract contract = new StoredContract(
            Mockito.mock(Project.class),
            Mockito.mock(Contributor.class),
            BigDecimal.ONE, "DEV",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            contract.hourlyRate(),
            Matchers.equalTo(BigDecimal.ONE));
    }

    /**
     * StoredContract returns its role.
     */
    @Test
    public void returnsRole() {
        final Contract contract = new StoredContract(
            Mockito.mock(Project.class),
            Mockito.mock(Contributor.class),
            BigDecimal.ONE, "DEV",
            Mockito.mock(Storage.class));
        MatcherAssert.assertThat(contract.role(), Matchers.equalTo("DEV"));
    }

    /**
     * Returns contracts invoices.
     */
    @Test
    public void returnsInvoices(){
        final Storage storage = Mockito.mock(Storage.class);
        final Contract contract = new StoredContract(
            Mockito.mock(Project.class),
            Mockito.mock(Contributor.class),
            BigDecimal.ONE, "DEV",
            storage);
        final Invoices all = Mockito.mock(Invoices.class);
        final Invoices invoices = Mockito.mock(Invoices.class);

        Mockito.when(all.ofContract(Mockito.any(Contract.Id.class)))
            .thenReturn(invoices);
        Mockito.when(storage.invoices()).thenReturn(all);

        MatcherAssert.assertThat(
            contract.invoices(),
            Matchers.is(invoices)
        );
    }

    /**
     * Returns contracts tasks.
     */
    @Test
    public void returnsTasks(){
        final Storage storage = Mockito.mock(Storage.class);
        final Project project = Mockito.mock(Project.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        final Contract contract = new StoredContract(
            project,
            contributor,
            BigDecimal.ONE, "DEV",
            storage);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = Mockito.mock(Tasks.class);

        Mockito.when(project.repoFullName()).thenReturn("john/repo");
        Mockito.when(contributor.username()).thenReturn("mihai");
        Mockito.when(contributor.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(storage.tasks()).thenReturn(all);
        Mockito.when(all.ofContract(Mockito.any(Contract.Id.class)))
            .thenReturn(tasks);

        MatcherAssert.assertThat(
            contract.tasks(),
            Matchers.is(tasks)
        );
    }

    /**
     * StoredContract can return its value.
     * @checkstyle ExecutableStatementCount (50 lines)
     */
    @Test
    public void returnsValue() {
        final Task one = Mockito.mock(Task.class);
        Mockito.when(one.value()).thenReturn(BigDecimal.valueOf(10000));
        final Task two = Mockito.mock(Task.class);
        Mockito.when(two.value()).thenReturn(BigDecimal.valueOf(15000));
        final Task three = Mockito.mock(Task.class);
        Mockito.when(three.value()).thenReturn(BigDecimal.valueOf(7500));
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(
            List.of(one, two, three).iterator()
        );
        final Invoices invoices = Mockito.mock(Invoices.class);
        final Invoice active = Mockito.mock(Invoice.class);
        Mockito.when(active.totalAmount())
            .thenReturn(BigDecimal.valueOf(12300));
        Mockito.when(invoices.active()).thenReturn(active);

        final Tasks allTasks = Mockito.mock(Tasks.class);
        Mockito.when(
            allTasks.ofContract(
                new Contract.Id(
                "john/test",
                "mihai",
                "github",
                "DEV"
                )
            )
        ).thenReturn(tasks);
        final Invoices allInvoices = Mockito.mock(Invoices.class);
        Mockito.when(
            allInvoices.ofContract(
                new Contract.Id(
            "john/test",
            "mihai",
            "github",
            "DEV"
                )
            )
        ).thenReturn(invoices);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(allTasks);
        Mockito.when(storage.invoices()).thenReturn(allInvoices);

        final Contract contract = new StoredContract(
            new Contract.Id(
                "john/test",
                "mihai",
                "github",
                "DEV"
            ),
            BigDecimal.valueOf(15000),
            storage
        );

        MatcherAssert.assertThat(
            contract.value(),
            Matchers.equalTo(BigDecimal.valueOf(44800))
        );
    }
}
