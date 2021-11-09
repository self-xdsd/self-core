/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permission is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.Labels;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Github;
import com.selfxdsd.core.mock.InMemory;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Unit tests for {@link StoredProjectManager}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (2000 lines)
 * @checkstyle ClassFanOutComplexity (2000 lines)
 */
public final class StoredProjectManagerTestCase {

    /**
     * StoredProjectManager returns its id.
     */
    @Test
    public void returnsId() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.id(),
            Matchers.equalTo(1)
        );
    }

    /**
     * StoredProjectManager returns its user id.
     */
    @Test
    public void returnsUserId() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.userId(),
            Matchers.equalTo("123")
        );
    }

    /**
     * StoredProjectManager returns its provider.
     */
    @Test
    public void returnsProvider() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.provider(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(Github.class)
            )
        );
    }

    /**
     * StoredProjectManager returns its commission project percentage.
     */
    @Test
    public void returnsProjectPercentage() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.projectPercentage(),
            Matchers.equalTo(8.0)
        );
    }

    /**
     * StoredProjectManager returns its commission contributor percentage.
     */
    @Test
    public void returnsContributorPercentage() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.contributorPercentage(),
            Matchers.equalTo(5.0)
        );
    }

    /**
     * StoredProjectManager can calculate the effective project commission
     * for a certain sum, based on the encapsulated percentage.
     */
    @Test
    public void returnsProjectCommission() {
        final ProjectManager fixed = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            fixed.projectCommission(BigDecimal.valueOf(1000)),
            Matchers.equalTo(BigDecimal.valueOf(80))
        );
        MatcherAssert.assertThat(
            fixed.projectCommission(BigDecimal.valueOf(10000)),
            Matchers.equalTo(BigDecimal.valueOf(800))
        );
        MatcherAssert.assertThat(
            fixed.projectCommission(BigDecimal.valueOf(100)),
            Matchers.equalTo(BigDecimal.valueOf(8))
        );
        MatcherAssert.assertThat(
            fixed.projectCommission(BigDecimal.valueOf(10)),
            Matchers.equalTo(BigDecimal.valueOf(1))
        );

        final ProjectManager comma = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8.6345,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            comma.projectCommission(BigDecimal.valueOf(1000)),
            Matchers.equalTo(BigDecimal.valueOf(86))
        );
        MatcherAssert.assertThat(
            comma.projectCommission(BigDecimal.valueOf(10000)),
            Matchers.equalTo(BigDecimal.valueOf(863))
        );
        MatcherAssert.assertThat(
            comma.projectCommission(BigDecimal.valueOf(100)),
            Matchers.equalTo(BigDecimal.valueOf(9))
        );
        MatcherAssert.assertThat(
            comma.projectCommission(BigDecimal.valueOf(10)),
            Matchers.equalTo(BigDecimal.valueOf(1))
        );
    }

    /**
     * StoredProjectManager can calculate the effective contributor commission
     * for a certain sum, based on the encapsulated percentage.
     */
    @Test
    public void returnsContributorCommission() {
        final ProjectManager fixed = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            fixed.contributorCommission(BigDecimal.valueOf(1000)),
            Matchers.equalTo(BigDecimal.valueOf(50))
        );
        MatcherAssert.assertThat(
            fixed.contributorCommission(BigDecimal.valueOf(10000)),
            Matchers.equalTo(BigDecimal.valueOf(500))
        );
        MatcherAssert.assertThat(
            fixed.contributorCommission(BigDecimal.valueOf(100)),
            Matchers.equalTo(BigDecimal.valueOf(5))
        );
        MatcherAssert.assertThat(
            fixed.contributorCommission(BigDecimal.valueOf(10)),
            Matchers.equalTo(BigDecimal.valueOf(1))
        );

        final ProjectManager comma = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8.6345,
            5.6345,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            comma.contributorCommission(BigDecimal.valueOf(1000)),
            Matchers.equalTo(BigDecimal.valueOf(56))
        );
        MatcherAssert.assertThat(
            comma.contributorCommission(BigDecimal.valueOf(10000)),
            Matchers.equalTo(BigDecimal.valueOf(563))
        );
        MatcherAssert.assertThat(
            comma.contributorCommission(BigDecimal.valueOf(100)),
            Matchers.equalTo(BigDecimal.valueOf(6))
        );
        MatcherAssert.assertThat(
            comma.contributorCommission(BigDecimal.valueOf(10)),
            Matchers.equalTo(BigDecimal.valueOf(1))
        );
    }

    /**
     * StoredProjectManager returns its assigned projects.
     */
    @Test
    public void returnsProjects() {
        final Projects assigned = Mockito.mock(Projects.class);
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.assignedTo(1)).thenReturn(assigned);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projects()).thenReturn(all);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            storage
        );
        MatcherAssert.assertThat(
            manager.projects(),
            Matchers.is(assigned)
        );
    }

    /**
     * PmUser returns its username.
     */
    @Test
    public void pmUserReturnsUsername() {
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.username()).thenReturn("zoeself");
        final User pmUser = new StoredProjectManager.PmUser(manager);
        MatcherAssert.assertThat(
            pmUser.username(),
            Matchers.is("zoeself"));
    }

    /**
     * PmUser returns its role.
     */
    @Test
    public void pmUserReturnsRole() {
        final User pmUser = new StoredProjectManager.PmUser(
            Mockito.mock(ProjectManager.class)
        );
        MatcherAssert.assertThat(
            pmUser.role(),
            Matchers.is("user"));
    }

    /**
     * A PM can never be admin.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void asAdminUnsupported() {
        final User pmUser = new StoredProjectManager.PmUser(
            Mockito.mock(ProjectManager.class)
        );
        pmUser.asAdmin();
    }

    /**
     * PmUser returns its assigned projects.
     */
    @Test
    public void pmUserReturnsProjects() {
        final Projects assigned = Mockito.mock(Projects.class);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final User pmUser = new StoredProjectManager.PmUser(manager);
        Mockito.when(manager.projects()).thenReturn(assigned);
        MatcherAssert.assertThat(
            pmUser.projects(),
            Matchers.is(assigned)
        );
    }

    /**
     * PmUser returns its provider.
     */
    @Test
    public void pmUserReturnsProvider() {
        final Provider provider = Mockito.mock(Provider.class);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(provider);
        final User pmUser = new StoredProjectManager.PmUser(manager);

        MatcherAssert.assertThat(
            pmUser.provider(),
            Matchers.is(provider)
        );
    }

    /**
     * StoredProjectManager can assign a repo to the manager it represents.
     */
    @Test
    public void assignsRepo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn("john/test");
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            new InMemory()
        );
        final Project assigned = manager.assign(repo);

        MatcherAssert.assertThat(
            assigned.projectManager(),
            Matchers.is(manager)
        );
        MatcherAssert.assertThat(
            assigned.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * StoredProjectManager can handle a newIssue event.
     */
    @Test
    public void handlesNewIssueEvent() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator())
            .thenReturn(new ArrayList<Label>().iterator());
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.labels()).thenReturn(labels);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("mihai");
        Mockito.when(issue.repoFullName()).thenReturn("mihai/test");
        Mockito.when(issue.provider()).thenReturn("github");
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        manager.newIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.NEW_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(1))
            .post(
                "@mihai thank you for reporting this. "
                + "I'll assign someone to take care of it soon."
            );
    }

    /**
     * StoredProjectManager can handle a newIssue which was reported
     * by the PM itself (no reply comment should be posted).
     */
    @Test
    public void handlesNewIssueOpenedByPm() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator())
            .thenReturn(new ArrayList<Label>().iterator());
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.labels()).thenReturn(labels);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("zoeself");
        Mockito.when(issue.repoFullName()).thenReturn("mihai/test");
        Mockito.when(issue.provider()).thenReturn("github");
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        manager.newIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.NEW_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(0))
            .post(
                "@zoeself thank you for reporting this. "
                    + "I'll assign someone to take care of it soon."
            );
    }

    /**
     * StoredProjectManager can handle a reopened Issue event when the initial
     * Task associated with the Issue has been finished (in this case we
     * register a new task and leave a comment).
     */
    @Test
    public void handlesTaskFinishedIssueReopenedEvent() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.FALSE);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("mihai");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "mihai/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");

        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(1))
            .post(
                "@mihai thanks for reopening this, "
                + "I'll find someone to take a look at it. \n"
                + "However, please keep in mind that reopening tickets "
                + "is a bad practice. "
                + "Next time, please open a new ticket."
            );
    }

    /**
     * StoredProjectManager can handle a reopened Issue event when the initial
     * Task associated with the Issue has been finished (in this case we
     * register a new task). No comment should be posted since the Issue's
     * author is the PM itself.
     */
    @Test
    public void handlesTaskFinishedIssueReopenedBySelf() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.FALSE);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("zoeself");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "mihai/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");
        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(0))
            .post(Mockito.anyString());
    }

    /**
     * StoredProjectManager can handle a reopened PR event when the initial
     * Task associated with it has been finished (in this case we
     * register a new task and leave a comment).
     */
    @Test
    public void handlesTaskFinishedReopenedPullRequestEvent() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("mihai");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "mihai/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");

        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(1))
            .post(
                "@mihai thanks for reopening this PR, "
                + "I'll find someone to review it soon."
            );
    }

    /**
     * StoredProjectManager can handle a reopened PR event when the initial
     * Task associated with it has been finished (in this case we
     * register a new task). No comment should be posted since the PR's author
     * is the PM itself.
     */
    @Test
    public void handlesTaskFinishedPrReopenedBySelf() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("zoeself");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "mihai/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");
        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(0))
            .post(Mockito.anyString());
    }

    /**
     * StoredProjectManager can handle a reopened Issue event when the current
     * Task is still ongoing (doesn't do anything, actually).
     */
    @Test
    public void handlesReopenedIssueEventTaskOngoing() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenThrow(
            new IllegalStateException(
                "No comments should be posted!"
            )
        );
        Mockito.when(issue.comments()).thenReturn(comments);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.getById("1", "mihai/test", "github", Boolean.FALSE))
            .thenReturn(Mockito.mock(Task.class));
        Mockito.when(all.register(issue)).thenThrow(
            new IllegalStateException(
                "There already is an ongoing task, "
              + "no new task should be registered!"
            )
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(all);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");

        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
    }

    /**
     * StoredProjectManager can handle a reopened Issue event when Issue has
     * the 'no-task' label on it (doesn't do anything).
     */
    @Test
    public void handlesReopenedIssueEventNoTaskLabel() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Labels labels = Mockito.mock(Labels.class);
        final Label noTask = Mockito.mock(Label.class);
        Mockito.when(noTask.name()).thenReturn("no-task");
        Mockito.when(labels.iterator()).thenReturn(List.of(noTask).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenThrow(
            new IllegalStateException(
                "No comments should be posted!"
            )
        );
        Mockito.when(issue.comments()).thenReturn(comments);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.getById("1", "mihai/test", "github", Boolean.FALSE))
            .thenThrow(
                new IllegalStateException(
                    "Issue has the 'no-task' label, Tasks.getById should not "
                    + "be called!"
                )
            );
        Mockito.when(all.register(issue)).thenThrow(
            new IllegalStateException(
                "Issue has the 'no-task' label, no Task should be registered!"
            )
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(all);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn("github");

        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return Type.REOPENED_ISSUE;
                }

                @Override
                public Issue issue() {
                    return issue;
                }

                @Override
                public Comment comment() {
                    return null;
                }

                @Override
                public Commit commit() {
                    return null;
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
    }

    /**
     * StoredProjectManager.unassignedTasks(Event) works when there
     * is no assignee found (posts comment in Issue).
     */
    @Test
    public void handlesUnassignedTasksEventNoAssignee() {
        final Task task = Mockito.mock(Task.class);

        final Issue issue = Mockito.mock(Issue.class);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.role()).thenReturn("DEV");

        final Tasks unassigned = Mockito.mock(Tasks.class);
        Mockito.when(unassigned.iterator())
            .thenReturn(Arrays.asList(task).iterator());
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(ofProject.unassigned()).thenReturn(unassigned);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(ofProject);
        Mockito.when(project.language()).thenReturn(new English());
        final Contributors contributors = Mockito.mock(Contributors.class);
        Mockito.when(contributors.elect(task)).thenReturn(null);
        Mockito.when(project.contributors()).thenReturn(contributors);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        Mockito.when(project.owner()).thenReturn(owner);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.unassignedTasks(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(project, Mockito.times(1)).tasks();
        Mockito.verify(ofProject, Mockito.times(1)).unassigned();
        Mockito.verify(contributors, Mockito.times(1)).elect(task);
        Mockito.verify(comments, Mockito.times(1))
            .post(
                Mockito.startsWith(
                    "@mihai I couldn't find any assignee for this task."
                )
            );

    }

    /**
     * StoredProjectManager.unassignedTasks(Event) works when there
     * are no unassigned tasks.
     */
    @Test
    public void handlesUnassignedTasksEventNoTasks() {
        final Tasks unassigned = Mockito.mock(Tasks.class);
        Mockito.when(unassigned.iterator())
            .thenReturn(new ArrayList<Task>().iterator());
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(ofProject.unassigned()).thenReturn(unassigned);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(ofProject);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.unassignedTasks(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(project, Mockito.times(1)).tasks();
        Mockito.verify(ofProject, Mockito.times(1)).unassigned();
    }

    /**
     * StoredProjectManager.unassignedTasks(Event) elects a Contributor if
     * the Issue doesn't already have an assignee.
     */
    @Test
    public void handlesUnassignedTasksEvent() {
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        final Task assigned = Mockito.mock(Task.class);

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assign(assignee)).thenReturn(assigned);

        final Issue issue = Mockito.mock(Issue.class);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.role()).thenReturn("DEV");

        final Tasks unassigned = Mockito.mock(Tasks.class);
        Mockito.when(unassigned.iterator())
            .thenReturn(Arrays.asList(task).iterator());
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(ofProject.unassigned()).thenReturn(unassigned);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(ofProject);
        Mockito.when(project.language()).thenReturn(new English());
        final Contributors contributors = Mockito.mock(Contributors.class);
        Mockito.when(contributors.elect(task)).thenReturn(assignee);

        Mockito.when(project.contributors()).thenReturn(contributors);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        Mockito.when(project.owner()).thenReturn(owner);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.unassignedTasks(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(project, Mockito.times(1)).tasks();
        Mockito.verify(ofProject, Mockito.times(1)).unassigned();
        Mockito.verify(contributors, Mockito.times(1)).elect(task);
        Mockito.verify(task, Mockito.times(1)).assign(assignee);
        Mockito.verify(issue, Mockito.times(1)).assign("mihai");
        Mockito.verify(comments, Mockito.times(1))
            .post(
                Mockito.startsWith(
                    "@mihai this is your task now, please go ahead."
                )
            );

    }

    /**
     * StoredProjectManager.unassignedTasks(Event) assigns Issue's
     * assignee if they have the appropriate Contract with the Project.
     */
    @Test
    public void handlesUnassignedTasksEventIssueAssigneeIsContributor(){

        final Contract contract = Mockito.mock(Contract.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        Mockito.when(contract.contributor()).thenReturn(assignee);

        final Task assigned = Mockito.mock(Task.class);

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assign(assignee)).thenReturn(assigned);

        final Issue issue = Mockito.mock(Issue.class);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(issue.assignee()).thenReturn("mihai");
        Mockito.when(issue.provider()).thenReturn("github");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.role()).thenReturn("DEV");

        final Tasks unassigned = Mockito.mock(Tasks.class);
        Mockito.when(unassigned.iterator())
            .thenReturn(Arrays.asList(task).iterator());
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(ofProject.unassigned()).thenReturn(unassigned);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("self-xdsd/self-core");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(ofProject);
        Mockito.when(project.language()).thenReturn(new English());

        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(
            contracts.findById(
                new Contract.Id(
                    "self-xdsd/self-core",
                    "mihai",
                    "github",
                    "DEV"
                )
            )
        ).thenReturn(contract);

        Mockito.when(project.contracts()).thenReturn(contracts);

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        Mockito.when(project.owner()).thenReturn(owner);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.unassignedTasks(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(project, Mockito.times(1)).tasks();
        Mockito.verify(ofProject, Mockito.times(1)).unassigned();
        Mockito.verify(task, Mockito.times(1)).assign(assignee);
        Mockito.verify(issue, Mockito.times(1)).assign("mihai");
        Mockito.verify(comments, Mockito.times(1))
            .post(
                Mockito.startsWith(
                    "@mihai this is your task now, please go ahead."
                )
            );

    }

    /**
     * StoredProjectManager.unassignedTasks(Event) unassigns Issue's
     * assignee if they don't have the appropriate Contract
     * with the Project and elects new Contributor.
     */
    @Test
    public void handlesUnassignedTasksEventIssueAssigneeIsNotContributor(){

        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        final Task assigned = Mockito.mock(Task.class);

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assign(assignee)).thenReturn(assigned);

        final Issue issue = Mockito.mock(Issue.class);
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(issue.assignee()).thenReturn("john");
        Mockito.when(issue.unassign("john")).thenReturn(true);
        Mockito.when(issue.provider()).thenReturn("github");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.role()).thenReturn("DEV");

        final Tasks unassigned = Mockito.mock(Tasks.class);
        Mockito.when(unassigned.iterator())
            .thenReturn(Arrays.asList(task).iterator());
        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(ofProject.unassigned()).thenReturn(unassigned);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("self-xdsd/self-core");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(ofProject);
        Mockito.when(project.language()).thenReturn(new English());

        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(
            contracts.findById(
                new Contract.Id(
                    "self-xdsd/self-core",
                    "john",
                    "github",
                    "DEV"
                )
            )
        ).thenReturn(null);

        Mockito.when(project.contracts()).thenReturn(contracts);

        final Contributors contributors = Mockito.mock(Contributors.class);
        Mockito.when(contributors.elect(task)).thenReturn(assignee);
        Mockito.when(contributors.getById("mihai", "github"))
            .thenReturn(assignee);



        Mockito.when(project.contributors()).thenReturn(contributors);

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        Mockito.when(project.owner()).thenReturn(owner);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.unassignedTasks(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(project, Mockito.times(1)).tasks();
        Mockito.verify(ofProject, Mockito.times(1)).unassigned();
        Mockito.verify(contributors, Mockito.times(1)).elect(task);
        Mockito.verify(task, Mockito.times(1)).assign(assignee);
        Mockito.verify(issue, Mockito.times(1)).assign("mihai");
        Mockito.verify(issue, Mockito.times(1)).unassign("john");
        Mockito.verify(comments, Mockito.times(1))
            .post(
                Mockito.startsWith(
                    "@mihai this is your task now, please go ahead."
                )
            );

    }

    /**
     * StoredProjectManager.unassignedTasks(Event) removes the task if the
     * issue associated with it was closed in the meantime.
     */
    @Test
    public void handlesUnassignedTasksEventRemoveTaskOnClosedIssue() {
        final Tasks tasks = Mockito.mock(Tasks.class);
        final Tasks unassigned = Mockito.mock(Tasks.class);
        final Task task = Mockito.mock(Task.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Project project = Mockito.mock(Project.class);
        final Event event = Mockito.mock(Event.class);
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(tasks.unassigned()).thenReturn(unassigned);
        Mockito.when(unassigned.iterator())
            .thenReturn(List.of(task).iterator());
        Mockito.when(issue.isClosed()).thenReturn(Boolean.TRUE);
        Mockito.when(task.issue()).thenReturn(issue);

        manager.unassignedTasks(event);

        Mockito.verify(tasks, Mockito.times(1)).remove(task);
    }

    /**
     * Can compare two StoredProjectManager objects.
     */
    @Test
    public void comparesStoredProjectManagerObjects() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final ProjectManager managerTwo = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(manager, Matchers.equalTo(managerTwo));
    }

    /**
     * Verifies HashCode generation from StoredProjectManager.
     */
    @Test
    public void verifiesStoredProjectManagerHashcode() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final ProjectManager managerTwo = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(manager.hashCode(),
            Matchers.equalTo(managerTwo.hashCode()));
    }

    /**
     * PM can handle the "assignedTasks" Event when there are no
     * tasks in the Project.
     */
    @Test
    public void handlesAssignedTasksEventNoTasks() {
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(
            new ArrayList<Task>().iterator()
        );
        Mockito.when(project.tasks()).thenReturn(tasks);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        manager.assignedTasks(event);
        Mockito.verify(project, Mockito.never()).language();
    }

    /**
     * PM can handle the "assignedTasks" Event when there are no
     * assigned tasks in the Project.
     */
    @Test
    public void handlesAssignedTasksEventNoAssignedTasks() {
        final List<Task> mocks = new ArrayList<>();
        for(int idx = 0; idx<3; idx++) {
            final Task task = Mockito.mock(Task.class);
            Mockito.when(task.assignee()).thenReturn(null);
            mocks.add(task);
        }
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(mocks.iterator());
        Mockito.when(project.tasks()).thenReturn(tasks);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        manager.assignedTasks(event);
        Mockito.verify(project, Mockito.never()).language();
        for(int idx = 0; idx<3; idx++) {
            final Task task = mocks.get(idx);
            Mockito.verify(task, Mockito.times(1)).assignee();
            Mockito.verify(task, Mockito.never()).issue();
        }
    }

    /**
     * PM can handle the "assignedTasks" Event when there is an
     * assigned Task whose corresponding Issue is closed.
     */
    @Test
    public void handlesAssignedTasksEventClosedIssue() {
        final List<Task> mocks = new ArrayList<>();
        final Task task = Mockito.mock(Task.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isClosed()).thenReturn(Boolean.TRUE);

        Mockito.when(task.assignee()).thenReturn(assignee);
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.value()).thenReturn(BigDecimal.valueOf(1000));

        mocks.add(task);

        final InvoicedTask invoiced = Mockito.mock(InvoicedTask.class);
        final Invoice active = Mockito.mock(Invoice.class);
        Mockito.when(
            active.register(
                task, BigDecimal.valueOf(80), BigDecimal.valueOf(50)
            )
        ).thenReturn(invoiced);
        final Invoices invoices = Mockito.mock(Invoices.class);
        Mockito.when(invoices.active()).thenReturn(active);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.invoices()).thenReturn(invoices);

        Mockito.when(task.contract()).thenReturn(contract);

        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(issue.comments()).thenReturn(comments);
        Mockito.when(
            comments.post(Mockito.anyString())
        ).thenReturn(Mockito.mock(Comment.class));

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(mocks.iterator());
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.remove(task)).thenReturn(true);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            storage
        );
        manager.assignedTasks(event);
        Mockito.verify(project, Mockito.times(1)).language();
        Mockito.verify(task, Mockito.times(1)).contract();
        Mockito.verify(contract, Mockito.times(1)).invoices();
        Mockito.verify(invoices, Mockito.times(1)).active();
        Mockito.verify(active, Mockito.times(1))
            .register(task, BigDecimal.valueOf(80), BigDecimal.valueOf(50));
        Mockito.verify(comments, Mockito.times(1)).post(Mockito.anyString());
    }

    /**
     * PM can handle the "assignedTasks" Event when there is an
     * assigned Task whose deadline is closing (time passed is less than half).
     */
    @Test
    public void handlesAssignedTasksEventDeadlineClosingReminder() {

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());

        final Task task = Mockito.mock(Task.class);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(List.of(task).iterator());
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Resignations resignations = Mockito.mock(Resignations.class);
        Mockito.when(task.resignations()).thenReturn(resignations);

        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        Mockito.when(task.assignee()).thenReturn(assignee);

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isClosed()).thenReturn(Boolean.FALSE);
        Mockito.when(task.issue()).thenReturn(issue);

        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(issue.comments()).thenReturn(comments);

        final LocalDateTime assignmentDate = LocalDateTime.now();
        final LocalDateTime deadlineDate = LocalDateTime.now().plusDays(10);
        final Supplier<LocalDateTime> now = ()-> assignmentDate.plusDays(6);
        Mockito.when(task.assignmentDate()).thenReturn(assignmentDate);
        Mockito.when(task.deadline()).thenReturn(deadlineDate);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class),
            now
        );
        manager.assignedTasks(event);
        Mockito.verify(comments, Mockito.times(1))
            .post("@mihai Don't forget to close this ticket before the"
                + " deadline (" + deadlineDate.toString() + "). "
                + "You are past the first half of the allowed period.");
    }

    /**
     * PM can handle the "assignedTasks" Event when there is an
     * assigned Task whose deadline is missed.
     */
    @Test
    public void handlesAssignedTasksEventMissedDeadline() {

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.language()).thenReturn(new English());

        final Task task = Mockito.mock(Task.class);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(tasks.iterator()).thenReturn(List.of(task).iterator());
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Resignations resignations = Mockito.mock(Resignations.class);
        Mockito.when(task.resignations()).thenReturn(resignations);

        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        Mockito.when(task.assignee()).thenReturn(assignee);

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isClosed()).thenReturn(Boolean.FALSE);
        Mockito.when(task.issue()).thenReturn(issue);

        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(issue.comments()).thenReturn(comments);

        final LocalDateTime assignmentDate = LocalDateTime.now();
        final LocalDateTime deadlineDate = LocalDateTime.now().plusDays(10);
        final Supplier<LocalDateTime> now = ()-> deadlineDate.plusMinutes(1);
        Mockito.when(task.assignmentDate()).thenReturn(assignmentDate);
        Mockito.when(task.deadline()).thenReturn(deadlineDate);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class),
            now
        );
        manager.assignedTasks(event);
        Mockito.verify(task, Mockito.times(1)).unassign();
        Mockito.verify(task.resignations(), Mockito.times(1))
            .register(task, Resignations.Reason.DEADLINE);
        Mockito.verify(comments, Mockito.times(1))
            .post("@mihai Looks like you've missed the task deadline ("
                + deadlineDate.toString() + "). "
                + "You are now resigned from this task.\n\n"
                + "Please stop working on it, you will not be paid. "
                + "I will assign it to someone else soon.");
    }

    /**
     * StoredProjectManager can rename a Project.
     */
    @Test
    public void handlesRepoRenamed() {
        final Project project = Mockito.mock(Project.class);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.repoNewName()).thenReturn("newName");

        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );

        manager.renamedProject(event);

        Mockito.verify(event, Mockito.times(1)).project();
        Mockito.verify(event, Mockito.times(1)).repoNewName();
        Mockito.verify(project, Mockito.times(1)).rename("newName");
    }

    /**
     * Mock a Repo for test.
     *
     * @param fullName Full name.
     * @param provider Provider.
     * @param issues Repo issues.
     * @return Repo.
     */
    private Repo mockRepo(
        final String fullName,
        final String provider,
        final Issues issues
    ) {
        final User user = Mockito.mock(User.class);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        Mockito.when(user.provider()).thenReturn(prov);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.owner()).thenReturn(user);
        Mockito.when(repo.provider()).thenReturn(provider);
        Mockito.when(repo.issues()).thenReturn(issues);
        Mockito.when(
            prov.repo(
                fullName.substring(0, fullName.indexOf("/")),
                fullName.substring(fullName.indexOf("/") + 1)
            )
        ).thenReturn(repo);
        return repo;
    }

    /**
     * A PM can not register token.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unableToRegisterToken() {
        final User pmUser = new StoredProjectManager.PmUser(
            Mockito.mock(ProjectManager.class)
        );
        pmUser.register("foo", "secret", LocalDateTime.MIN);
    }

    /**
     * A PM can update a task estimation if issue estimation has changed.
     * @todo #1265:60min Unignore/update this test once the whole functionality
     *  is refactored to use Steps.
     */
    @Test
    @Ignore
    public void handlesIssueLabelsChangesOfEstimation() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        final Task task = Mockito.mock(Task.class);

        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(project.tasks()).thenReturn(tasks);

        Mockito.when(issue.issueId()).thenReturn("123");
        Mockito.when(issue.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(issue.repoFullName()).thenReturn("john/test");
        Mockito.when(issue.estimation()).thenReturn(() -> 30);

        Mockito.when(tasks.getById(
            "123",
            "john/test",
            Provider.Names.GITHUB,
            false
        )).thenReturn(task);
        Mockito.when(task.estimation()).thenReturn(60);

        manager.issueLabelsChanged(event);

        Mockito.verify(task, Mockito.times(1)).updateEstimation(30);
    }

    /**
     * A PM can skip handling issue labels changes if task associated with issue
     * is not found.
     */
    @Test
    public void skipHandlingIssueLabelsChanges() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        final Task task = Mockito.mock(Task.class);

        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(project.tasks()).thenReturn(tasks);

        Mockito.when(issue.issueId()).thenReturn("123");
        Mockito.when(issue.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(issue.repoFullName()).thenReturn("john/test");
        Mockito.when(issue.estimation()).thenReturn(() -> 30);

        Mockito.when(tasks.getById(
            "not-this-issue-123",
            "john/test",
            Provider.Names.GITHUB,
            false
        )).thenReturn(task);

        manager.issueLabelsChanged(event);

        Mockito.verify(task, Mockito.never())
            .updateEstimation(Mockito.anyInt());
    }

    /**
     * A PM can skip handling issue labels changes if issue is closed.
     */
    @Test
    public void skipHandlingIssueLabelsChangesOnClosedIssue() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            8,
            5,
            Mockito.mock(Storage.class)
        );
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Task task = Mockito.mock(Task.class);

        Mockito.when(issue.isClosed()).thenReturn(true);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(issue.issueId()).thenReturn("123");

        manager.issueLabelsChanged(event);

        Mockito.verify(task, Mockito.never())
            .updateEstimation(Mockito.anyInt());
    }

}
