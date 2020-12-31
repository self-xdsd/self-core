/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
package com.selfxdsd.api;

/**
 * A Contributor. Username + provider = PK.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Contributor {

    /**
     * The contributor's username.
     * @return Integer.
     */
    String username();

    /**
     * The provider (github, gitlab etc).
     * @return Provider's name.
     */
    String provider();

    /**
     * This contributor's Project Contracts.
     * @return Contracts
     */
    Contracts contracts();

    /**
     * This contributor's PayoutMethods.
     * @return PayoutMethods.
     */
    PayoutMethods payoutMethods();

    /**
     * Get a Contract if this Contributor has it.
     * @param repoFullName Repo full name.
     * @param provider Provider.
     * @param role Role.
     * @return Contract or null if it doesn't exist.
     */
    Contract contract(
        final String repoFullName,
        final String provider,
        final String role
    );

    /**
     * This contributor's tasks.
     * @return Tasks
     */
    Tasks tasks();

    /**
     * Create a Stripe Connect Account for this Contributor,
     * so we can pay them. The Connect Account will be linked
     * to the Self's Platform Account on Stripe.
     * @param billingInfo Info associated with the Stripe Account.
     * @return PayoutMethod.
     * @throws IllegalStateException If the Contributor alredy has
     *  a Stripe Account/PayoutMethod.
     */
    PayoutMethod createStripeAccount(final BillingInfo billingInfo);

    /**
     * Billing information of this Contributor. This information will
     * appear on the Invoices emitted by the Contributor to the Project,
     * at the "From" section.
     * @return BillingInfo.
     */
    BillingInfo billingInfo();
}
