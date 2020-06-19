package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import java.time.Duration;
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
