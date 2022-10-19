package com.selfxdsd.core;

import com.selfxdsd.api.EmailNotification;
import com.selfxdsd.api.PlatformInvoice;

/**
 * E-mail sent to the admin of Self XDSD when a PlatformInvoice is generated.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.99
 */
final class PlatformInvoiceEmailNotification implements EmailNotification {

    private final PlatformInvoice platformInvoice;

    PlatformInvoiceEmailNotification(final PlatformInvoice platformInvoice) {
        this.platformInvoice = platformInvoice;
    }

    @Override
    public String to() {
        return "office@extremelydistributed.com";
    }

    @Override
    public String subject() {
        return "[Self XDSD] New Platform Invoice " +
            "(ID: " + this.platformInvoice.id() + ")";
    }

    @Override
    public String body() {
        return "New platform invoice (id is " + this.platformInvoice.id() +
            ") registered at " + this.platformInvoice.createdAt();
    }
}
