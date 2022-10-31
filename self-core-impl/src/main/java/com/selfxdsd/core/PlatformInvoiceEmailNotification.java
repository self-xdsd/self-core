/**
 * Copyright (c) 2020-2022, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
        return "New Platform Invoice created "
            + "(Id: " + this.platformInvoice.id() + ")";
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
