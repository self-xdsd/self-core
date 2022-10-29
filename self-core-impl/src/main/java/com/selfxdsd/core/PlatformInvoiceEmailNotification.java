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

    /**
     * PlatformInvoice for which we send the notification.
     */
    private final PlatformInvoice platformInvoice;

    /**
     * Ctor.
     * @param platformInvoice PlatformInvoice in the notification.
     */
    PlatformInvoiceEmailNotification(final PlatformInvoice platformInvoice) {
        this.platformInvoice = platformInvoice;
    }

    @Override
    public String to() {
        return "office@extremelydistributed.com";
    }

    @Override
    public String toName() {
        return "Self XDSD Admin";
    }

    @Override
    public String subject() {
        return "New Platform Invoice "
            + "(ID: " + this.platformInvoice.id() + ")";
    }

    @Override
    public String body() {
        return "New platform invoice (id is " + this.platformInvoice.id()
            + ") registered at " + this.platformInvoice.createdAt() + ".";
    }

    @Override
    public String type() {
        return "PlatformInvoide Mail Notification";
    }
}
