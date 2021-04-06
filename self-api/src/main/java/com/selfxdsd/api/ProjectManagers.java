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

/**
 * Project Managers API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface ProjectManagers extends Iterable<ProjectManager> {

    /**
     * Get a PM by its ID.
     * @param id ID of the PM.
     * @return ProjectManager or null if it's not found.
     */
    ProjectManager getById(final int id);

    /**
     * Get a PM by username and provider.
     * @param username Username.
     * @param provider Provider.
     * @return ProjectManager of null if it's not found.
     */
    ProjectManager getByUsername(final String username, final String provider);

    /**
     * Pick a ProjectManager from a specific repository.
     * @param provider Provider name of the repository.
     * @return ProjectManager or null if it's not found.
     */
    ProjectManager pick(final String provider);

    /**
     * Register a project manager.
     * @param userId User ID.
     * @param username User name.
     * @param provider Provider name.
     * @param accessToken Access token.
     * @param projectPercentage Commission percentage that this PM takes from
     *  the Project for each invoiced task.
     * @param contributorPercentage Commission percentage that this PM takes
     *  from the Contributor for each invoiced task.
     * @return The registered ProjectManager.
     */
    ProjectManager register(
        final String userId,
        final String username,
        final String provider,
        final String accessToken,
        final double projectPercentage,
        final double contributorPercentage
    );
}
