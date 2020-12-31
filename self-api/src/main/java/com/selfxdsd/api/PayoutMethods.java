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
 * Payout methods of contributors working in Self.
 * How is Self going to send money to them?
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.22
 */
public interface PayoutMethods extends Iterable<PayoutMethod> {

    /**
     * Register a new PayoutMethod for a Contributor. It will be
     * inactive by default.
     * @param contributor Contributor in question.
     * @param type Type of the payout (e.g. stripe).
     * @param identifier Identifier of the payout method.
     * @return PayoutMethod.
     */
    PayoutMethod register(
        final Contributor contributor,
        final String type,
        final String identifier
    );

    /**
     * Get a Contributor's PayoutMethods.
     * @param contributor Contributor.
     * @return PayoutMethods.
     */
    PayoutMethods ofContributor(final Contributor contributor);

    /**
     * Get the active PayoutMethod.
     * @return PayoutMethod or null if there isn't any.
     */
    PayoutMethod active();

    /**
     * Activate the given PayoutMethod. All the other payout methods
     * of the same Contributor should be deactivated (only one active
     * PayoutMethod per Contributor is allowed).
     * @param payoutMethod PayoutMethod to be activated.
     * @return Activated payout method.
     */
    PayoutMethod activate(final PayoutMethod payoutMethod);

}
