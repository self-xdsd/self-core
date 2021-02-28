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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Github;
import com.selfxdsd.core.Gitlab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * @checkstyle ExecutableStatementCount (1000 lines)
 * @checkstyle ClassFanOutComplexity (1000 lines)
 * @since 0.0.1
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
     * Commission for each handled Task.
     */
    private final double percentage;

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
     * @param percentage Commission percentage.
     * @param storage Self's storage.
     * @checkstyle ParameterNumber (10 lines)
     */
    public StoredProjectManager(
        final int id,
        final String userId,
        final String username,
        final String provider,
        final String accessToken,
        final double percentage,
        final Storage storage
    ) {
        this(id,
            userId,
            username,
            provider,
            accessToken,
            percentage,
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
     * @param percentage Commission percentage.
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
        final double percentage,
        final Storage storage,
        final Supplier<LocalDateTime> dateTimeSupplier
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.provider = provider;
        this.accessToken = accessToken;
        this.percentage = percentage;
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
        if (this.provider.equalsIgnoreCase(Provider.Names.GITHUB)) {
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
    public double percentage() {
        return this.percentage;
    }

    @Override
    public BigDecimal commission(final BigDecimal value) {
        return value.multiply(
            BigDecimal
                .valueOf(this.percentage)
                .setScale(2, RoundingMode.HALF_UP)
        ).divide(
            BigDecimal.valueOf(100),
            0,
            RoundingMode.HALF_UP
        );
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
        final Step steps = new IssueHasLabel(
            "no-task",
            hasLabel -> LOG.debug(
                "New Issue is labeled 'no-task'. "
              + "Will not register it."
            ),
            new RegisterIssue(
                new IssueIsAssigned(
                    new IssueAssigneeHasRoles(
                        new AssignTaskToIssueAssignee(
                            sendReply -> {
                                final Issue issue = sendReply.issue();
                                final String author = issue.author();
                                if(!this.username.equalsIgnoreCase(author)) {
                                    final Project project = sendReply.project();
                                    final String reply = String.format(
                                        project.language()
                                            .reply("manualAssignment.comment"),
                                        author
                                    );
                                    issue.comments().post(reply);
                                }
                            }
                        ),
                        new UnassignIssue(
                            sendReply -> {
                                final Issue issue = sendReply.issue();
                                final String author = issue.author();
                                if(!this.username.equalsIgnoreCase(author)) {
                                    final Project project = sendReply.project();
                                    final String reply = String.format(
                                        project.language()
                                            .reply(
                                                "newIssueUnassigned.comment"
                                            ),
                                        author,
                                        issue.assignee(),
                                        issue.role()
                                    );
                                    issue.comments().post(reply);
                                }
                            }
                        ),
                        event
                    ),
                    notAssigned -> {
                        final Issue issue = notAssigned.issue();
                        final String author = issue.author();
                        if(!this.username.equalsIgnoreCase(author)) {
                            final Project project = notAssigned.project();
                            final String reply;
                            if (issue.isPullRequest()) {
                                reply = String.format(
                                    project.language()
                                        .reply("newPullRequest.comment"),
                                    issue.author()
                                );
                            } else {
                                reply = String.format(
                                    project.language()
                                        .reply("newIssue.comment"),
                                    issue.author()
                                );
                            }
                            issue.comments().post(reply);
                        }
                    }
                )
            )
        );
        steps.perform(event);
    }

    @Override
    public void reopenedIssue(final Event event) {
        final Project project = event.project();
        final Issue issue = event.issue();
        final Task task = project.tasks()
            .getById(
                issue.issueId(),
                issue.repoFullName(),
                issue.provider(),
                issue.isPullRequest()
            );
        if(task == null) {
            project.tasks().register(issue);
            final String author = issue.author();
            if(!this.username.equalsIgnoreCase(author)) {
                final String reply;
                if (issue.isPullRequest()) {
                    reply = String.format(
                        project.language().reply(
                            "reopenedPullRequest.comment"
                        ),
                        author
                    );
                } else {
                    reply = String.format(
                        project.language().reply("reopened.comment"),
                        author
                    );
                }
                issue.comments().post(reply);
            }
        }
    }

    @Override
    public void unassignedTasks(final Event event) {
        final Project project = event.project();
        LOG.debug(
            "Checking the unassigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
        final Tasks projectTasks = project.tasks();
        for(final Task task : projectTasks.unassigned()) {
            final Issue issue = task.issue();
            if (issue.isClosed()) {
                LOG.debug("Issue associated with task #" + issue.issueId()
                    + " is closed. Removing task...");
                projectTasks.remove(task);
                continue;
            }
            final String issueAssignee = issue.assignee();
            if (issueAssignee != null) {
                final Contract contract = project.contracts().findById(
                    new Contract.Id(
                        project.repoFullName(),
                        issueAssignee,
                        project.provider(),
                        task.role()
                    )
                );
                if (contract == null) {
                    LOG.debug("Unassigning @" + issueAssignee
                        + " from issue #" + issue.issueId()
                        + ". They are not contributor for project "
                        + project.repoFullName() + " at " + project.provider()
                        + ". "
                    );
                    if (issue.unassign(issueAssignee)) {
                        LOG.debug("Electing new assignee for task #"
                            + issue.issueId());
                        Contributor elected = project.contributors()
                            .elect(task);
                        this.assignTask(project, task, issue, elected);
                    } else {
                        LOG.debug("Could not unassign @" + issueAssignee
                            + " from issue #" + issue.issueId()
                            + ". New election aborted.");
                    }
                } else {
                    this.assignTask(
                        project, task, issue, contract.contributor()
                    );
                }
            } else {
                LOG.debug("Electing assignee for task #" + issue.issueId());
                final Contributor elected = project.contributors().elect(task);
                this.assignTask(project, task, issue, elected);
            }
        }
        LOG.debug(
            "Finished checking the unassigned tasks of project "
            + project.repoFullName() + " at " + project.provider()
        );
    }

    /**
     * Assigns Project's Task to a Contributor. Contributor might be null from
     * election.
     * @param project Project.
     * @param task Task.
     * @param issue Issue.
     * @param contributor Contributor, might be null.
     */
    private void assignTask(final Project project,
                            final Task task,
                            final Issue issue,
                            final Contributor contributor) {
        if (contributor == null) {
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
            final String reply;
            if(issue.isPullRequest()) {
                reply = String.format(
                    project.language().reply("pullRequestAssigned.comment"),
                    contributor.username(),
                    assigned.deadline(),
                    assigned.estimation()
                );
            } else {
                reply = String.format(
                    project.language().reply("taskAssigned.comment"),
                    contributor.username(),
                    assigned.deadline(),
                    assigned.estimation()
                );
            }
            issue.comments().post(reply);
            LOG.debug(
                "Task #" + issue.issueId() + " assigned to @"
                    + contributor.username() + "."
            );
        }
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
                        .register(task, this.commission(task.value()));
                    if(invoiced != null) {
                        issue.comments().post(
                            String.format(
                                project.language().reply(
                                    "taskInvoiced.comment"
                                ),
                                assignee.username()
                            )
                        );
                        if(issue.assignee() != null) {
                            issue.unassign(issue.assignee());
                        }
                        LOG.debug(
                            "Task #" + issue.issueId() + " successfully"
                            + " invoiced and taken out of scope."
                        );
                    }
                } else {
                    final LocalDateTime now = this.dateTimeSupplier.get();
                    if (now.until(task.deadline(), ChronoUnit.MINUTES) < 0) {
                        task.resignations()
                            .register(task, Resignations.Reason.DEADLINE);
                        task.unassign();
                        if(issue.assignee() != null) {
                            issue.unassign(issue.assignee());
                        }
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
    public void comment(final Event event) {
        final Comment comment = event.comment();
        if(this.username.equalsIgnoreCase(comment.author())) {
            return;
        }
        if(comment.body().startsWith("@" + this.username)) {
            LOG.debug(
                "Received comment [" + comment.body()
                + "] from @" + comment.author() + ". Starting conversation..."
            );
            final Conversation conversation = new IgnoreBots(
                new Understand(
                    new Hello(
                        new Status(
                            new Resign(
                                new Deregister(
                                    new Register(
                                        new Confused()
                                    )
                                )
                            )
                        )
                    )
                )
            );
            final Step steps = conversation.start(event);
            LOG.debug("Executing steps...");
            steps.perform(event);
            LOG.debug("Conversation ended.");
        }
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

        @Override
        public Admin asAdmin() {
            throw new UnsupportedOperationException(
                "A Project Manager can never be an Admin!"
            );
        }

        @Override
        public ApiTokens apiTokens() {
            throw new UnsupportedOperationException(
                "A Project Manager does not have any ApiTokens!"
            );
        }

        @Override
        public ApiToken register(
            final String name,
            final String token,
            final LocalDateTime expiration
        ) {
            throw new UnsupportedOperationException(
                "A Project Manager can not register ApiTokens!"
            );
        }
    }
}
