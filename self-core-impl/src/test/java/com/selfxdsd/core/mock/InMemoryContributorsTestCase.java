package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for class {@link InMemoryContributors}.
 *
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryContributorsTestCase {

    /**
     * Register a project.
     */
    @Test
    public void contributorRegister() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors().register("horea",
                "github");
        assertThat(storage.contributors(),
                contains(registered));
    }

    /**
     * Get contributor by id.
     */
    @Test
    public void contributorGetById() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors()
            .register("horea", "github");
        assertThat(storage.contributors()
            .getById("horea", "github"),
            is(registered));

    }

    /**
     * Get contributors of a project.
     */
    @Test
    public void returnsContributorsOfProject(){
        final Storage storage = new InMemory();
        final Project project = storage.projects().register(
            this.mockRepo("john/test", "github"),
            storage.projectManagers().pick("github"),
            "whtoken123"
        );
        final Contributor mihai = storage.contributors()
            .register("mihai", "github");
        storage.contributors()
            .register("vlad", "github");
        storage.contributors()
            .register("george", "github");

        MatcherAssert.assertThat(
            storage.contributors(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            storage.contributors()
                .ofProject("john/test", "github"),
            Matchers.iterableWithSize(0)
        );
        storage.contracts().addContract(
            project.repoFullName(),
            mihai.username(),
            mihai.provider(),
            BigDecimal.valueOf(10000),
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(
            storage.contributors()
                .ofProject("john/test", "github"),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Get contributor by an unknown id should return null.
     */
    @Test
    public void notCreatedContributorGetById() {
        final Storage storage = new InMemory();
        assertThat(storage.contributors()
                        .getById("horea", "github"),
                equalTo(null));
    }

    /**
     * Check if iterator is working properly.
     */
    @Test
    public void iteratorWorks() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors()
            .register("horea", "github");
        MatcherAssert.assertThat(
            storage.contributors(),
            Matchers.iterableWithSize(1));
    }

    /**
     * Check if iterator is working properly.
     */
    @Test
    public void iteratorWorksByPage() {
        final Storage storage = new InMemory();
        for (int i = 1; i <= 50; i++) {
            storage.contributors()
                .register("horea".repeat(i), "github");
        }
        MatcherAssert.assertThat(
            storage.contributors(),
            Matchers.iterableWithSize(50));

        final Contributors pageOne = storage.contributors()
            .page(new Paged.Page(1, 20));
        MatcherAssert.assertThat(
            pageOne,
            Matchers.iterableWithSize(20));
        MatcherAssert.assertThat(pageOne.iterator().next().username(),
            Matchers.equalTo("horea".repeat(1)));

        final Contributors pageTwo = storage.contributors()
            .page(new Paged.Page(2, 20));
        MatcherAssert.assertThat(
            pageTwo,
            Matchers.iterableWithSize(20));
        MatcherAssert.assertThat(pageTwo.iterator().next().username(),
            Matchers.equalTo("horea".repeat(21)));

        final Contributors pageThree = storage.contributors()
            .page(new Paged.Page(3, 20));
        MatcherAssert.assertThat(
            pageThree,
            Matchers.iterableWithSize(10));
        MatcherAssert.assertThat(pageThree.iterator().next().username(),
            Matchers.equalTo("horea".repeat(41)));
    }

    /**
     * Mock a Repo for test.
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(
        final String fullName,
        final String provider
    ) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }
}
