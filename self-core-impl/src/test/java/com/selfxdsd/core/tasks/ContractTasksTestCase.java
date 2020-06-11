package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link com.selfxdsd.core.tasks.ContractTasks}.
 * @author criske
 * @version $Id$
 * @since 0.0.6
 */
public final class ContractTasksTestCase {


    /**
     * ContractTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
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
     * Returns the same tasks for the same contract id.
     */
    @Test
    public void returnsSameTasksOfSameContract(){
        final Contract.Id contractId = new Contract.Id("foo", "mihai",
            "github", "dev");
        final Tasks tasks = new ContractTasks(
            contractId,
            List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.ofContract(contractId),
            Matchers.is(tasks));
    }

    /**
     * Returns the other tasks different contract id.
     * @checkstyle ExecutableStatementCount (2 lines)
     */
    @Test
    public void returnsOtherTasksOfOtherContract(){
        final Storage storage = Mockito.mock(Storage.class);

        final Contract.Id contractId = new Contract.Id("foo", "mihai",
            "github", "dev");
        final Contract.Id otherContractId =
            new Contract.Id("other", "mihai2",
            "github", "dev");

        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks otherTasks = Mockito.mock(Tasks.class);
        Mockito.when(storage.tasks()).thenReturn(all);
        Mockito.when(all.ofContract(otherContractId)).thenReturn(otherTasks);

        final Task registered = Mockito.mock(Task.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.username()).thenReturn("mihai2");

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("other");
        Mockito.when(project.provider()).thenReturn("github");

        Mockito.when(registered.assignee()).thenReturn(contributor);
        Mockito.when(registered.project()).thenReturn(project);
        Mockito.when(registered.role()).thenReturn("dev");
        Mockito.when(otherTasks.spliterator())
            .thenReturn(List.of(registered).spliterator());

        final Tasks contractTasks = new ContractTasks(
            contractId,
            List.of(registered),
            storage
        );
        final Tasks otherContractTasks  = contractTasks
            .ofContract(otherContractId);

        MatcherAssert.assertThat(otherContractTasks,
            Matchers.not(contractTasks));
        MatcherAssert.assertThat(otherContractTasks,
            Matchers.iterableWithSize(1));
    }

    /**
     * Should throw when calling unassigned.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsForUnassignedTasks(){
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).unassigned();
    }

    /**
     * Should register a new task associated with an issue.
     * Right now is throwing UOE.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerTask() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).register(Mockito.mock(Issue.class));
    }


    /**
     * Should return tasks of a project.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsTasksOfProject(){
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).ofProject("foo", "github");
    }

    /**
     * Should return tasks of a contributor.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsTasksOfContributor(){
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).ofContributor("mihai", "github");
    }


    /**
     * Should return a task by id.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void returnsTaskById(){
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).getById("123", "john/repo", "github");
    }


}
