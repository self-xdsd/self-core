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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contracts.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Contracts extends Iterable<Contract> {

    /**
     * Get the Contracts belonging to a given Project.
     * @param projectId Project ID.
     * @return Contracts.
     */
    Contracts ofProject(final int projectId);

    /**
     * Get the Contracts belonging to a Contributor.
     * @param contributorId Contributor ID.
     * @return Contracts.
     */
    Contracts ofContributor(final int contributorId);

    /**
     * Empty Contracts. Return an instance of this when you cannot
     * find the Contracts you are looking for.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.1
     */
    final class Empty implements Contracts {
        @Override
        public Contracts ofProject(final int projectId) {
            return new Empty();
        }

        @Override
        public Contracts ofContributor(final int contributorId) {
            return new Empty();
        }

        @Override
        public Iterator<Contract> iterator() {
            return new ArrayList<Contract>().iterator();
        }
    }
}
