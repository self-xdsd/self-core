package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.projects.UserProjects;
import org.junit.Test;
import java.util.ArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link InMemoryProjects}.
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 * @todo #16:30min Continue writing unit tests for the InMemory storage
 *  infrastructure. In the end all classes and methods should be covered
 *  with unit tests so we can rely 100% on them.
 */
public final class InMemoryProjectsTestCase {

    /**
     * Register a project.
     */
    @Test
    public void projectRegister() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
                .projectManagers().pick();
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        Project project = projects.register(
                mock(Repo.class), projectManager);

        assertThat(project.projectManager(),
                equalTo(projectManager));
        assertThat(project.projectId(),
                equalTo(0));
        assertThat(storage.projects(), contains(project));
    }
    /**
     * Register a project twice, making sure id is incremented.
     */
    @Test
    public void projectRegisterTwice() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
                .projectManagers().pick();
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        Project project = projects.register(
                mock(Repo.class), projectManager);
        Project projectcopy = projects.register(mock(
                Repo.class), projectManager);

        assertThat(project.projectId(),
                equalTo(0));
        assertThat(projectcopy.projectId(), equalTo(1));
    }
    /**
     * Check if project is owned by.
     */
    @Test
    public void projectOwnedBy() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage.projectManagers().pick();
        InMemoryProjects projects = (InMemoryProjects)
                storage.projects();
        final Projects userprojects = new UserProjects(
                this.mockUser("mihai", "github"),
                new ArrayList<>()
        );
        assertThat(userprojects.ownedBy(this.mockUser(
                "mihai", "github")), equalTo(userprojects));

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
