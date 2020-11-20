package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
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
            1,
            contract,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        assertThat(invoice.contract(), is(contract));
    }

    /**
     * Invoice can return the invoiced tasks it contains.
     */
    @Test
    public void returnsTasks() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transactionID",
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final List<InvoicedTask> tasks = new ArrayList<>();
                    tasks.add(Mockito.mock(InvoicedTask.class));
                    tasks.add(Mockito.mock(InvoicedTask.class));
                    tasks.add(Mockito.mock(InvoicedTask.class));
                    return tasks.stream();
                },
                storage
            )
        );
        Mockito.when(storage.invoicedTasks()).thenReturn(all);

        MatcherAssert.assertThat(
            invoice.tasks(),
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * Invoice can return its total amount.
     */
    @Test
    public void returnsTotalAmount() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transactionID",
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final InvoicedTask task = Mockito.mock(InvoicedTask.class);
                    Mockito.when(task.totalAmount())
                        .thenReturn(BigDecimal.valueOf(1525));
                    final List<InvoicedTask> tasks = new ArrayList<>();
                    tasks.add(task);
                    tasks.add(task);
                    tasks.add(task);
                    return tasks.stream();
                },
                storage
            )
        );
        Mockito.when(storage.invoicedTasks()).thenReturn(all);

        MatcherAssert.assertThat(
            invoice.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(4575))
        );
    }

    /**
     * Invoice can return its total commission.
     */
    @Test
    public void returnsCommission() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transactionID",
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final InvoicedTask task = Mockito.mock(InvoicedTask.class);
                    Mockito.when(task.commission())
                        .thenReturn(BigDecimal.valueOf(100));
                    final List<InvoicedTask> tasks = new ArrayList<>();
                    tasks.add(task);
                    tasks.add(task);
                    tasks.add(task);
                    return tasks.stream();
                },
                storage
            )
        );
        Mockito.when(storage.invoicedTasks()).thenReturn(all);

        MatcherAssert.assertThat(
            invoice.commission(),
            Matchers.equalTo(BigDecimal.valueOf(300))
        );
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
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.createdAt(),
            Matchers.is(creationTime)
        );
    }

    /**
     * A StoredInvoice can return its payment time.
     */
    @Test
    public void hasPaymentTime() {
        final LocalDateTime paymentTime = LocalDateTime.now();
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            paymentTime,
            "transactionId",
            storage
        );
        MatcherAssert.assertThat(
            invoice.paymentTime(),
            Matchers.is(paymentTime)
        );
    }

    /**
     * A StoredInvoice can return its transaction id.
     */
    @Test
    public void hasTransactionId() {
        final LocalDateTime paymentTime = LocalDateTime.now();
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            paymentTime,
            "transactionId123",
            mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.transactionId(),
            Matchers.is("transactionId123")
        );
    }

    /**
     * A StoredInvoice will not register a Task which is from
     * another Contract.
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
            1,
            contract,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
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

        invoice.register(task, BigDecimal.valueOf(50));
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

        invoice.register(task, BigDecimal.valueOf(50));
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
            null,
            null,
            storage
        );

        final BigDecimal commission = BigDecimal.valueOf(50);

        final InvoicedTask registered = Mockito.mock(InvoicedTask.class);
        final InvoicedTasks invoicedTasks = Mockito.mock(InvoicedTasks.class);
        Mockito
            .when(
                invoicedTasks.register(
                    invoice, task, commission
                )
            )
            .thenReturn(registered);
        Mockito.when(storage.invoicedTasks()).thenReturn(invoicedTasks);

        MatcherAssert.assertThat(
            invoice.register(task, commission),
            Matchers.is(registered)
        );
    }

    /**
     * Can compare two StoredInvoice objects.
     */
    @Test
    public void comparesStoredInvoiceObjects() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(invoice, Matchers.equalTo(invoiceTwo));
    }

    /**
     * Verifies HashCode generation from StoredInvoice.
     */
    @Test
    public void verifiesStoredInvoiceHashcode() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "transacetionId123",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(invoice.hashCode(),
            Matchers.equalTo(invoiceTwo.hashCode()));
    }

}
