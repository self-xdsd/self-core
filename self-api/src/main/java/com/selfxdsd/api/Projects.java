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
package com.selfxdsd.api;

import com.selfxdsd.api.storage.Paged;

/**
 * Projects managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Projects extends Iterable<Project>, Paged {

    /**
     * Register a new Project with Self.
     * @param repo Repo to register.
     * @param manager PM who will take charge of the new Project.
     * @param webHookToken Token to secure WebHook calls from the Provider.
     * @return The registered Project.
     */
    Project register(
        final Repo repo,
        final ProjectManager manager,
        final String webHookToken
    );

    /**
     * Get the Projects assigned to a PM.
     * @param projectManagerId ID of the Project Manager.
     * @return Projects.
     */
    Projects assignedTo(final int projectManagerId);

    /**
     * Get the Project owned by the specified User.
     * @param user Owner of the projects.
     * @return Projects.
     */
    Projects ownedBy(final User user);

    /**
     * Get the Project with the corresponding ID.
     * The ID of a Project in Self is the fullname and the provider
     * of the Repo that it represents.
     * @param repoFullName Repo full name.
     * @param repoProvider Repo provider.
     * @return Project, or null if no Project with this id is found.
     */
    Project getProjectById(
        final String repoFullName,
        final String repoProvider
    );

    /**
     * Get the Projects at the provided Page.
     * @param page Page number.
     * @return Projects in a page.
     */
    Projects page(final Paged.Page page);

    /**
     * Remove specific project.
     *
     * @param project Project to remove.
     */
    void remove(final Project project);

    /**
     * Rename a Project and return the new instance.
     * @param project Project to be renamed.
     * @param newName New name of the project (simple name, without the owner).
     * @return Project.
     */
    Project rename(final Project project, final String newName);
}
