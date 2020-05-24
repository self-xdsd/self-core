package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.projects.UserProjects;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link InMemoryProjects}.
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 * @todo #96:30min Continue tests for {@link Projects#getProjectById(int)}
 *  in PmProjectsTestCase and UserProjectsTestCase.
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
                mock(Repo.class), projectManager);

        assertThat(registered.projectManager(),
                is(projectManager));
        assertThat(registered.projectId(),
                equalTo(0));
        assertThat(storage.projects(), contains(registered));
    }
    /**
     * Register two projects, making sure id is incremented.
     */
    @Test
    public void projectRegisterForTwoProjects() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
                .projectManagers().pick("github");
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        Project first = projects.register(
                mock(Repo.class), projectManager);
        Project second = projects.register(mock(
                Repo.class), projectManager);

        assertThat(first.projectId(),
                equalTo(0));
        assertThat(second.projectId(), equalTo(1));
    }
    /**
     * Check if project is owned by.
     */
    @Test
    public void projectOwnedBy() {
        final Storage storage = new InMemory();
        InMemoryProjects projects = (InMemoryProjects) storage.projects();
        ProjectManager projectManager = storage
                .projectManagers().pick("github");
        Project project = projects.register(mock(Repo.class), projectManager);
        List<Project> ProjectList = new ArrayList<>();
        ProjectList.add(project);
        final Projects userprojects = new UserProjects(
                this.mockUser("mihai", "github"),
                ProjectList
        );
        MatcherAssert.assertThat(
                userprojects.ownedBy(this.mockUser(
                        "mihai", "github")),
                Matchers.iterableWithSize(1));
    }

    /**
     * Check if the right project is returned.
     */
    @Test
    public void getProjectById() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
                .projectManagers().pick("github");
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        projects.register(mock(Repo.class), projectManager);
        projects.register(mock(Repo.class), projectManager);
        Project project = projects.getProjectById(1);
        assertThat(project.projectId(), equalTo(1));
    }

    /**
     * Check if no project is returned when id is not found.
     */
    @Test
    public void getProjectByNotFoundId() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
                .projectManagers().pick("github");
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        projects.register(mock(Repo.class), projectManager);
        Project project = projects.getProjectById(-1);
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

}
