package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
            )::stream,
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
            Stream::empty,
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
        final Project projectFirst = Mockito.mock(Project.class);
        Mockito.when(projectFirst.repoFullName()).thenReturn("john/test");
        Mockito.when(projectFirst.provider()).thenReturn("github");
        Mockito.when(first.project()).thenReturn(projectFirst);
        Mockito.when(first.issueId()).thenReturn("123first");

        final Task second = Mockito.mock(Task.class);
        final Project projectSecond = Mockito.mock(Project.class);
        Mockito.when(projectSecond.repoFullName()).thenReturn("john/test2");
        Mockito.when(projectSecond.provider()).thenReturn("github");
        Mockito.when(second.project()).thenReturn(projectSecond);
        Mockito.when(second.issueId()).thenReturn("123second");

        final Tasks tasks = new UnassignedTasks(
            List.of(first, second)::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123first", "john/test", "github"),
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

        final List<Task> allSrc = new ArrayList<>();
        final Tasks all = Mockito.mock(Tasks.class);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);
        Mockito.when(all.spliterator()).thenReturn(allSrc.spliterator());

        final Tasks tasks = new UnassignedTasks(
            allSrc::stream,
            storage
        );
        Mockito.when(all.register(Mockito.any(Issue.class)))
                .thenAnswer(invocation -> {
                    allSrc.add(registered);
                    return registered;
                });
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
        new UnassignedTasks(Stream::empty, Mockito.mock(Storage.class))
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
            List.of(taskOne, taskTwo)::stream,
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
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.unassigned(),
            Matchers.is(tasks));
    }

    /**
     * Throws {@link UnsupportedOperationException} when calling ofContract.
     * Contracts should not have unassigned tasks.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenGetTasksOfContract(){
        new UnassignedTasks(Stream::empty, Mockito.mock(Storage.class))
            .ofContract(new Contract.Id("john/repo", "mihai",
                Provider.Names.GITHUB, Contract.Roles.DEV));
    }

    /**
     * Throws {@link UnsupportedOperationException} when calling unassign.
     * Tasks here are already unassigned.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenUnassigningTask(){
        new UnassignedTasks(Stream::empty, Mockito.mock(Storage.class))
            .unassign(Mockito.mock(Task.class));
    }

    /**
     * Can remove a task from storage if task is part of UnassignedTasks.
     */
    @Test
    public void removesTask() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = new UnassignedTasks(
            () -> Stream.of(task),
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.repoFullName()).thenReturn("john/test");
        Mockito.when(issue.provider()).thenReturn("github");
        Mockito.when(task.issueId()).thenReturn("1");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(storage.tasks()).thenReturn(all);

        tasks.remove(task);
        Mockito.verify(storage.tasks(), Mockito.times(1)).remove(task);
    }

    /**
     * Throws ISE when task is not part of UnassignedTasks.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenRemovingTaskNotPartOf() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks =new UnassignedTasks(
            Stream::empty,
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.repoFullName()).thenReturn("john/test");
        Mockito.when(issue.provider()).thenReturn("github");
        Mockito.when(task.issueId()).thenReturn("1");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(storage.tasks()).thenReturn(all);

        tasks.remove(task);
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
