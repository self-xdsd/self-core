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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.selfxdsd.core.Env;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StripePayoutMethod}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 */
public final class StripePayoutMethodTestCase {

    /**
     * StripePayoutMethod can return its owner.
     */
    @Test
    public void returnsContributor() {
        final Contributor owner = Mockito.mock(Contributor.class);
        final PayoutMethod method = new StripePayoutMethod(
            owner,
            "acct_001",
            Boolean.FALSE
        );
        MatcherAssert.assertThat(
            method.contributor(),
            Matchers.is(owner)
        );
    }

    /**
     * StripePayoutMethod returns its type.
     */
    @Test
    public void returnsType() {
        final PayoutMethod method = new StripePayoutMethod(
            Mockito.mock(Contributor.class),
            "acct_001",
            Boolean.FALSE
        );
        MatcherAssert.assertThat(
            method.type(),
            Matchers.equalTo(PayoutMethod.Type.STRIPE)
        );
    }

    /**
     * StripePayoutMethod returns its status.
     */
    @Test
    public void returnsActiveFlag() {
        final PayoutMethod method = new StripePayoutMethod(
            Mockito.mock(Contributor.class),
            "acct_001",
            Boolean.FALSE
        );
        MatcherAssert.assertThat(
            method.active(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * StripePayoutMethod returns its identifier.
     */
    @Test
    public void returnsIdentifier() {
        final PayoutMethod method = new StripePayoutMethod(
            Mockito.mock(Contributor.class),
            "acct_001",
            Boolean.FALSE
        );
        MatcherAssert.assertThat(
            method.identifier(),
            Matchers.equalTo("acct_001")
        );
    }

    /**
     * StoredContributor.json() should throw an ISE
     * if the stripe.api.token env variable is not set.
     */
    @Test
    public void jsonComplainsOnMissingApiKey() {
        final PayoutMethod method = new StripePayoutMethod(
            Mockito.mock(Contributor.class),
            "acct_001",
            Boolean.TRUE
        );

        try {
            method.json();
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                    "Please specify the "
                    + Env.STRIPE_API_TOKEN
                    + " Environment Variable!"
                )
            );
        }
    }


    /**
     * StoredContributor.billingInfo() should throw an ISE
     * if the Stripe API token is not set.
     */
    @Test
    public void billingInfoComplainsOnMissingKey() {
        final PayoutMethod method = new StripePayoutMethod(
            Mockito.mock(Contributor.class),
            "acct_001",
            Boolean.TRUE
        );

        try {
            method.billingInfo();
            Assert.fail("IllegalStateException was expected.");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo(
                "Please specify the "
                    + Env.STRIPE_API_TOKEN
                    + " Environment Variable!"
                )
            );
        }
    }
}
