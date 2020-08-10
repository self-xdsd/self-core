package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StoredInvoice}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public final class StoredInvoiceTestCase {

    /**
     * Invoice has the correct id.
     */
    @Test
    public void hasCorrectId() {
        final Invoice invoice = new StoredInvoice(
            1, Mockito.mock(Contract.class),
            LocalDateTime.now(), mock(Storage.class)
        );
        assertThat(invoice.invoiceId(), is(1));
    }

    /**
     * Invoice has the correct contract id.
     */
    @Test
    public void returnsContract() {
        final Contract contract = Mockito.mock(Contract.class);
        final Invoice invoice = new StoredInvoice(
            1, contract, LocalDateTime.now(), mock(Storage.class)
        );
        assertThat(invoice.contract(), is(contract));
    }

    /**
     * A StoredInvoice can return its creation time.
     */
    @Test
    public void hasCreationTime() {
        final LocalDateTime creationTime = LocalDateTime.now();
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            creationTime,
            mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.createdAt(),
            Matchers.is(creationTime)
        );
    }

    /**
     * A StoredInvoice will not register a Task which is from another Contract.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerRejectsForeignTask() {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(
            new Contract.Id(
                "john/test",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        final Invoice invoice = new StoredInvoice(
            1, contract, LocalDateTime.now(), Mockito.mock(Storage.class)
        );

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/other");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.assignee()).thenReturn(assignee);
        Mockito.when(task.role()).thenReturn(Contract.Roles.DEV);

        invoice.register(task);
    }

    /**
     * We shouldn't be able to register a new Task if the Invoice is already
     * paid.
     */
    @Test(expected = IllegalStateException.class)
    public void registerComplainsIfInvoiceIsPaid() {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(
            new Contract.Id(
                "john/test",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transactionId123",
            Mockito.mock(Storage.class)
        );

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.assignee()).thenReturn(assignee);
        Mockito.when(task.role()).thenReturn(Contract.Roles.DEV);

        invoice.register(task);
    }

    /**
     * We should be able to register a new Task if the Task and the Invoice
     * belong to the same Contract and the Invoice is active (not paid yet).
     */
    @Test
    public void registersNewTask() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.assignee()).thenReturn(assignee);
        Mockito.when(task.role()).thenReturn(Contract.Roles.DEV);

        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(
            new Contract.Id(
                "john/test",
                "mihai",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );

        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            storage
        );

        final InvoicedTask registered = Mockito.mock(InvoicedTask.class);
        final InvoicedTasks invoicedTasks = Mockito.mock(InvoicedTasks.class);
        Mockito
            .when(invoicedTasks.register(invoice, task))
            .thenReturn(registered);
        Mockito.when(storage.invoicedTasks()).thenReturn(invoicedTasks);

        MatcherAssert.assertThat(
            invoice.register(task),
            Matchers.is(registered)
        );
    }

    /**
     * Can compare two StoredInvoice objects.
     */
    @Test
    public void comparesStoredInvoiceObjects() {
        final Invoice invoice = new StoredInvoice(
            1, Mockito.mock(Contract.class),
            LocalDateTime.now(), mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1, Mockito.mock(Contract.class),
            LocalDateTime.now(), mock(Storage.class)
        );
        MatcherAssert.assertThat(invoice, Matchers.is(invoiceTwo));
    }

    /**
     * Verifies HashCode generation from StoredInvoice.
     */
    @Test
    public void verifiesStoredInvoiceHashcode() {
        final Invoice invoice = new StoredInvoice(
            1, Mockito.mock(Contract.class),
            LocalDateTime.now(), mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1, Mockito.mock(Contract.class),
            LocalDateTime.now(), mock(Storage.class)
        );
        MatcherAssert.assertThat(invoice.hashCode(),
            Matchers.equalTo(invoiceTwo.hashCode()));
    }

    /**
     * Mocks an {@link InvoiceTask}.
     *
     * @param invoiceId Invoice id.
     * @param duration Duration of task.
     * @return Mocked {@link InvoiceTask}
     */
    private InvoiceTask mockInvoiceTask(
        final int invoiceId,
        final Duration duration) {
        return new InvoiceTask() {
            @Override
            public int invoiceId() {
                return invoiceId;
            }

            @Override
            public Duration timeSpent() {
                return duration;
            }

            @Override
            public Task task() {
                return mock(Task.class);
            }
        };
    }
}
