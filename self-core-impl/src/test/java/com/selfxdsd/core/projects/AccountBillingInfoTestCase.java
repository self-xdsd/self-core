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
package com.selfxdsd.core.projects;

import com.stripe.model.Account;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link AccountBillingInfo}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.45
 */
public final class AccountBillingInfoTestCase {

    /**
     * AccountBillingInfo can be formatted nicely via toString(),
     * if it's an individual (not a company).
     */
    @Test
    public void formatsIndividualBillingInfo() {
        final Account account = new Account();
        account.setEmail("amihaiemil@gmail.com");
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("isCompany", "false");
        metadata.put("firstName", "Mihai");
        metadata.put("lastName", "Andronache");
        metadata.put("country", "RO");
        metadata.put("address", "Str. Exemplu, nr 10");
        metadata.put("city", "Cluj-Napoca");
        metadata.put("zipCode", "400000");
        metadata.put("taxId", "VAT_12345");
        metadata.put("other", "C.U.I RO1234567");

        account.setMetadata(metadata);
        MatcherAssert.assertThat(
            new AccountBillingInfo(account).toString(),
            Matchers.equalTo(
                "Mihai Andronache\n"
                    + "Str. Exemplu, nr 10; 400000 Cluj-Napoca; RO\n"
                    + "amihaiemil@gmail.com\n"
                    + "VAT_12345\n"
                    + "C.U.I RO1234567"
            )
        );
    }

    /**
     * AccountBillingInfo can be formatted nicely via toString(),
     * if it's a company.
     */
    @Test
    public void formatsCompanyBillingInfo() {
        final Account account = new Account();
        account.setEmail("amihaiemil@gmail.com");
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("isCompany", "true");
        metadata.put("legalName", "Example Corp SRL");
        metadata.put("country", "RO");
        metadata.put("address", "Str. Exemplu, nr 10");
        metadata.put("city", "Cluj-Napoca");
        metadata.put("zipCode", "400000");
        metadata.put("taxId", "VAT_12345");
        metadata.put("other", "C.U.I RO1234567");

        account.setMetadata(metadata);
        MatcherAssert.assertThat(
            new AccountBillingInfo(account).toString(),
            Matchers.equalTo(
                "Example Corp SRL\n"
                    + "Str. Exemplu, nr 10; 400000 Cluj-Napoca; RO\n"
                    + "amihaiemil@gmail.com\n"
                    + "VAT_12345\n"
                    + "C.U.I RO1234567"
            )
        );
    }

}
