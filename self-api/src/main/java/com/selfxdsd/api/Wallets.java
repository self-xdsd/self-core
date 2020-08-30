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
package com.selfxdsd.api;

import java.math.BigDecimal;

/**
 * Project wallets.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 */
public interface Wallets extends Iterable<Wallet> {

    /**
     * Register a new Wallet. It will be inactive by default.
     * @param project Project which owns the Wallet.
     * @param type Type of the waller (fake, stripe etc).
     * @param cash How much cash does the wallet initially hold, in cents?
     * @return The registered Wallet.
     */
    Wallet register(
        final Project project,
        final String type,
        final BigDecimal cash
    );

    /**
     * Get a Project's wallets.
     * @param project Project.
     * @return Wallets.
     */
    Wallets ofProject(final Project project);

    /**
     * Get the activa Wallet.
     * @return Wallet.
     */
    Wallet active();

    /**
     * Activate the given wallet. All the other wallets
     * of the same Project should be deactivated (only one active
     * Wallet per Project is allowed).
     * @param wallet Wallet to be activated.
     * @return Activated wallet.
     */
    Wallet activate(final Wallet wallet);

}
