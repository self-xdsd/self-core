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
     * Throws IllegalStateException when getting tasks for other contract.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenGettingTasksForOtherContract() {
        final Tasks contractTasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        );
        contractTasks.ofContract(new Contract.Id("other", "mihai2",
            "github", "dev"));
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
