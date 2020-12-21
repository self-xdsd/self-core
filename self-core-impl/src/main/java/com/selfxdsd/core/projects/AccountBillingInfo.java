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

import com.selfxdsd.api.BillingInfo;
import com.stripe.model.Account;

/**
 * BillingInfo from Stripe Connected Account.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.44
 * @todo #813:60min Implement and test this class. We should retrieve
 *  the information from the Account, most likely from the Metadata attribute.
 */
public final class AccountBillingInfo implements BillingInfo {

    /**
     * Stripe connected Account.
     */
    private final Account account;

    /**
     * Ctor.
     * @param account Stripe connected Account.
     */
    public AccountBillingInfo(final Account account) {
        this.account = account;
    }

    @Override
    public String legalName() {
        return null;
    }

    @Override
    public String country() {
        return null;
    }

    @Override
    public String address() {
        return null;
    }

    @Override
    public String city() {
        return null;
    }

    @Override
    public String zipcode() {
        return null;
    }

    @Override
    public String email() {
        return null;
    }

    @Override
    public String other() {
        return null;
    }
}
