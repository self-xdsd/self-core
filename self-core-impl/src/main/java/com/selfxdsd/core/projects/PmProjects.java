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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Projects assigned to a certain PM. This class <b>just represents</b>
 * the projects. The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class PmProjects implements Projects {

    /**
     * ID of the manager.
     */
    private final int pmId;

    /**
     * Projects of the PM.
     */
    private final Iterable<Project> projects;

    /**
     * Constructor.
     * @param pmId ID of the manager.
     * @param projects Projects to choose from.
     */
    public PmProjects(final int pmId, final Iterable<Project> projects) {
        this.pmId = pmId;
        this.projects = projects;
    }

    @Override
    public Project register(final Repo repo, final ProjectManager manager) {
        throw new IllegalStateException(
            "Projects of a PM are immutable, "
          + "can't register a new one here."
        );
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        if(projectManagerId == this.pmId) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the projects of PM " + this.pmId + "."
        );
    }

    @Override
    public Projects ownedBy(final User user) {
        final List<Project> owned = new ArrayList<>();
        for(final Project project : this.projects) {
            final User owner = project.owner();
            if(owner.username().equals(user.username())
                && owner.provider().name().equals(user.provider().name())) {
                owned.add(project);
            }
        }
        return new UserProjects(user, owned);
    }

    @Override
    public Iterator<Project> iterator() {
        return this.projects.iterator();
    }
}
