package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            this.mockRepo("mihai/test", "github"),
            projectManager,
            "whtoken123"
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
            projectManager,
            "whtoken123"
        );
        final Project second = projects.register(
            mockRepo("amihaiemil/test2", "github"),
            projectManager,
            "whtoken124"
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

        final Project project = projects.register(
            repo, projectManager, "whtoken123"
        );
        final Projects owned = projects.ownedBy(owner);
        MatcherAssert.assertThat(owned, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            owned.iterator().next(),
            Matchers.is(project)
        );
    }

    /**
     * InMemoryProjects can return the Projects owned by
     * a User in a Page.
     */
    @Test
    public void returnsOwnedByPaginated() {
        final Storage storage = new InMemory();
        final Projects projects = storage.projects();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final User owner = this.mockUser("amihaiemil", "github");
        IntStream.rangeClosed(1, 10)
            .mapToObj(i -> this.mockRepo("amihaiemil/test"+i, "github"))
            .collect(Collectors.toUnmodifiableList())
            .forEach(repo -> {
                Mockito.when(repo.owner()).thenReturn(owner);
                projects.register(repo,
                    projectManager,
                    "whtoken123"+ repo.fullName());
            });
        final Projects owned = projects
            .page(new Paged.Page(2, 2))
            .ownedBy(owner);
        MatcherAssert.assertThat(owned, Matchers.iterableWithSize(2));
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
            projectManager,
            "whtoken123"
        );
        all.register(
            this.mockRepo("mihai/test2", "github"),
            projectManager,
            "whtoken124"
        );
        final Project found = all.getProjectById(
            "mihai/test", "github"
        );
        assertThat(first, is(found));
    }


    /**
     * Check if the right project is returned in page.
     */
    @Test
    public void getProjectByIdInPage() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects all = storage.projects();
        IntStream.rangeClosed(1, 5)
            .mapToObj(i -> this.mockRepo("amihaiemil/test" + i, "github"))
            .collect(Collectors.toUnmodifiableList())
            .forEach(repo -> all.register(repo,
                projectManager,
                "whtoken123" + repo.fullName()));
        final Project found = all
            .page(new Paged.Page(3, 2))
            .getProjectById("amihaiemil/test1", "github");
        assertThat(found, Matchers.notNullValue());
    }

    /**
     * Check if no project is returned when id is not found.
     */
    @Test
    public void getProjectByNotFoundId() {
        final Storage storage = new InMemory();
        Projects all = storage.projects();
        Project project = all.getProjectById("octo/missing", "github");
        assertThat(project, nullValue());
    }

    /**
     * Check if no project is returned when id is not found in page, even
     * though existing overall.
     */
    @Test
    public void getProjectByNotFoundIdInPage() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects all = storage.projects();
        IntStream.rangeClosed(1, 5)
            .mapToObj(i -> this.mockRepo("amihaiemil/test" + i, "github"))
            .collect(Collectors.toUnmodifiableList())
            .forEach(repo -> all.register(repo,
                projectManager,
                "whtoken123" + repo.fullName()));
        final Project found = all
            .page(new Paged.Page(3, 2))
            .getProjectById("amihaiemil/test3", "github");
        assertThat(found, Matchers.nullValue());
    }


    /**
     * Iterates projects in a given page.
     */
    @Test
    public void iteratesProjectsPaged() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects all = storage.projects();
        IntStream.rangeClosed(1, 10)
            .mapToObj(i -> this.mockRepo("amihaiemil/test" + i, "github"))
            .collect(Collectors.toUnmodifiableList())
            .forEach(repo -> all.register(repo,
                projectManager,
                "whtoken123" + repo.fullName()));
        assertThat(all.page(new Paged.Page(2, 3)),
            Matchers.iterableWithSize(3));
    }

    /**
     * InMemoryProjects has correct info about total pages after new projects
     * has been registered.
     */
    @Test
    public void hasCorrectTotalPages() {
        final Storage storage = new InMemory();
        final ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Projects all = storage.projects();

        MatcherAssert.assertThat(all.current().getNumber(),
            Matchers.is(1));
        MatcherAssert.assertThat(all.current().getSize(),
            Matchers.is(10));
        MatcherAssert.assertThat(all.totalPages(),
            Matchers.is(1));
        MatcherAssert.assertThat(all, Matchers.emptyIterable());

        for (int i = 0; i < 100; i++) {
            final Repo repo = this.mockRepo("amihaiemil/test" + (i + 1),
                "github");
            all.register(repo, projectManager, "whtoken" + (i + 1));
        }

        MatcherAssert.assertThat(all.totalPages(),
            Matchers.is(10));
        MatcherAssert.assertThat(all, Matchers.iterableWithSize(10));
        MatcherAssert.assertThat(all
                .page(new Paged.Page(1, Integer.MAX_VALUE)),
            Matchers.iterableWithSize(100));

        Projects pageThree = all.page(new Paged.Page(3, 10));
        MatcherAssert.assertThat(pageThree.totalPages(),
            Matchers.is(10));
        MatcherAssert.assertThat(pageThree.current().getNumber(),
            Matchers.is(3));
        MatcherAssert.assertThat(pageThree.current().getSize(),
            Matchers.is(10));
        MatcherAssert.assertThat(pageThree, Matchers.iterableWithSize(10));
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

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        Mockito.when(repo.owner()).thenReturn(owner);
        return repo;
    }

}
