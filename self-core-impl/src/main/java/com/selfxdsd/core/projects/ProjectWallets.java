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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.Wallets;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Project's wallets.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.21
 */
public final class ProjectWallets implements Wallets {

    /**
     * The project.
     */
    private final Project project;

    /**
     * The wallets.
     */
    private final List<Wallet> wallets;

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param project The Project.
     * @param wallets The Project's wallets.
     * @param storage Parent storage.
     */
    public ProjectWallets(
        final Project project,
        final List<Wallet> wallets,
        final Storage storage
    ) {
        this.project = project;
        this.wallets = new ArrayList<>();
        this.wallets.addAll(wallets);
        this.storage = storage;
    }

    @Override
    public Wallet register(
        final Project project,
        final String type,
        final BigDecimal cash,
        final String identifier
    ) {
        if(this.project.equals(project)) {
            for(final Wallet wallet : this.wallets) {
                if(wallet.type().equalsIgnoreCase(type)) {
                    throw new IllegalStateException(
                        "Wallet type [" + type + "] already exists for "
                        + "project " + project.repoFullName() + " at "
                        + project.provider()
                    );
                }
            }
            final Wallet registered = this.storage.wallets().register(
                this.project,
                type,
                cash,
                identifier
            );
            this.wallets.add(registered);
            return registered;
        } else {
            throw new IllegalStateException(
                "These are the wallets of Project " + project.repoFullName()
                + " at " + project.provider() + ". You cannot register a "
                + "Wallet for another project here."
            );
        }
    }

    @Override
    public Wallets ofProject(final Project project) {
        if(this.project.equals(project)) {
            return this;
        }
        throw new IllegalStateException(
            "These are the wallets of Project " + project.repoFullName()
            + " at " + project.provider() + ". You cannot get the wallets of "
            + "another project here."
        );
    }

    @Override
    public Wallet active() {
        Wallet active = null;
        for(final Wallet wallet : this.wallets) {
            if(wallet.active()) {
                active = wallet;
                break;
            }
        }
        return active;
    }

    @Override
    public Wallet activate(final Wallet wallet) {
        if(this.project.equals(wallet.project())) {
            return this.storage.wallets().activate(wallet);
        }
        throw new IllegalStateException(
            "These are the wallets of Project " + project.repoFullName()
            + " at " + project.provider() + ". You cannot activate a Wallet "
            + "belonging to another Project here."
        );
    }

    @Override
    public Wallet updateCash(final Wallet wallet, final BigDecimal cash) {
        if(this.project.equals(wallet.project())) {
            return this.storage.wallets().updateCash(wallet, cash);
        }
        throw new IllegalStateException(
            "These are the wallets of Project " + project.repoFullName()
            + " at " + project.provider() + ". You update cash for a Wallet"
            + " belonging to another Project here."
        );
    }

    @Override
    public boolean remove(final Wallet wallet) {
        if(this.project.equals(wallet.project())) {
            this.wallets.remove(wallet);
            return this.storage.wallets().remove(wallet);
        }
        throw new IllegalStateException(
            "These are the wallets of Project " + project.repoFullName()
            + " at " + project.provider() + ". You cannot remove a Wallet "
            + "belonging to another Project here."
        );
    }

    @Override
    public Iterator<Wallet> iterator() {
        return this.wallets.iterator();
    }
}
