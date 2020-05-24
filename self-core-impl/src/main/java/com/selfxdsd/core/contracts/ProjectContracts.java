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
import com.selfxdsd.api.Contracts;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contracts belonging to a Project. Pay attention:
 * this class <b>just represents</b> the contracts.
 * The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectContracts implements Contracts {

    /**
     * ID of the Project.
     */
    private final int projectId;

    /**
     * The project's contracts.
     */
    private final List<Contract> contracts;

    /**
     * Constructor.
     * @param projectId Project ID.
     * @param contracts Project's contracts.
     */
    public ProjectContracts(
        final int projectId,
        final List<Contract> contracts
    ) {
        this.projectId = projectId;
        this.contracts = contracts;
    }

    @Override
    public Contracts ofProject(final int projId) {
        if(this.projectId == projId) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the contracts of Project " + this.projectId + "."
        );
    }

    @Override
    public Contracts ofContributor(final int contributorId) {
        final List<Contract> ofContributor = this.contracts
            .stream()
            .filter(
                //@checkstyle LineLength (1 line)
                contract -> contract.contributor().contributorId() == contributorId
            )
            .collect(Collectors.toList());
        return new ContributorContracts(contributorId, ofContributor);
    }

    @Override
    public Contract addContract(final int projectId,
                                final String contributorUsername,
                                final String contributorProvider,
                                final BigDecimal hourlyRate,
                                final String role) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.iterator();
    }
}
