package com.selfxdsd.api;

import java.time.Duration;

/**
 * An entry in an invoice.
 * @author criske
 * @version $Id$
 * @since 0.0.3
 */
public interface InvoiceTask {

    /**
     * Time spent to complete a task, usually hours.
     * It will be used to calculate invoice's total amount.
     * @return Duration.
     */
    Duration timeSpent();

    /**
     * The completed task.
     * @return Task.
     */
    Task task();

}
