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

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.WalletAlreadyExistsException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Env;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Project stored in Self. Use this class whe implementing the storage.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (500 lines)
 * @todo #278:30min Continue implementation of the resolve(...) method.
 *  It should decide what kind of event has occurred and delegate it
 *  further to the ProjectManager who will deal with it. We still need
 *  the Issue Assigned case and Comment Created case.
 */
public final class StoredProject implements Project {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        StoredProject.class
    );

    /**
     * Owner of this Project.
     */
    private final User owner;

    /**
     * Repo full name.
     */
    private final String repoFullName;

    /**
     * WebHook token.
     */
    private String webHookToken;

    /**
     * Manager in charge of this Project.
     */
    private final ProjectManager projectManager;

    /**
     * Self's Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param owner Owner of the project/repo.
     * @param repoFullName Repo full name.
     * @param webHookToken Webhook token.
     * @param projectManager Manager in charge.
     * @param storage Storage of Self.
     * @checkstyle ParameterNumber (10 lines)
     */
    public StoredProject(
        final User owner,
        final String repoFullName,
        final String webHookToken,
        final ProjectManager projectManager,
        final Storage storage
    ) {
        this.owner = owner;
        this.repoFullName = repoFullName;
        this.webHookToken = webHookToken;
        this.projectManager = projectManager;
        this.storage = storage;
    }

    @Override
    public String repoFullName() {
        return this.repoFullName;
    }

    @Override
    public String provider() {
        return this.owner.provider().name();
    }

    @Override
    public User owner() {
        return this.owner;
    }

    @Override
    public Wallets wallets() {
        return this.storage.wallets().ofProject(this);
    }

    @Override
    public Wallet wallet() {
        return this.wallets().active();
    }

    @Override
    public ProjectManager projectManager() {
        return this.projectManager;
    }

    @Override
    public Repo repo() {
        return this.owner.provider().repo(
            this.repoFullName.substring(0, this.repoFullName.indexOf("/")),
            this.repoFullName.substring(this.repoFullName.indexOf("/") + 1)
        );
    }

    @Override
    public Contracts contracts() {
        return this.storage.contracts()
            .ofProject(this.repoFullName(), this.provider());
    }

    @Override
    public Contributors contributors() {
        return this.storage.contributors().ofProject(
            this.repoFullName(), this.provider()
        );
    }

    @Override
    public Tasks tasks() {
        return this.storage.tasks().ofProject(
            this.repoFullName(),
            this.provider()
        );
    }

    @Override
    public Language language() {
        return new English();
    }

    @Override
    public void resolve(final Event event) {
        final String type = event.type();
        switch (type) {
            case Event.Type.ACTIVATE:
                this.projectManager.newProject(event);
                break;
            case Event.Type.NEW_ISSUE:
                this.projectManager.newIssue(event);
                break;
            case Event.Type.REOPENED_ISSUE:
                this.projectManager.reopenedIssue(event);
                break;
            case Event.Type.UNASSIGNED_TASKS:
                this.projectManager.unassignedTasks(event);
                break;
            case Event.Type.ASSIGNED_TASKS:
                this.projectManager.assignedTasks(event);
                break;
            case Event.Type.ISSUE_COMMENT:
                this.projectManager.comment(event);
                break;
            default:
                break;
        }
    }

    @Override
    public String webHookToken() {
        return this.webHookToken;
    }

    /**
     * {@inheritDoc}
     *
     * We take the Repo as method parameter because StoredProject.repo()
     * will return an unauthenticated Repo in this scenario (we do not save
     * the User's authentication token in the DB).<br><br>
     *
     * The User has to give us an authenticated Repo when calling
     * this method, in order to uninvite the PM and remove any Self Webhooks.
     */
    @Override
    public Repo deactivate(final Repo repo) {
        final String provider = this.provider();
        LOG.debug(
            "Deactivating Project " + this.repoFullName
            + " at " + provider + "... "
        );
        final int count = this.contracts().count();
        if(count > 0) {
            LOG.error(
                "Project still has " + count + " contracts. "
                + "It cannot be deactivated."
            );
            throw new IllegalStateException(
                "Project " + this.repoFullName + " at " + provider
                + " still has " + count + " contracts. "
                + "A project can only be removed after all "
                + "its contracts are removed."
            );
        }
        this.storage.projects().remove(this);
        LOG.debug("Project successfully deactivated (removed).");
        boolean noWebhooks = repo.webhooks().remove();
        if(noWebhooks) {
            LOG.debug("Successfully removed webhooks.");
        } else {
            LOG.error("Problem while removing webhooks.");
        }
        final String user;
        if(Provider.Names.GITHUB.equalsIgnoreCase(provider)) {
            user = this.projectManager.username();
        } else if (Provider.Names.GITLAB.equalsIgnoreCase(provider)) {
            user = this.projectManager.userId();
        } else {
            throw new IllegalStateException(
                "Unknown Provider: [" + provider + "]."
            );
        }
        boolean noPm = repo.collaborators().remove(user);
        if(noPm) {
            LOG.debug("PM is no longer a Repo collaborator.");
        } else {
            LOG.error("Problem while removing PM from repo Collaborators.");
        }
        return repo;
    }

    @Override
    public Wallet createStripeWallet(final BillingInfo billingInfo) {
        LOG.debug(
            "Creating STRIPE wallet for Project " + this.repoFullName
            + " at " + this.provider() + "... "
        );
        final Wallets wallets = this.wallets();
        for(final Wallet wallet : wallets) {
            if(wallet.type().equalsIgnoreCase(Wallet.Type.STRIPE)) {
                LOG.error(
                    "STRIPE wallet already exists, can't create a second one."
                );
                throw new WalletAlreadyExistsException(
                    this, Wallet.Type.STRIPE
                );
            }
        }
        final String apiToken = System.getenv(Env.STRIPE_API_TOKEN);
        if(apiToken == null || apiToken.trim().isEmpty()) {
            LOG.error("Stripe API Token missing!");
            throw new IllegalStateException(
                "Please specify the "
                + Env.STRIPE_API_TOKEN
                + " Environment Variable!"
            );
        }
        Stripe.apiKey = apiToken;
        try {
            final Map<String, String> metadata = new HashMap<>();
            metadata.put("isCompany", String.valueOf(billingInfo.isCompany()));
            final String taxId = billingInfo.taxId();
            if(taxId != null && !taxId.isEmpty()) {
                metadata.put("taxId", taxId);
            }
            metadata.put("other", billingInfo.other());
            final String name;
            if(billingInfo.isCompany()) {
                name = billingInfo.legalName();
            } else {
                name = billingInfo.firstName() + " " + billingInfo.lastName();
            }
            final Customer customer = Customer.create(
                CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(billingInfo.email())
                    .setAddress(
                        CustomerCreateParams.Address.builder()
                            .setCountry(billingInfo.country())
                            .setCity(billingInfo.city())
                            .setLine1(billingInfo.address())
                            .setPostalCode(billingInfo.zipcode())
                            .build()
                    )
                    .setMetadata(metadata)
                    .setDescription(
                        this.repoFullName + " at " + this.provider()
                    )
                    .build()
            );
            LOG.debug("Created STRIPE Wallet [" + customer.getId() + "].");
            return this.storage.wallets().register(
                this, Wallet.Type.STRIPE,
                BigDecimal.valueOf(0), customer.getId()
            );
        } catch (final StripeException ex) {
            LOG.error(
                "StripeException while trying to create the wallet.",
                ex
            );
            throw new IllegalStateException(
                "Stripe threw an exception when trying to create a Wallet "
                + "(Customer) for Project " + this.repoFullName + " at "
                + this.provider() + ". ",
                ex
            );
        }
    }

    @Override
    public BillingInfo billingInfo() {
        return this.wallet().billingInfo();
    }

    @Override
    public Storage storage() {
        return this.storage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repoFullName, this.provider());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Project)) {
            return false;
        }
        final Project other = (Project) obj;
        return this.repoFullName.equalsIgnoreCase(other.repoFullName())
            && this.provider().equalsIgnoreCase(other.provider());
    }
}
