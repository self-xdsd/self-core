/**
 * Copyright (c) 2020, Self XDSD Contributors
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
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Github;
import com.selfxdsd.core.mock.InMemory;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Unit tests for {@link StoredProjectManager}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
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
            BigDecimal.valueOf(50),
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
            BigDecimal.valueOf(50),
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
            BigDecimal.valueOf(50),
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
     * StoredProjectManager returns its commission.
     */
    @Test
    public void returnsCommission() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            manager.commission(),
            Matchers.equalTo(BigDecimal.valueOf(50))
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
            BigDecimal.valueOf(50),
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
            BigDecimal.valueOf(50),
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
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
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
                    return "reopened";
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
                public Project project() {
                    return project;
                }

                @Override
                public String provider() {
                    return Provider.Names.GITHUB;
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
     * StoredProjectManager can handle a reopened Issue event when the initial
     * Task associated with the Issue has been finished (in this case we
     * register a new task and leave a comment).
     */
    @Test
    public void handlesTaskFinishedReopenedIssueEvent() {
        final ProjectManager manager = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "123token",
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.author()).thenReturn("mihai");
        Mockito.when(issue.repoFullName()).thenReturn("mihai/test");
        Mockito.when(issue.provider()).thenReturn("github");
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenReturn(null);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "mihai/test", "github")
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.language()).thenReturn(new English());
        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return "reopened";
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
                public Project project() {
                    return project;
                }

                @Override
                public String provider() {
                    return Provider.Names.GITHUB;
                }
            }
        );
        Mockito.verify(tasks, Mockito.times(1))
            .register(issue);
        Mockito.verify(comments, Mockito.times(1))
            .post(
                "@mihai thanks for reopening this, "
                + "I'll find someone to take a look at it again. \n"
                + "However, please keep in mind that reopening tickets "
                + "is a bad practice. "
                + "Next time, please open a new ticket."
            );
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
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.repoFullName()).thenReturn("mihai/test");
        Mockito.when(issue.provider()).thenReturn("github");
        final Comments comments = Mockito.mock(Comments.class);
        Mockito.when(comments.post(Mockito.anyString())).thenThrow(
            new IllegalStateException(
                "No comments should be posted!"
            )
        );
        Mockito.when(issue.comments()).thenReturn(comments);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.getById("1", "mihai/test", "github"))
            .thenReturn(Mockito.mock(Task.class));
        Mockito.when(all.register(issue)).thenThrow(
            new IllegalStateException(
                "There already is an ongoing task, "
              + "no new task should be registered!"
            )
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(all);

        manager.reopenedIssue(
            new Event() {
                @Override
                public String type() {
                    return "reopened";
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
                public Project project() {
                    return project;
                }

                @Override
                public String provider() {
                    return Provider.Names.GITHUB;
                }
            }
        );
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
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        final ProjectManager managerTwo = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            BigDecimal.valueOf(50),
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
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        final ProjectManager managerTwo = new StoredProjectManager(
            1,
            "123",
            "zoeself",
            Provider.Names.GITHUB,
            "1s23token",
            BigDecimal.valueOf(50),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(manager.hashCode(),
            Matchers.equalTo(managerTwo.hashCode()));
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
}
