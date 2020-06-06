package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;
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
     * Checks if invoice is active.
     */
    @Test
    public void checksIfIsPaid() {
        final Storage storage = mock(Storage.class);
        Contract.Id contractId = new Contract.Id("repo", "john",
            Provider.Names.GITHUB, Contract.Roles.DEV);
        final Invoice invoice = new StoredInvoice(1, storage,
            contractId);
        final Invoices all = mock(Invoices.class);
        final Invoices ofContract = mock(Invoices.class);

        when(storage.invoices()).thenReturn(all);
        when(storage.invoices().ofContract(contractId))
            .thenReturn(ofContract);
        when(ofContract.isPaid(anyInt())).thenReturn(true);

        assertThat(invoice.isPaid(), is(true));
    }

    /**
     * Invoice has the correct id.
     */
    @Test
    public void hasCorrectId(){
        Contract.Id contractId = new Contract.Id("repo", "john",
            Provider.Names.GITHUB, Contract.Roles.DEV);
        final Invoice invoice = new StoredInvoice(1, mock(Storage.class),
            contractId);
        assertThat(invoice.invoiceId(), is(1));
    }

    /**
     * Invoice has the correct contract id.
     */
    @Test
    public void hasCorrectContractId(){
        Contract.Id contractId = new Contract.Id("repo", "john",
            Provider.Names.GITHUB, Contract.Roles.DEV);
        final Invoice invoice = new StoredInvoice(1, mock(Storage.class),
            contractId);
        assertThat(invoice.contractId(), is(contractId));
    }

    /**
     * Calculates total amount.
     */
    @Test
    public void calculatesTotalAmount() {
        final Storage storage = mock(Storage.class);
        Contract.Id contractId = new Contract.Id("repo", "john",
            Provider.Names.GITHUB, Contract.Roles.DEV);
        final Invoice invoice = new StoredInvoice(1, storage,
            contractId);
        final Invoices all = mock(Invoices.class);
        final Invoices ofContract = mock(Invoices.class);

        final Contract contract = mock(Contract.class);
        final Contracts allContracts = mock(Contracts.class);

        when(contract.hourlyRate()).thenReturn(BigDecimal.TEN);
        when(storage.contracts()).thenReturn(allContracts);
        when(allContracts.findById(contractId)).thenReturn(contract);

        when(storage.invoices()).thenReturn(all);
        when(storage.invoices().ofContract(contractId))
            .thenReturn(ofContract);
        when(ofContract.tasks(1)).thenReturn(
            List.of(
                mockInvoiceTask(Duration.ofHours(1)),
                mockInvoiceTask(Duration.ofMinutes(60 + 60))
            )
        );

        assertThat(invoice.totalAmount(), is(BigDecimal.valueOf(30)));

    }

    /**
     * Mocks an {@link InvoiceTask}.
     *
     * @param duration Duration of task.
     * @return Mocked {@link InvoiceTask}
     */
    private InvoiceTask mockInvoiceTask(final Duration duration) {
        return new InvoiceTask() {
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
