package com.selfxdsd.core.mock;

import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.ProjectManagers;
import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Units tests for {@link InMemoryProjectManagers}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryProjectManagersTestCase {

    /**
     * Register a project manager.
     */
    @Test
    public void registerProjectManager() {
        final ProjectManagers projectManagers = new InMemoryProjectManagers(
            mock(Storage.class));
        projectManagers.register("foo-provider", "122token");
        assertThat(
            "Should have pms for: github, gitlab, foo-provider",
            projectManagers, iterableWithSize(3));
    }

    /**
     * Get the registered project manager by id.
     */
    @Test
    public void findRegisteredProjectManagerById() {
        final ProjectManagers projectManagers = new InMemoryProjectManagers(
            mock(Storage.class));
        final ProjectManager projectManager = projectManagers
            .register("foo-provider", "122token");
        assertThat(projectManager, equalTo(projectManagers.getById(3)));
    }

    /**
     * Get the registered project manager by provider name.
     */
    @Test
    public void findRegisteredProjectManagerByProvider() {
        final ProjectManagers projectManagers = new InMemoryProjectManagers(
            mock(Storage.class));
        final ProjectManager projectManager = projectManagers
            .register("foo-provider", "122token");
        assertThat(projectManager,
            equalTo(projectManagers.pick("foo-provider")));
    }
}
