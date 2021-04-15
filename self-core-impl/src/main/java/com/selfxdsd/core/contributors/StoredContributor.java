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
package com.selfxdsd.core.contributors;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import static com.stripe.param.AccountCreateParams.BusinessType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Contributor stored in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #192:30min Method tasks() here should return the tasks with
 *  respect ot the encapsulated Contracts. If the Contracts exist (!= null),
 *  then the method should only return the Tasks from these contracts.
 *  Otherwise, it should return all the Tasks of the contributor, as it
 *  does now.
 * @todo #985:240min At the moment, method billingInfo() here always reads
 *  the BillingInfo from the Stripe PayoutMethod if it exists. When we will
 *  have more types of PayoutMethod, we will need to make some big changes:
 *  the BillingInfo will have to be saved on our side as a relationship to
 *  the Contributor.
 */
public final class StoredContributor implements Contributor {

    /**
     * Username.
     */
    private final String username;

    /**
     * Provider.
     */
    private final String provider;

    /**
     * This contributor's Contracts. If they are missing,
     * they will be read from the storage.
     */
    private final Contracts contracts;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param username Username.
     * @param provider Provider.
     * @param storage Storage.
     */
    public StoredContributor(
        final String username,
        final String provider,
        final Storage storage
    ) {
        this(username, provider, null, storage);
    }

    /**
     * Constructor. Use this when you want to load
     * the Contributor's Contracts eagerly.
     * @param username Username.
     * @param provider Provider.
     * @param contracts Contributor's Contracts.
     * @param storage Storage.
     */
    public StoredContributor(
        final String username,
        final String provider,
        final Contracts contracts,
        final Storage storage
    ) {
        this.username = username;
        this.provider = provider;
        this.contracts = contracts;
        this.storage = storage;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String provider() {
        return this.provider;
    }

    @Override
    public Contracts contracts() {
        final Contracts assigned;
        if(this.contracts == null) {
            assigned = this.storage.contracts().ofContributor(this);
        } else {
            assigned = this.contracts;
        }
        return assigned;
    }

    @Override
    public PayoutMethods payoutMethods() {
        return this.storage.payoutMethods().ofContributor(this);
    }

    @Override
    public Contract contract(
        final String repoFullName,
        final String provider,
        final String role
    ) {
        return this.storage.contracts().findById(
            new Contract.Id(
                repoFullName,
                this.username,
                provider,
                role
            )
        );
    }

    @Override
    public Tasks tasks() {
        return this.storage.tasks().ofContributor(this.username, this.provider);
    }

    @Override
    public PayoutMethod createStripeAccount(final BillingInfo billingInfo) {
        final PayoutMethods methods = this.payoutMethods();
        for(final PayoutMethod method : methods) {
            if(method.type().equals(PayoutMethod.Type.STRIPE)) {
                throw new IllegalStateException(
                    "Contributor " + this.username + " at " + this.provider
                    + " already has a Stripe Connect Account."
                );
            }
        }
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                "[CREATE_STRIPE_ACCOUNT] Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final Account account = Account.create(
                this.accountParams(billingInfo)
            );
            return methods.register(
                this,
                PayoutMethod.Type.STRIPE,
                account.getId()
            );
        } catch (final StripeException ex) {
            throw new IllegalStateException(
                "Stripe threw an exception when trying to create SCA for "
                + "Contributor " + this.username + "/" + this.provider + ". ",
                ex
            );
        }
    }

    @Override
    public BillingInfo billingInfo() {
        final BillingInfo info;
        final PayoutMethod stripe = this.payoutMethods().getByType(
            PayoutMethod.Type.STRIPE
        );
        if(stripe != null) {
            info = stripe.billingInfo();
        } else {
            info = new BillingInfo() {
                @Override
                public boolean isCompany() {
                    return true;
                }

                @Override
                public String legalName() {
                    return StoredContributor.this.username;
                }

                @Override
                public String firstName() {
                    return "";
                }

                @Override
                public String lastName() {
                    return "";
                }

                @Override
                public String country() {
                    return "";
                }

                @Override
                public String address() {
                    return StoredContributor.this.provider;
                }

                @Override
                public String city() {
                    return "";
                }

                @Override
                public String zipcode() {
                    return "";
                }

                @Override
                public String email() {
                    return "";
                }

                @Override
                public String taxId() {
                    return "";
                }

                @Override
                public String other() {
                    return "";
                }

                @Override
                public String toString() {
                    return "Contributor " + this.legalName()
                        + " at " + this.address() + ".";
                }
            };
        }
        return info;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.provider);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Contributor)) {
            return false;
        }
        final Contributor other = (Contributor) obj;
        return this.username.equalsIgnoreCase(other.username())
            && this.provider.equalsIgnoreCase(other.provider());
    }

    /**
     * Create AccountCreateParams for Stripe.<br><br>
     *
     * We duplicate almost all the info in the metadata field, so we
     * can also retrieve it from Stripe. Otherwise, Stripe will not
     * give us this info when fetching an Account.
     *
     * @param info Billing info.
     * @return AccountCreateParams.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    private AccountCreateParams accountParams(final BillingInfo info) {
        final AccountCreateParams.BusinessType businessType;
        final Map<String, String> metadata = new HashMap<>();
        if(info.isCompany()) {
            businessType = COMPANY;
            metadata.put("isCompany", "true");
        } else {
            businessType = INDIVIDUAL;
            metadata.put("isCompany", "false");
        }
        final AccountCreateParams.Builder account = AccountCreateParams
            .builder()
            .setEmail(info.email())
            .setCountry(info.country())
            .setBusinessType(businessType)
            .setType(AccountCreateParams.Type.EXPRESS);
        if(businessType.equals(COMPANY)) {
            account.setCompany(
                AccountCreateParams.Company.builder()
                    .setName(info.legalName())
                    .setAddress(
                        AccountCreateParams.Company.Address.builder()
                            .setCountry(info.country())
                            .setCity(info.city())
                            .setLine1(info.address())
                            .setPostalCode(info.zipcode())
                            .build()
                    )
                .build()
            );
            metadata.put("legalName", info.legalName());
        } else {
            account.setIndividual(
                AccountCreateParams.Individual.builder()
                    .setFirstName(info.firstName())
                    .setLastName(info.lastName())
                    .setAddress(
                        AccountCreateParams.Individual.Address.builder()
                            .setCountry(info.country())
                            .setCity(info.city())
                            .setLine1(info.address())
                            .setPostalCode(info.zipcode())
                            .build()
                    )
                .build()
            );
            metadata.put("firstName", info.firstName());
            metadata.put("lastName", info.lastName());
        }
        metadata.put("country", info.country());
        metadata.put("city", info.city());
        metadata.put("address", info.address());
        metadata.put("zipCode", info.zipcode());
        final String other = info.other();
        if(other != null && !other.isEmpty()) {
            metadata.put("other", other);
        }
        final String taxId = info.taxId();
        if(taxId != null && !taxId.isEmpty()) {
            metadata.put("taxId", taxId);
        }
        account.setMetadata(metadata);
        return account.build();
    }
}
