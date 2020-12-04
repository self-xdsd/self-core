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
import com.selfxdsd.api.storage.Paged;
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
 * @todo #59:30min Implement and test method register(...).
 *  It should register the repo with respect to the fact that
 *  this is a User's Projects, meaning the given Repo has to
 *  belong to this User, the PM has to work for the same provider etc.
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
     * Constructor.
     * @param user The user.
     * @param projects The user's projects.
     */
    public UserProjects(final User user,
                        final Supplier<Stream<Project>> projects) {
        this(user, projects, new Page(1, 10));
    }

    /**
     * Constructor.
     * @param user The user.
     * @param projects The user's projects.
     * @param page Current page.
     */
    public UserProjects(final User user,
                        final Supplier<Stream<Project>> projects,
                        final Page page) {
        super(page, () -> (int) projects.get().count());
        this.user = user;
        this.projects = projects;
    }

    @Override
    public Project register(
        final Repo repo,
        final ProjectManager manager,
        final String webHookToken
    ) {
        throw new UnsupportedOperationException(
            "Not yet implemented. Use Repo.activate()."
        );
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        final Page page = super.current();
        final Supplier<Stream<Project>> assigned = () -> this.projects.get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> p.projectManager().id() == projectManagerId);
        return new PmProjects(projectManagerId, assigned);
    }

    @Override
    public Projects ownedBy(final User usr) {
        if(this.user.username().equals(usr.username())
            && this.user.provider().name().equals(usr.provider().name())) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the projects of User " + this.user.username()
                + ", from provider " + this.user.provider().name()
        );
    }

    @Override
    public Project getProjectById(
        final String repoFullName, final String repoProvider
    ) {
        final Page page = super.current();
        return projects
            .get()
            .skip((page.getNumber() - 1) * page.getSize())
            .limit(page.getSize())
            .filter(p -> p.repoFullName().equals(repoFullName)
                && p.provider().equals(repoProvider))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Projects page(final Paged.Page page) {
        return new UserProjects(this.user, this.projects, page);
    }

    @Override
    public void remove(final Project project) {
        final User owner = project.owner();
        if(this.user.username().equals(owner.username())
            && this.user.provider().name().equals(owner.provider().name())) {
            project.deactivate();
        } else {
            throw new IllegalStateException(
                "These are the Projects of User " + this.user.username()
                + ", from " + this.user.provider().name() + ". "
                + "You cannot remove a Project of another User here."
            );
        }
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
