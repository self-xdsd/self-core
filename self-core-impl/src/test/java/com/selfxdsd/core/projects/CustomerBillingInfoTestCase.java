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

import com.stripe.model.Address;
import com.stripe.model.Customer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Map;

/**
 * Unit tests for {@link CustomerBillingInfo}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.44
 */
public final class CustomerBillingInfoTestCase {

    /**
     * CustomerBillingInfo can be formatted nicely via toString().
     */
    @Test
    public void formatsBillingInfo() {
        final Customer customer = new Customer();
        customer.setName("Mihai Andronache");
        customer.setEmail("amihaiemil@gmail.com");
        customer.setMetadata(
            Map.of("other", "TVA Code: 12345")
        );
        final Address address = new Address();
        address.setCountry("RO");
        address.setCity("Cluj-Napoca");
        address.setLine1("Str. Exemplu, nr 10");
        address.setPostalCode("400000");
        customer.setAddress(address);

        MatcherAssert.assertThat(
            new CustomerBillingInfo(customer).toString(),
            Matchers.equalTo(
                "Mihai Andronache\n"
                + "Str. Exemplu, nr 10; 400000 Cluj-Napoca; RO\n"
                + "amihaiemil@gmail.com\n"
                + "TVA Code: 12345"
            )
        );
    }

}
