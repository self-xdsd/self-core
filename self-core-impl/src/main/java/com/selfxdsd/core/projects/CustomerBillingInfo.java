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
import com.stripe.model.Address;
import com.stripe.model.Customer;

import java.util.Map;

/**
 * BillingInfo from Stripe Customer.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.44
 */
public final class CustomerBillingInfo implements BillingInfo {

    /**
     * Customer.
     */
    private final Customer customer;

    /**
     * Address.
     */
    private final Address address;

    /**
     * Ctor.
     * @param customer Stripe Customer.
     */
    public CustomerBillingInfo(final Customer customer) {
        this.customer = customer;
        this.address = customer.getAddress();
    }

    @Override
    public boolean isCompany() {
        final boolean isCompany;
        if(this.customer == null) {
            isCompany = false;
        } else {
            final Map<String, String> metadata = this.customer.getMetadata();
            if (metadata == null) {
                isCompany = false;
            } else {
                isCompany = Boolean.valueOf(metadata.get("isCompany"));
            }
        }
        return isCompany;
    }

    @Override
    public String legalName() {
        String name = "";
        if(this.isCompany()) {
            if (this.customer == null) {
                name = null;
            } else {
                name = this.customer.getName();
            }
        }
        return name;
    }

    @Override
    public String firstName() {
        String firstName = "";
        if(!this.isCompany()) {
            final String[] parts = this.customer.getName().split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (i < parts.length - 1) {
                    firstName = firstName + " " + parts[i];
                }
            }
        }
        return firstName.trim();
    }

    @Override
    public String lastName() {
        String lastName = "";
        if(!this.isCompany()) {
            final String[] parts = this.customer.getName().split(" ");
            lastName = parts[parts.length -1];
        }
        return lastName;
    }

    @Override
    public String country() {
        final String country;
        if(this.address == null) {
            country = "";
        } else {
            country = this.address.getCountry();
        }
        return country;
    }

    @Override
    public String address() {
        final String addr;
        if(this.address == null) {
            addr = "";
        } else {
            addr = this.address.getLine1();
        }
        return addr;
    }

    @Override
    public String city() {
        final String city;
        if(this.address == null) {
            city = "";
        } else {
            city = address.getCity();
        }
        return city;
    }

    @Override
    public String zipcode() {
        final String zipcode;
        if(this.address == null) {
            zipcode = "";
        } else {
            zipcode = address.getPostalCode();
        }
        return zipcode;
    }

    @Override
    public String email() {
        final String email;
        if(this.customer == null) {
            email = null;
        } else {
            email = this.customer.getEmail();
        }
        return email;
    }

    @Override
    public String taxId() {
        final String taxId;
        final Map<String, String> metadata = this.customer.getMetadata();
        if(metadata != null) {
            taxId = metadata.get("taxId");
        } else {
            taxId = null;
        }
        return taxId;
    }

    @Override
    public String other() {
        final String other;
        if(this.customer == null) {
            other = "";
        } else {
            final Map<String, String> metadata = this.customer.getMetadata();
            if(metadata != null) {
                other = metadata.get("other");
            } else {
                other = "";
            }
        }
        return other;
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
        billingInfo
            .append(name + "\n")
            .append(
                this.address() + "; "
                + this.zipcode() + " "
                + this.city() + "; "
                + this.country() + "\n"
            ).append(this.email() + "\n")
            .append(taxId + "\n")
            .append(this.other());
        return billingInfo.toString();
    }

}
