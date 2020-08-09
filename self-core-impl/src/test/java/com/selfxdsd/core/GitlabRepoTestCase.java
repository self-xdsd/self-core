package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import java.net.URI;

/**
 * Unit tests for {@link GitlabRepo}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabRepoTestCase {

    /**
     * A GitlabRepo can return its owner.
     */
    @Test
    public void returnsOwner() {
        final User owner = Mockito.mock(User.class);
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080"),
            owner,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repo.owner(), Matchers.is(owner));
    }

    /**
     * A GitlabRepo can be activated.
     */
    @Test
    public void canBeActivated() {
        final Project activated = Mockito.mock(Project.class);

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final ProjectManagers all = Mockito.mock(ProjectManagers.class);
        Mockito.when(all.pick(Provider.Names.GITLAB)).thenReturn(manager);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(all);

        final User owner = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITLAB);
        Mockito.when(owner.provider()).thenReturn(provider);

        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            owner,
            storage
        );

        Mockito.when(manager.assign(repo)).thenReturn(activated);

        Project project = repo.activate();

        MatcherAssert.assertThat(project, Matchers.is(activated));
        Mockito.verify(project, Mockito.times(1)).resolve(Mockito.any());
    }

}
