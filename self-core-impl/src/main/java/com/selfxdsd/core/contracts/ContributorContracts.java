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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contracts belonging to a Contributor. Pay attention:
 * this class <b>just represents</b> the contracts.
 * The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorContracts implements Contracts {

    /**
     * ID of the Contributor.
     */
    private final int contributorId;

    /**
     * The contributor's contracts.
     */
    private final List<Contract> contracts;

    /**
     * Constructor.
     * @param contributorId Contributor ID.
     * @param contracts Contributor's contracts.
     */
    public ContributorContracts(
        final int contributorId,
        final List<Contract> contracts
    ) {
        this.contributorId = contributorId;
        this.contracts = contracts;
    }

    @Override
    public Contracts ofProject(final int projectId) {
        final List<Contract> ofProject = this.contracts
            .stream()
            .filter(contract -> contract.project().projectId() == projectId)
            .collect(Collectors.toList());
        return new ProjectContracts(projectId, ofProject);
    }

    @Override
    public Contracts ofContributor(final int contribId) {
        if(this.contributorId == contribId) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the contracts of Contributor "
          + this.contributorId + "."
        );
    }

    @Override
    public Iterator<Contract> iterator() {
        return this.contracts.iterator();
    }
}
