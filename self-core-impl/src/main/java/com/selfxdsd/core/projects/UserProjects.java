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
 * Projects belonging to a User. Pay attention:
 * this class <b>just represents</b> the projects.
 * The actual filtering has to be done in an upper layer,
 * so we can take care of e.g. pagination.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle LineLength (200 lines)
 */
public final class UserProjects extends BasePaged implements Projects {

    /**
     * User to whom these projects belong.
     */
    private final User user;

    /**
     * The projects.
     */
    private final Supplier<Stream<Project>> projects;

    /**
     * Self Storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param user The user.
     * @param projects The user's projects.
     * @param storage Self Storage.
     */
    public UserProjects(
        final User user,
        final Supplier<Stream<Project>> projects,
        final Storage storage
    ) {
        this(user, projects, storage, Page.all());
    }

    /**
     * Constructor.
     * @param user The user.
     * @param projects The user's projects.
     * @param storage Self Storage.
     * @param page Current page.
     */
    public UserProjects(
        final User user,
        final Supplier<Stream<Project>> projects,
        final Storage storage,
        final Page page
    ) {
        super(page, () -> (int) projects.get().count());
        this.user = user;
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
            "Projects of a User are immutable, "
            + "can't register a new one here. "
            + "Use Repo.activate()."
        );
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        final Page page = super.current();
        final Supplier<Stream<Project>> assigned = () -> this.projects.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> p.projectManager().id() == projectManagerId);
        return new PmProjects(projectManagerId, assigned, this.storage);
    }

    @Override
    public Projects ownedBy(final User usr) {
        if(this.user.username().equalsIgnoreCase(usr.username())
            && this.user.provider().name().equalsIgnoreCase(usr.provider().name())) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the projects of User " + this.user.username()
                + ", from provider " + this.user.provider().name()
        );
    }

    /**
     * {@inheritDoc}
     * <br>
     * Instead of parsing the owner from the repoFullName and matching
     * it with the encapsulated User first, we do it the other way around:
     * we first select the Project and then match the owner.<br>
     *
     * This is because the repo can belong to an Organization, in which case
     * the parsed owner (from the repoFullName) will differ from the User's
     * username. On the other hand, the owner saved in the DB when a Project
     * is registered is always the User who activated it in the first place.
     */
    @Override
    public Project getProjectById(
        final String repoFullName, final String repoProvider
    ) {
        Project found = this.storage.projects().getProjectById(
            repoFullName, repoProvider
        );
        if(found != null) {
            final String ownerUsername = found.owner().username();
            if(!ownerUsername.equalsIgnoreCase(this.user.username())) {
                found = null;
            }
        }
        return found;
    }

    @Override
    public Project getByWebHookToken(final String webHookToken) {
        Project found = this.storage.projects().getByWebHookToken(
            webHookToken
        );
        if(found != null) {
            final String ownerUsername = found.owner().username();
            if(!ownerUsername.equalsIgnoreCase(this.user.username())) {
                found = null;
            }
        }
        return found;
    }

    @Override
    public Projects page(final Paged.Page page) {
        return new UserProjects(this.user, this.projects, this.storage, page);
    }

    @Override
    public void remove(final Project project) {
        final User owner = project.owner();
        if(this.user.username().equalsIgnoreCase(owner.username())
            && this.user.provider().name().equalsIgnoreCase(owner.provider().name())) {
            project.deactivate(project.repo());
        } else {
            throw new IllegalStateException(
                "These are the Projects of User " + this.user.username()
                + ", from " + this.user.provider().name() + ". "
                + "You cannot remove a Project of another User here."
            );
        }
    }

    @Override
    public Project rename(final Project project, final String newName) {
        throw new UnsupportedOperationException(
            "Projects of a User are immutable, "
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
