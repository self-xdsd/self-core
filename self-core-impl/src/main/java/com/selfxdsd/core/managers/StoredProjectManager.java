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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Github;
import com.selfxdsd.core.Gitlab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * A Project Manager stored in Self. Use this class when implementing
 * the Storage.<br><br>
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class StoredProjectManager implements ProjectManager {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        StoredProjectManager.class
    );

    /**
     * This PMs id.
     */
    private final int id;

    /**
     * This PM's user ID.
     */
    private final String userId;

    /**
     * This PM's username.
     */
    private final String username;

    /**
     * Provider's name.
     */
    private final String provider;

    /**
     * This PM's access token.
     */
    private final String accessToken;

    /**
     * Commission for each handled Task, in cents.
     */
    private final BigDecimal commission;

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Current date time supplier. Used in testing task deadlines.
     */
    private final Supplier<LocalDateTime> dateTimeSupplier;

    /**
     * Constructor.
     * @param id PM's id.
     * @param userId PM's user ID.
     * @param username PM's username.
     * @param provider The provider's name (Gitlab, Github etc).
     * @param accessToken API Access token.
     * @param commission Commission in cents.
     * @param storage Self's storage.
     * @checkstyle ParameterNumber (10 lines)
     */
    public StoredProjectManager(
        final int id,
        final String userId,
        final String username,
        final String provider,
        final String accessToken,
        final BigDecimal commission,
        final Storage storage
    ) {
        this(id,
            userId,
            username,
            provider,
            accessToken,
            commission,
            storage,
            LocalDateTime::now);
    }

    /**
     * Constructor.
     * @param id PM's id.
     * @param userId PM's user ID.
     * @param username PM's username.
     * @param provider The provider's name (Gitlab, Github etc).
     * @param accessToken API Access token.
     * @param commission Commission in cents.
     * @param storage Self's storage.
     * @param dateTimeSupplier Current date time. Used in testing deadlines.
     * @checkstyle ParameterNumber (10 lines)
     */
    StoredProjectManager(
        final int id,
        final String userId,
        final String username,
        final String provider,
        final String accessToken,
        final BigDecimal commission,
        final Storage storage,
        final Supplier<LocalDateTime> dateTimeSupplier
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.provider = provider;
        this.accessToken = accessToken;
        this.commission = commission;
        this.storage = storage;
        this.dateTimeSupplier = dateTimeSupplier;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String userId() {
        return this.userId;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public Provider provider() {
        final Provider provider;
        if(this.provider.equals(Provider.Names.GITHUB)) {
            provider = new Github(new PmUser(this), this.storage);
        } else {
            provider = new Gitlab(new PmUser(this), this.storage);
        }
        return provider.withToken(this.accessToken);
    }

    @Override
    public Project assign(final Repo repo) {
        return this.storage.projects().register(
            repo,
            this,
            UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
        );
    }

    @Override
    public Projects projects() {
        return this.storage.projects().assignedTo(this.id);
    }

    @Override
    public BigDecimal commission() {
        return this.commission;
    }

    @Override
    public void newProject(final Event event) {
        final Step steps = new InvitePm(
            new SetupWebhook(
                lastly -> {
                    final Project project = event.project();
                    LOG.debug(
                        "Finished setting up project "
                        + project.repoFullName() + " at "
                        + project.provider()
                    );
                }
            )
        );
        steps.perform(event);
    }

    @Override
    public void newIssue(final Event event) {
        final Project project = event.project();
        final Issue issue = event.issue();
        project.tasks().register(issue);
        issue.comments().post(
            String.format(
                project.language().reply("newIssue.comment"),
                issue.author()
            )
        );
    }

    @Override
    public void reopenedIssue(final Event event) {
        final Project project = event.project();
        final Issue issue = event.issue();
        final Task task = project.tasks()
            .getById(
                issue.issueId(),
                issue.repoFullName(),
                issue.provider()
            );
        if(task == null) {
            project.tasks().register(issue);
            issue.comments().post(
                String.format(
                    project.language().reply("reopened.comment"),
                    issue.author()
                )
            );
        }
    }

    @Override
    public void unassignedTasks(final Event event) {
        final Project project = event.project();
        LOG.debug(
            "Checking the unassigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
        for(final Task task : project.tasks().unassigned()) {
            final Issue issue = task.issue();
            LOG.debug("Electing assignee for task #" + issue.issueId());
            final Contributor contributor = project.contributors().elect(task);
            if(contributor == null) {
                LOG.debug("Couldn't find any assignee, posting comment...");
                issue.comments().post(
                    String.format(
                        project.language().reply("noAssigneeFound.comment"),
                        project.owner().username(),
                        task.role()
                    )
                );
                LOG.debug("Comment for noAssigneeFound posted.");
            } else {
                LOG.debug("Elected @" + contributor.username() + ".");
                final Task assigned = task.assign(contributor);
                issue.assign(contributor.username());
                issue.comments().post(
                    String.format(
                        project.language().reply("taskAssigned.comment"),
                        contributor.username(),
                        assigned.deadline(),
                        assigned.estimation()
                    )
                );
                LOG.debug(
                    "Task #" + issue.issueId() + " assigned to @"
                    + contributor.username() + "."
                );
            }
        }
        LOG.debug(
            "Finished checking the unassigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
    }

    @Override
    public void assignedTasks(final Event event) {
        final Project project = event.project();
        LOG.debug(
            "Checking the assigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
        for(final Task task : project.tasks()) {
            final Contributor assignee = task.assignee();
            if(assignee != null) {
                final Issue issue = task.issue();
                if(issue.isClosed()) {
                    LOG.debug(
                        "Task #" + issue.issueId()
                        + " of Contributor " + assignee.username()
                        + " is closed. Invoicing... "
                    );
                    final InvoicedTask invoiced = task.contract()
                        .invoices()
                        .active()
                        .register(task, this.commission);
                    if(invoiced != null) {
                        issue.comments().post(
                            String.format(
                                project.language().reply(
                                    "taskInvoiced.comment"
                                ),
                                assignee.username()
                            )
                        );
                        this.storage.tasks().remove(task);
                        LOG.debug(
                            "Task #" + issue.issueId()
                            + " successfully invoiced and taken out of scope."
                        );
                    }
                } else {
                    final LocalDateTime now = this.dateTimeSupplier.get();
                    if (now.until(task.deadline(), ChronoUnit.MINUTES) < 0) {
                        task.unassign();
                        task.resignations()
                            .register(task, Resignations.Reason.DEADLINE);
                        issue.comments().post(
                            String.format(
                                project.language().reply(
                                    "taskDeadlineMissed.comment"
                                ),
                                assignee.username(),
                                task.deadline()
                            )
                        );
                    } else {
                        final int time = Period.between(
                            task.assignmentDate().toLocalDate(),
                            task.deadline().toLocalDate()
                        ).getDays();
                        final int left = Period.between(
                            now.toLocalDate(),
                            task.deadline().toLocalDate()
                        ).getDays();
                        if (left <= time / 2) {
                            issue.comments().post(
                                String.format(
                                    project.language().reply(
                                        "taskDeadlineReminder.comment"
                                    ),
                                    assignee.username(),
                                    task.deadline()
                                )
                            );
                        }
                    }
                }
            }
        }
        LOG.debug(
            "Finished checking the assigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof ProjectManager
            && this.id == ((ProjectManager) obj).id());
    }

    /**
     * PM as a User.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.1
     */
    static final class PmUser implements User {

        /**
         * The PM.
         */
        private final ProjectManager manager;

        /**
         * Constructor.
         * @param manager PM acting as a user.
         */
        PmUser(final ProjectManager manager) {
            this.manager = manager;
        }

        @Override
        public String username() {
            return this.manager.username();
        }

        @Override
        public String email() {
            return null;
        }

        @Override
        public String role() {
            return "user";
        }

        @Override
        public Provider provider() {
            return this.manager.provider();
        }

        @Override
        public Projects projects() {
            return this.manager.projects();
        }

        @Override
        public Contributor asContributor() {
            throw new UnsupportedOperationException(
                "The PM is never a Contributor."
            );
        }
    }
}
