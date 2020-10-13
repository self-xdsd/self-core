/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Wallet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link StripeWallet}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.27
 */
public final class StripeWalletTestCase {

    /**
     * The StripeWallet can return its cash limit.
     */
    @Test
    public void returnsCashLimit() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.cash(),
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );
    }

    /**
     * The StripeWallet can return its Project.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Wallet stripe = new StripeWallet(
            project,
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.project(),
            Matchers.is(project)
        );
    }

    /**
     * The StripeWallet can return its "active" flag.
     */
    @Test
    public void returnsActiveFlag() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        MatcherAssert.assertThat(
            stripe.active(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Pay method is not yet supported.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void payIsNotYetSupported() {
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        stripe.pay(Mockito.mock(Invoice.class));
    }

    /**
     * Payment methods is not yet supported.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void paymentMethodsAreNotSupported(){
        final Wallet stripe = new StripeWallet(
            Mockito.mock(Project.class),
            BigDecimal.valueOf(1000),
            "123StripeID",
            Boolean.TRUE
        );
        stripe.paymentMethods();
    }
}
