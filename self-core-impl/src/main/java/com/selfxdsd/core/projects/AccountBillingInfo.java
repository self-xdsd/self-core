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

import java.util.HashMap;
import java.util.Map;

/**
 * BillingInfo from Stripe Connected Account.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.44
 */
public final class AccountBillingInfo implements BillingInfo {

    /**
     * Stripe account.
     */
    private final Account account;

    /**
     * Metadata of the Stripe Connected Account.
     */
    private final Map<String, String> metadata;

    /**
     * Ctor.
     * @param account Stripe connected Account.
     */
    public AccountBillingInfo(final Account account) {
        if(account.getMetadata() == null) {
            this.metadata = new HashMap<>();
        } else {
            this.metadata = account.getMetadata();
        }
        this.account = account;
    }

    @Override
    public boolean isCompany() {
        return Boolean.valueOf(this.metadata.get("isCompany"));
    }

    @Override
    public String legalName() {
        return this.metadata.get("legalName");
    }

    @Override
    public String firstName() {
        return this.metadata.get("firstName");
    }

    @Override
    public String lastName() {
        return this.metadata.get("lastName");
    }

    @Override
    public String country() {
        return this.metadata.get("country");
    }

    @Override
    public String address() {
        return this.metadata.get("address");
    }

    @Override
    public String city() {
        return this.metadata.get("city");
    }

    @Override
    public String zipcode() {
        return this.metadata.get("zipCode");
    }

    @Override
    public String email() {
        return this.account.getEmail();
    }

    @Override
    public String taxId() {
        return this.metadata.get("taxId");
    }

    @Override
    public String other() {
        return this.metadata.get("other");
    }

    @Override
    public String toString() {
        final StringBuilder billingInfo = new StringBuilder();
        final String name;
        if(this.isCompany()) {
            name = this.legalName();
        } else {
            name = this.firstName() + " " + this.lastName();
        }
        String taxId = this.taxId();
        if(taxId == null) {
            taxId = "";
        }
        String other = this.other();
        if(other == null) {
            other = "";
        }
        billingInfo
            .append(name + "\n")
            .append(
                this.address() + "; "
                + this.zipcode() + " "
                + this.city() + "; "
                + this.country() + "\n"
            ).append(this.email() + "\n")
            .append(taxId + "\n")
            .append(other);
        return billingInfo.toString();
    }
}
