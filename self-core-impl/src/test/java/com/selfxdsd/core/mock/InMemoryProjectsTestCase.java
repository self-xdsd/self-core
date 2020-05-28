package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link InMemoryProjects}.
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryProjectsTestCase {

    /**
     * Register a project.
     */
    @Test
    public void projectRegister() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Project registered = storage.projects().register(
            this.mockRepo("mihai/test", "github"), projectManager
        );

        assertThat(
            registered.projectManager(),
            is(projectManager)
        );
        assertThat(
            registered.repoFullName(),
            equalTo("mihai/test")
        );
        assertThat(
            registered.provider(),
            equalTo("github")
        );
        assertThat(storage.projects(), contains(registered));
    }

    /**
     * Register two projects.
     */
    @Test
    public void projectRegisterForTwoProjects() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects projects = storage.projects();
        final Project first = projects.register(
            mockRepo("amihaiemil/test", "github"),
            projectManager
        );
        final Project second = projects.register(
            mockRepo("amihaiemil/test2", "github"),
            projectManager
        );

        final Projects all = storage.projects();
        assertThat(all, iterableWithSize(2));
        final Iterator<Project> iterator = all.iterator();
        assertThat(
            iterator.next().repoFullName(),
            equalTo(first.repoFullName())
        );
        assertThat(
            iterator.next().repoFullName(),
            equalTo(second.repoFullName())
        );
    }

    /**
     * InMemoryProjects can return the Projects owned by
     * a User.
     */
    @Test
    public void returnsOwnedBy() {
        final Storage storage = new InMemory();
        final Projects projects = storage.projects();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final User owner = this.mockUser("amihaiemil", "github");
        final Repo repo = this.mockRepo("amihaiemil/test", "github");
        Mockito.when(repo.owner()).thenReturn(owner);

        final Project project = projects.register(repo, projectManager);
        final Projects owned = projects.ownedBy(owner);
        MatcherAssert.assertThat(owned, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            owned.iterator().next(),
            Matchers.is(project)
        );
    }

    /**
     * Check if the right project is returned.
     */
    @Test
    public void getProjectById() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects all = storage.projects();
        final Project first = all.register(
            this.mockRepo("mihai/test", "github"),
            projectManager
        );
        all.register(
            this.mockRepo("mihai/test2", "github"),
            projectManager
        );
        final Project found = all.getProjectById(
            "mihai/test", "github"
        );
        assertThat(first, is(found));
    }

    /**
     * Check if no project is returned when id is not found..
     */
    @Test
    public void getProjectByNotFoundId() {
        final Storage storage = new InMemory();
        Projects all = storage.projects();
        Project project = all.getProjectById("octo/missing", "github");
        assertThat(project, nullValue());
    }

    /**
     * Helper method for mocking a {@link User}.
     * @param userName User name
     * @param providerName Provider name
     * @return Mocked {@link User}
     */
    @SuppressWarnings("SameParameterValue")
    private User mockUser(final String userName, final String providerName){
        final Provider provider = mock(Provider.class);
        when(provider.name()).thenReturn(providerName);

        final User user = mock(User.class);
        when(user.username()).thenReturn(userName);
        when(user.provider()).thenReturn(provider);

        return user;
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
        return repo;
    }

}
