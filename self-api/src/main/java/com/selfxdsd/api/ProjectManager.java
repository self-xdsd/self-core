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

import java.math.BigDecimal;

/**
 * A Project Manager.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface ProjectManager {

    /**
     * The ProjectManager's ID.
     * @checkstyle MethodName (5 lines)
     * @return Integer id.
     */
    int id();

    /**
     * The PM's user ID from the Provider.
     * @return String.
     */
    String userId();

    /**
     * The PM's username.
     * @return String.
     */
    String username();

    /**
     * The provider (Github, Gitlab etc).
     * @return Provider.
     */
    Provider provider();

    /**
     * Assign a repo to this ProjectManager.
     * @param repo Repo to be assigned.
     * @return Project.
     */
    Project assign(final Repo repo);

    /**
     * Projects being managed by this PM.
     * @return Projects.
     */
    Projects projects();

    /**
     * The commission percentage that this PM will earn <b>from the Project</b>
     * for every handled Task.
     * @return Double.
     */
    double projectPercentage();

    /**
     * Calculate the effective project commission that the PM will earn for
     * a sum of money, in cents.
     * @param value Commissioned value, in cents.
     * @return Effective commission, in cents.
     */
    BigDecimal projectCommission(final BigDecimal value);

    /**
     * The commission percentage that this PM will earn <b>from the
     * Contributor</b> for every handled Task.
     * @return Double.
     */
    double contributorPercentage();

    /**
     * Calculate the effective contributor commission that the PM will earn for
     * a sum of money, in cents.
     * @param value Commissioned value, in cents.
     * @return Effective commission, in cents.
     */
    BigDecimal contributorCommission(final BigDecimal value);

    /**
     * Handle the "activate" (new project registered) event.
     * @param event Event.
     */
    void newProject(final Event event);

    /**
     * Handle the "newIssue" event.
     * @param event Event.
     */
    void newIssue(final Event event);

    /**
     * Handle the "reopened" Issue event.
     * @param event Event.
     */
    void reopenedIssue(final Event event);

    /**
     * Handle the "unassinged tasks" event.
     * @param event Event.
     */
    void unassignedTasks(final Event event);

    /**
     * Handle the "assigned tasks" event.
     * @param event Event.
     */
    void assignedTasks(final Event event);

    /**
     * Handle a comment.
     * @param event Event.
     */
    void comment(final Event event);

    /**
     * Handle renaming of a Project.
     * @param event Event.
     */
    void renamedProject(final Event event);
}
