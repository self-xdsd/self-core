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

import com.selfxdsd.api.PlatformInvoice;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

/**
 * Unit tests for {@link PlatformInvoiceEmailNotification}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.99
 */
public final class PlatformInvoiceEmailNotificationTestCase {

    /**
     * It can return the "to" e-mail address.
     */
    @Test
    public void returnsToEmailAddress() {
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(
                Mockito.mock(PlatformInvoice.class)
            ).to(),
            Matchers.equalTo("office@extremelydistributed.com")
        );
    }

    /**
     * It can return the "to" name.
     */
    @Test
    public void returnsToName() {
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(
                Mockito.mock(PlatformInvoice.class)
            ).toName(),
            Matchers.equalTo("Self XDSD Admin")
        );
    }

    /**
     * It can return the "from" e-mail address.
     */
    @Test
    public void returnsFromEmailAddress() {
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(
                Mockito.mock(PlatformInvoice.class)
            ).from(),
            Matchers.equalTo("support@self-xdsd.com")
        );
    }

    /**
     * It can return the "from" name.
     */
    @Test
    public void returnsFromName() {
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(
                Mockito.mock(PlatformInvoice.class)
            ).fromName(),
            Matchers.equalTo("Self XDSD")
        );
    }

    /**
     * It can return the type.
     */
    @Test
    public void returnsType() {
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(
                Mockito.mock(PlatformInvoice.class)
            ).type(),
            Matchers.equalTo("PlatformInvoide Mail Notification")
        );
    }

    /**
     * It can return the subject.
     */
    @Test
    public void returnsSubject() {
        final PlatformInvoice invoice = Mockito.mock(PlatformInvoice.class);
        Mockito.when(invoice.id()).thenReturn(15);
        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(invoice).subject(),
            Matchers.equalTo("New Platform Invoice created (Id: 15)")
        );
    }

    /**
     * It can return the body.
     */
    @Test
    public void returnsBody() {
        final LocalDateTime createdAt = LocalDateTime.now();
        final PlatformInvoice invoice = Mockito.mock(PlatformInvoice.class);
        Mockito.when(invoice.id()).thenReturn(15);
        Mockito.when(invoice.createdAt()).thenReturn(createdAt);

        MatcherAssert.assertThat(
            new PlatformInvoiceEmailNotification(invoice).body(),
            Matchers.equalTo(
                "New platform invoice (id is 15) registered at "
                + createdAt + "."
            )
        );
    }

}
