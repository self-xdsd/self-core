package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link ContributorTasks}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorTasksTestCase {

    /**
     * ContributorTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new ContributorTasks(
            "foo",
            "github", List.of(
            Mockito.mock(Task.class),
            Mockito.mock(Task.class),
            Mockito.mock(Task.class)
        ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(3));
    }

    /**
     * Should register a new task associated with an issue.
     */
    @Test
    public void registerTask() {
        final Task registered = Mockito.mock(Task.class);
        final Issue issue = this.mockIssue(
            "123",
            "john/other",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(registered.issue()).thenReturn(issue);
        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.register(issue)).thenReturn(registered);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new ContributorTasks(
            "foo", "github", List.of(),
            storage
        );
        MatcherAssert.assertThat(tasks, Matchers.emptyIterable());
        tasks.register(issue);
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(1));
    }

    /**
     * Should throw when register a new task associated with an issue.
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwWhenIssueWithDifferentProvider() {
        final Issue issue = this.mockIssue(
            "123",
            "john/other",
            "github",
            Contract.Roles.DEV
        );
        final Tasks tasks = new ContributorTasks(
            "foo", "gitlab", List.of(),
            Mockito.mock(Storage.class)
        );
        tasks.register(issue);
    }

    /**
     * Should return new ContributorTask when searching for different
     * contributor + provider key.
     */
    @Test
    public void returnSameTasksForSameUserNameProvider(){
        final Tasks tasks = new ContributorTasks(
            "foo", "gitlab", List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.ofContributor("foo", "gitlab"),
            Matchers.equalTo(tasks)
        );
    }

    /**
     * Should return new ContributorTasks when searching for different
     * contributor + provider key.
     */
    @Test
    public void returnNewTasksForDiffUserNameProvider(){
        final Storage storage = Mockito.mock(Storage.class);
        final Task registered = Mockito.mock(Task.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.username()).thenReturn("mihai");
        Mockito.when(contributor.provider()).thenReturn("github");
        Mockito.when(registered.assignee()).thenReturn(contributor);
        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.spliterator())
            .thenReturn(List.of(registered).spliterator());
        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new ContributorTasks(
            "foo", "gitlab", List.of(),
            storage
        );

        Tasks tasksOfMihai = tasks.ofContributor("mihai", "github");
        MatcherAssert.assertThat(
            tasksOfMihai,
            Matchers.not(Matchers.equalTo(tasks))
        );
        MatcherAssert.assertThat(
            tasksOfMihai,
            Matchers.iterableWithSize(1)
        );
    }




    /**
     * Mock an Issue for test.
     *
     * @param issueId ID.
     * @param repoFullName Repo fullname.
     * @param provider Provider.
     * @param role Role.
     * @return Issue.
     */
    private Issue mockIssue(
        final String issueId, final String repoFullName,
        final String provider, final String role) {
        Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn(issueId);
        Mockito.when(issue.repoFullName()).thenReturn(repoFullName);
        Mockito.when(issue.provider()).thenReturn(provider);
        Mockito.when(issue.role()).thenReturn(role);
        return issue;
    }
}
