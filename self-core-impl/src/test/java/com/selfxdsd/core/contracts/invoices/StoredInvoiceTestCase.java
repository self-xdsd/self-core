package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.contracts.StoredContract;
import com.selfxdsd.core.contributors.StoredContributor;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.mock.InMemory;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.tasks.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        assertThat(invoice.invoiceId(), is(1));
    }

    /**
     * Invoice has the latest Payment.
     */
    @Test
    public void hasLatestPayment() {
        final Payment latest = Mockito.mock(Payment.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            latest,
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        assertThat(invoice.latest(), is(latest));
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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

        Mockito.verify(storage, times(1)).invoicedTasks();
    }

    /**
     * Invoice can return the invoiced tasks more times, but they should be
     * read from the Storage only once and be cached.
     */
    @Test
    public void returnsCachedTasks() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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
        MatcherAssert.assertThat(
            invoice.tasks(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            invoice.tasks(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            invoice.tasks(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            invoice.tasks(),
            Matchers.iterableWithSize(3)
        );

        Mockito.verify(storage, times(1)).invoicedTasks();
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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
     * Invoice can return its amount.
     */
    @Test
    public void returnsAmount() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final InvoicedTask task = Mockito.mock(InvoicedTask.class);
                    Mockito.when(task.value())
                        .thenReturn(BigDecimal.valueOf(1000));
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
            invoice.amount(),
            Matchers.equalTo(BigDecimal.valueOf(3000))
        );
    }

    /**
     * Invoice can return its total project commission.
     */
    @Test
    public void returnsProjectCommission() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final InvoicedTask task = Mockito.mock(InvoicedTask.class);
                    Mockito.when(task.projectCommission())
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
            invoice.projectCommission(),
            Matchers.equalTo(BigDecimal.valueOf(300))
        );
    }

    /**
     * Invoice can return its total contributor commission.
     */
    @Test
    public void returnsContributorCommission() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        final InvoicedTasks all = Mockito.mock(InvoicedTasks.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(
            new InvoiceTasks(
                invoice,
                () -> {
                    final InvoicedTask task = Mockito.mock(InvoicedTask.class);
                    Mockito.when(task.contributorCommission())
                        .thenReturn(BigDecimal.valueOf(50));
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
            invoice.contributorCommission(),
            Matchers.equalTo(BigDecimal.valueOf(150))
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.createdAt(),
            Matchers.is(creationTime)
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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

        invoice.register(task, BigDecimal.valueOf(50), BigDecimal.valueOf(30));
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
        final Payment payment = Mockito.mock(Payment.class);
        Mockito.when(payment.status()).thenReturn(Payment.Status.SUCCESSFUL);
        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            payment,
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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

        invoice.register(task, BigDecimal.valueOf(50), BigDecimal.valueOf(30));
    }

    /**
     * We should be able to register a new Task if the Task and the Invoice
     * belong to the same Contract and the Invoice is active (not paid yet).
     * @checkstyle LocalFinalVariableName (100 lines)
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
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );

        final BigDecimal projectCommission = BigDecimal.valueOf(50);
        final BigDecimal contributorCommission = BigDecimal.valueOf(30);

        final InvoicedTask registered = Mockito.mock(InvoicedTask.class);
        final InvoicedTasks invoicedTasks = Mockito.mock(InvoicedTasks.class);
        Mockito
            .when(
                invoicedTasks.register(
                    invoice, task, projectCommission, contributorCommission
                )
            )
            .thenReturn(registered);
        Mockito.when(storage.invoicedTasks()).thenReturn(invoicedTasks);

        MatcherAssert.assertThat(
            invoice.register(task, projectCommission, contributorCommission),
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
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
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        final Invoice invoiceTwo = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(invoice.hashCode(),
            Matchers.equalTo(invoiceTwo.hashCode()));
    }

    /**
     * If the billedBy attribute is set in the constructor
     * (not null and not empty), that's the value that should be
     * returned.
     */
    @Test
    public void returnsSetBilledBy() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedBy(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * If the billedByCountry attribute is set in the constructor
     * (not null and not empty), that's the value that should be
     * returned.
     */
    @Test
    public void returnsSetBilledByCountry() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "BG",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedByCountry(),
            Matchers.equalTo("BG")
        );
    }

    /**
     * If the billedTo attribute is set in the constructor
     * (not null and not empty), that's the value that should be
     * returned.
     */
    @Test
    public void returnsSetBilledTo() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedTo(),
            Matchers.equalTo("vlad")
        );
    }

    /**
     * If the billedToCountry attribute is set in the constructor
     * (not null and not empty), that's the value that should be
     * returned.
     */
    @Test
    public void returnsSetBilledToCountry() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "DE",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedToCountry(),
            Matchers.equalTo("DE")
        );
    }

    /**
     * If the eurToRon attribute is set in the constructor
     * (not 0), that's the value that should be
     * returned.
     */
    @Test
    public void returnsSetEurToRon() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "DE",
            BigDecimal.valueOf(300),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.eurToRon(),
            Matchers.equalTo(BigDecimal.valueOf(300))
        );
    }

    /**
     * If the eurToRon attribute is NOT set in the constructor
     * (value 0), then it will be read from BNR (via XmlBnr).
     */
    @Test
    public void returnsBnrEurToRon() {
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            null,
            null,
            BigDecimal.valueOf(0),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.eurToRon(),
            Matchers.greaterThanOrEqualTo(BigDecimal.valueOf(450))
        );
    }

    /**
     * If the billedBy is not given as ctor parameter (set to null),
     * then it should be read from the Contract.
     */
    @Test
    public void returnsContractBilledBy() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        Mockito.when(info.toString()).thenReturn("Contributor LLC");
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.billingInfo()).thenReturn(info);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);

        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            null,
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedBy(),
            Matchers.equalTo("Contributor LLC")
        );
    }

    /**
     * If the billedByCountry is not given as ctor parameter (set to null),
     * then it should be read from the Contract.
     */
    @Test
    public void returnsContractBilledByCountry() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        Mockito.when(info.country()).thenReturn("UK");
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.billingInfo()).thenReturn(info);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(contributor);

        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            null,
            "vlad",
            null,
            null,
            BigDecimal.valueOf(0),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedByCountry(),
            Matchers.equalTo("UK")
        );
    }

    /**
     * If the billedTo is not given as ctor parameter (set to null),
     * then it should be read from the Contract.
     */
    @Test
    public void returnsContractBilledTo() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        Mockito.when(info.toString()).thenReturn("Project LLC");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.billingInfo()).thenReturn(info);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);

        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            null,
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedTo(),
            Matchers.equalTo("Project LLC")
        );
    }

    /**
     * If the billedToCountry is not given as ctor parameter (set to null),
     * then it should be read from the Contract.
     */
    @Test
    public void returnsContractBilledToCountry() {
        final BillingInfo info = Mockito.mock(BillingInfo.class);
        Mockito.when(info.country()).thenReturn("DK");
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.billingInfo()).thenReturn(info);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.project()).thenReturn(project);

        final Invoice invoice = new StoredInvoice(
            1,
            contract,
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            null,
            null,
            null,
            BigDecimal.valueOf(0),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            invoice.billedToCountry(),
            Matchers.equalTo("DK")
        );
    }

    /**
     * The PlatformInvoice is null if the Invoice is not paid (active).
     */
    @Test
    public void noPlatformInvoiceIfActive() {
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.platformInvoices()).thenThrow(
            new IllegalStateException(
                "PlatformInvoices storage should not be called!"
            )
        );
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            null,
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        MatcherAssert.assertThat(
            invoice.platformInvoice(),
            Matchers.nullValue()
        );
    }

    /**
     * The PlatformInvoice is null if the Invoice has been paid with
     * the FakeWallet (transactionId starts with "fake_payment_").
     */
    @Test
    public void noPlatformInvoiceIfFakePayment() {
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.platformInvoices()).thenThrow(
            new IllegalStateException(
                "PlatformInvoices storage should not be called!"
            )
        );
        final Payment fake = Mockito.mock(Payment.class);
        Mockito.when(fake.status()).thenReturn(Payment.Status.SUCCESSFUL);
        Mockito.when(fake.transactionId()).thenReturn("fake_payment_123");
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            fake,
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        MatcherAssert.assertThat(
            invoice.platformInvoice(),
            Matchers.nullValue()
        );
    }

    /**
     * The PlatformInvoice is returned if the Invoice is paid
     * with a real wallet.
     */
    @Test
    public void returnsPlatformInvoiceIfRealPayment() {
        final PlatformInvoice found = Mockito.mock(PlatformInvoice.class);

        final LocalDateTime payment = LocalDateTime.now();
        final String transactionId = "transaction123";

        final Storage storage = Mockito.mock(Storage.class);
        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        Mockito.when(storage.platformInvoices()).thenReturn(all);

        Mockito.when(all.getByPayment(transactionId, payment))
            .thenReturn(found);

        final Payment successful = Mockito.mock(Payment.class);
        Mockito.when(successful.status()).thenReturn(Payment.Status.SUCCESSFUL);
        Mockito.when(successful.transactionId()).thenReturn(transactionId);
        Mockito.when(successful.paymentTime()).thenReturn(payment);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            successful,
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        MatcherAssert.assertThat(
            invoice.platformInvoice(),
            Matchers.is(found)
        );
    }

    /**
     * The stream of a pdf file is returned.
     * @throws IOException If unable to create file
     */
    @Test
    public void returnsOutputOfPdfFile() throws IOException {
        final Storage storage = new InMemory();
        final StoredProject project = new StoredProject(
            new StoredUser(
                "owner",
                "owner@example.com",
                "ARC",
                "github",
                storage
            ),
            "repo",
            "token-1234",
            new StoredProjectManager(
                0,
                "1234",
                "pm",
                "github",
                "token-1235",
                0.20,
                0.50,
                storage
            ),
            storage
        );
        final Payment payment = Mockito.mock(Payment.class);
        Mockito.when(payment.status()).thenReturn(Payment.Status.SUCCESSFUL);
        final Invoice invoice = new StoredInvoice(
            1,
            new StoredContract(
                project,
                new StoredContributor(
                    "contributor",
                    "contributor@example.com",
                    storage
                ),
                BigDecimal.ZERO,
                "DEV",
                LocalDateTime.now(),
                storage
            ),
            LocalDateTime.now(),
            payment,
            "mihai",
            "contributro",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        storage
            .invoices()
            .registerAsPaid(
                invoice,
                BigDecimal.ZERO,
                BigDecimal.ONE
            );
        storage
            .invoicedTasks()
            .register(
                invoice,
                new StoredTask(
                    project,
                    "#1234",
                    "DEV", 30,
                    false,
                    storage
                ),
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoice.toPdf(out);
        MatcherAssert.assertThat(
            out.size(),
            Matchers.greaterThan(0)
        );
    }

    /**
     * StoredInvoice.payments() reads the Invoice's payments from the storage.
     */
    @Test
    public void returnsPayments() {
        final Storage storage = Mockito.mock(Storage.class);
        final Invoice invoice = new StoredInvoice(
            1,
            Mockito.mock(Contract.class),
            LocalDateTime.now(),
            Mockito.mock(Payment.class),
            "mihai",
            "vlad",
            "RO",
            "RO",
            BigDecimal.valueOf(487),
            storage
        );
        final Payments all = Mockito.mock(Payments.class);
        final Payments ofInvoice = Mockito.mock(Payments.class);
        Mockito.when(all.ofInvoice(invoice)).thenReturn(ofInvoice);
        Mockito.when(storage.payments()).thenReturn(all);

        MatcherAssert.assertThat(
            invoice.payments(),
            Matchers.is(ofInvoice)
        );
        Mockito.verify(storage, timeout(1)).payments();
        Mockito.verify(all, timeout(1)).ofInvoice(invoice);
    }
}
