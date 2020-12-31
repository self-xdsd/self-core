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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Contributor's PayoutMethods.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 */
public final class ContributorPayoutMethods implements PayoutMethods {

    /**
     * The contributor.
     */
    private final Contributor contributor;

    /**
     * The payout methods.
     */
    private final List<PayoutMethod> payoutMethods;

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param contributor The Project.
     * @param payoutMethods The Project's wallets.
     * @param storage Parent storage.
     */
    public ContributorPayoutMethods(
        final Contributor contributor,
        final List<PayoutMethod> payoutMethods,
        final Storage storage
    ) {
        this.contributor = contributor;
        this.payoutMethods = new ArrayList<>();
        this.payoutMethods.addAll(payoutMethods);
        this.storage = storage;
    }

    @Override
    public PayoutMethod register(
        final Contributor contributor,
        final String type,
        final String identifier
    ) {
        if(this.contributor.equals(contributor)) {
            for(final PayoutMethod method : this.payoutMethods) {
                if(method.type().equalsIgnoreCase(type)) {
                    throw new IllegalStateException(
                        "Payment method type [" + type + "] already exists for "
                        + "Contributor " + contributor.username() + " at "
                        + contributor.provider()
                    );
                }
            }
            final PayoutMethod registered = this.storage.payoutMethods()
                .register(
                    this.contributor,
                    type,
                    identifier
                );
            this.payoutMethods.add(registered);
            return registered;
        } else {
            throw new IllegalStateException(
                "These are the PayoutMethods of Contributor "
                + this.contributor.username() + " at "
                + this.contributor.provider() + ". "
                + "You cannot register a PayoutMethod "
                + "for another Contributor here."
            );
        }
    }

    @Override
    public PayoutMethods ofContributor(final Contributor contributor) {
        if(this.contributor.equals(contributor)) {
            return this;
        }
        throw new IllegalStateException(
            "These are the PayoutMethods of Contributor "
            + this.contributor.username() + " at "
            + this.contributor.provider() + ". "
            + "You cannot get the PayoutMethods of another contributor here."
        );
    }

    @Override
    public PayoutMethod active() {
        PayoutMethod active = null;
        for(final PayoutMethod method : this.payoutMethods) {
            if(method.active()) {
                active = method;
                break;
            }
        }
        return active;
    }

    @Override
    public PayoutMethod activate(final PayoutMethod payoutMethod) {
        if(this.contributor.equals(payoutMethod.contributor())) {
            return this.storage.payoutMethods().activate(payoutMethod);
        }
        throw new IllegalStateException(
            "These are the PayoutMethods of Contributor "
            + this.contributor.username() + " at "
            + this.contributor.provider() + ". "
            + "You cannot activate the PayoutMethod "
            + "of another Contributor here."
        );
    }

    @Override
    public Iterator<PayoutMethod> iterator() {
        return this.payoutMethods.iterator();
    }
}
