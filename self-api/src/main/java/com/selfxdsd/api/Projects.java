/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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

import java.util.Iterator;

/**
 * Projects managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Projects extends Iterable<Project> {

    /**
     * Register a new Project with Self.
     * @param repo Repo to register.
     * @param manager PM who will take charge of the new Project.
     * @return The registered Project.
     */
    Project register(final Repo repo, final ProjectManager manager);

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
     * Get the Project with the corresponding projectId.
     * @param projectId Id of the Project.
     * @return Project, or null if no Project with this id is found.
     */
    default Project getProjectById(final int projectId) {
        Iterator<Project> iterator = iterator();
        Project project = null;
        while (iterator.hasNext()) {
            Project nextProject = iterator.next();
            if (nextProject.projectId() == projectId) {
                project = nextProject;
                break;
            }
        }
        return project;
    }
}
