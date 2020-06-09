package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link UnassignedTasks}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.5
 */
public final class UnassignedTaskTestCase {

    /**
     * UnassignedTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new UnassignedTasks(
            List.of(
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(3));
    }

    /**
     * Returns null when the specified Task is not found.
     */
    @Test
    public void getByIdFindsNothing() {
        final Tasks tasks = new UnassignedTasks(
            List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123", "john/test", "github"),
            Matchers.nullValue()
        );
    }

    /**
     * Returns the found Task.
     */
    @Test
    public void getByIdFindReturnsFound() {
        final Task first = Mockito.mock(Task.class);
        final Issue issueOne = this.mockIssue(
            "123",
            "john/test",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(first.issue()).thenReturn(issueOne);
        final Task second = Mockito.mock(Task.class);
        final Issue issueTwo = this.mockIssue(
            "123",
            "john/test",
            "gitlab",
            Contract.Roles.DEV
        );
        Mockito.when(second.issue()).thenReturn(issueTwo);
        final Tasks tasks = new UnassignedTasks(
            List.of(first, second),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123", "john/test", "github"),
            Matchers.is(first)
        );
    }

    /**
     * Registers a new issue.
     */
    @Test
    public void registersNewIssue(){
        final Task registered = Mockito.mock(Task.class);
        final Issue issue = this.mockIssue(
            "123",
            "john/test",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(registered.issue()).thenReturn(issue);

        final Tasks all = Mockito.mock(Tasks.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new UnassignedTasks(
            List.of(),
            storage
        );
        MatcherAssert.assertThat(tasks, Matchers.emptyIterable());
        tasks.register(issue);
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(1));

    }


    /**
     * Throws {@link UnsupportedOperationException} when calling OfContributor.
     * Contributor should not have unassigned tasks.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenGetTasksOfContributor(){
        new UnassignedTasks(List.of(), Mockito.mock(Storage.class))
            .ofContributor("foo", Provider.Names.GITHUB);
    }

    /**
     * Returns unassigned tasks of a project.
     */
    @Test
    public void tasksOfProject(){
        final Storage storage = Mockito.mock(Storage.class);

        final Project projectOne = Mockito.mock(Project.class);
        Mockito.when(projectOne.repoFullName()).thenReturn("mihai");
        Mockito.when(projectOne.provider()).thenReturn(Provider.Names.GITHUB);
        final Task taskOne = Mockito.mock(Task.class);
        Mockito.when(taskOne.project()).thenReturn(projectOne);

        final Project projectTwo = Mockito.mock(Project.class);
        Mockito.when(projectTwo.repoFullName()).thenReturn("mihai/other");
        Mockito.when(projectTwo.provider()).thenReturn(Provider.Names.GITLAB);
        final Task taskTwo = Mockito.mock(Task.class);
        Mockito.when(taskTwo.project()).thenReturn(projectTwo);

        final Tasks tasks = new UnassignedTasks(
            List.of(
                taskOne,
                taskTwo
            ),
            storage
        );

        MatcherAssert.assertThat(
            tasks.ofProject("mihai", Provider.Names.GITHUB),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Returns "itself" when call unassigned().
     */
    @Test
    public void returnsItSelf(){
        final Tasks tasks = new UnassignedTasks(
            List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.unassigned(),
            Matchers.is(tasks));
    }

    /**
     * Mock an Issue for test.
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
