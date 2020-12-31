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
 * Billing info.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.44
 */
public interface BillingInfo {

    /**
     * Is it a company or an individual?
     * @return True if company/business, false if individual.
     */
    boolean isCompany();

    /**
     * Legal name of the business.
     * @return String.
     */
    String legalName();

    /**
     * First name of the individual, if this represents
     * the data of an individual.
     * @return String.
     */
    String firstName();

    /**
     * Last name of the individual, if this represents the data
     * of an individual.
     * @return String.
     */
    String lastName();

    /**
     * Country.
     * @return String.
     */
    String country();

    /**
     * Address (street, number etc).
     * @return String.
     */
    String address();

    /**
     * City.
     * @return String.
     */
    String city();

    /**
     * Zipcode (postal code).
     * @return String.
     */
    String zipcode();

    /**
     * E-Mail address.
     * @return String.
     */
    String email();

    /**
     * Tax ID (for example VAT id).
     * @return String.
     */
    String taxId();

    /**
     * Other info (e.g. company registration number).
     * @return String.
     */
    String other();
}
