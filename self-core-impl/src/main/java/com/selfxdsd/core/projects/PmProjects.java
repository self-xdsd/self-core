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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Projects assigned to a certain PM. This class <b>just represents</b>
 * the projects. The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class PmProjects extends BasePaged implements Projects {

    /**
     * ID of the manager.
     */
    private final int pmId;

    /**
     * Projects of the PM.
     */
    private final Supplier<Stream<Project>> projects;

    /**
     * Self Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param pmId ID of the manager.
     * @param projects Projects to choose from.
     * @param storage Self Storage.
     */
    public PmProjects(
        final int pmId,
        final Supplier<Stream<Project>> projects,
        final Storage storage
    ) {
        this(pmId, projects, storage, Page.all());
    }

    /**
     * Constructor.
     * @param pmId ID of the manager.
     * @param projects Projects to choose from.
     * @param storage Self Storage.
     * @param page Current Page.
     */
    public PmProjects(
        final int pmId,
        final Supplier<Stream<Project>> projects,
        final Storage storage,
        final Page page
    ) {
        super(page, () -> (int) projects.get().count());
        this.pmId = pmId;
        this.projects = projects;
        this.storage = storage;
    }

    @Override
    public Project register(
        final Repo repo,
        final ProjectManager manager,
        final String webHookToken
    ) {
        throw new UnsupportedOperationException(
            "Projects of a PM are immutable, "
            + "can't register a new one here. "
            + "Use Repo.activate()."
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
        final Page page = super.current();
        final Supplier<Stream<Project>> owned = () -> this.projects.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> {
                final User owner = p.owner();
                return owner.username().equalsIgnoreCase(user.username())
                    && owner.provider().name()
                    .equalsIgnoreCase(user.provider().name());
            });
        return new UserProjects(user, owned, this.storage);
    }

    @Override
    public Project getProjectById(
        final String repoFullName, final String repoProvider
    ) {
        final Page page = super.current();
        return this.projects.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> p.repoFullName().equalsIgnoreCase(repoFullName)
                && p.provider().equalsIgnoreCase(repoProvider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Projects page(final Paged.Page page) {
        return new PmProjects(this.pmId, this.projects, this.storage, page);
    }

    @Override
    public void remove(final Project project) {
        if(this.pmId == project.projectManager().id()) {
            project.deactivate(project.repo());
        } else {
            throw new IllegalStateException(
                "These are the Projects of PM " + this.pmId + ". "
                + "You cannot remove a Project of another PM here."
            );
        }
    }

    @Override
    public Project rename(
        final Project project,
        final String newName
    ) {
        throw new UnsupportedOperationException(
            "Projects of a PM are immutable, "
            + "can't rename one here. "
            + "Use Project.rename(...)."
        );
    }

    @Override
    public Iterator<Project> iterator() {
        final Page page = super.current();
        return this.projects.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .iterator();
    }

}
