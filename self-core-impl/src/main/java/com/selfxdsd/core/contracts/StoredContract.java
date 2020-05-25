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
package com.selfxdsd.core.contracts;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.storage.Storage;

import java.math.BigDecimal;

/**
 * A Contract stored in self.
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredContract implements Contract {

    /**
     * Project of this Contract.
     */
    private final Project project;

    /**
     * Contributor of this Contract.
     */
    private final Contributor contributor;

    /**
     * Project ID.
     */
    private final BigDecimal hourlyRate;

    /**
     * The role.
     */
    private final String role;

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param project The contract's project.
     * @param contributor The contract's contributor.
     * @param hourlyRate The hourly rate.
     * @param role The role.
     * @param storage The self's storage
     */
    public StoredContract(
           final Project project,
           final Contributor contributor,
           final BigDecimal hourlyRate,
           final String role,
           final Storage storage
    ) {
        this.project = project;
        this.contributor = contributor;
        this.hourlyRate = hourlyRate;
        this.role = role;
        this.storage = storage;
    }

    /**
     * The Project.
     *
     * @return Project.
     */
    @Override
    public Project project() {
        return this.project;
    }

    /**
     * The Contributor.
     *
     * @return Contributor.
     */
    @Override
    public Contributor contributor() {
        return this.contributor;
    }

    /**
     * The Contributor's hourly rate in USD Cents.
     *
     * @return BigDecimal.
     */
    @Override
    public BigDecimal hourlyRate() {
        return this.hourlyRate;
    }

    /**
     * The Contributor's role (DEV, QA, ARCH etc).
     *
     * @return String.
     */
    @Override
    public String role() {
        return this.role;
    }
}
