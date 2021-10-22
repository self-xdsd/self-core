package com.selfxdsd.core;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.RepoException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.net.URI;

/**
 * Unit tests for {@link GitlabRepo}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (500 lines)
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
        Mockito.when(activated.repoFullName()).thenReturn("mihai/test");
        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(activated.wallets()).thenReturn(wallets);

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        final ProjectManagers managers = Mockito.mock(ProjectManagers.class);
        Mockito.when(managers.pick(Provider.Names.GITLAB)).thenReturn(manager);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.projectManagers()).thenReturn(managers);
        Mockito.when(storage.projects())
            .thenReturn(Mockito.mock(Projects.class));

        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("mihai");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITLAB);
        Mockito.when(owner.provider()).thenReturn(provider);

        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(activated.contracts()).thenReturn(contracts);

        final JsonResources res = new MockJsonResources(request -> {
            return new MockJsonResources
                .MockResource(200, Json.createObjectBuilder()
                .add("path_with_namespace", "mihai/test")
                .build());
        });
        final Repo repo = new GitlabRepo(
            res,
            URI.create("http://localhost:8080/repos/mihai/test/"),
            owner,
            storage
        );

        Mockito.when(manager.assign(repo)).thenReturn(activated);

        Project project = repo.activate();

        MatcherAssert.assertThat(project, Matchers.is(activated));
        Mockito.verify(project, Mockito.times(1)).resolve(Mockito.any());
        Mockito.verify(contracts).addContract(
            "mihai/test",
            "mihai",
            "gitlab",
            BigDecimal.valueOf(2500),
            "PO"
        );
    }

    /**
     * Throws {@link RepoException.AlreadyActive} if {@link GitlabRepo}  is
     * already active.
     */
    @Test(expected = RepoException.AlreadyActive.class)
    public void throwsRepoAlreadyActiveExceptionIfActive(){
        final Storage storage = new InMemory();
        final User owner = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(owner.provider()).thenReturn(provider);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITLAB);
        final JsonResources res = new MockJsonResources(request -> {
            return new MockJsonResources
                .MockResource(200, Json.createObjectBuilder()
                .add("path_with_namespace", "john/test")
                .build());
        });
        final Repo repo = new GitlabRepo(res, URI.create("/"), owner, storage);
        storage.projects().register(repo,
            storage.projectManagers().pick(Provider.Names.GITLAB),
            "token");

        repo.activate();
    }

    /**
     * A GitlabRepo can return its webhooks.
     */
    @Test
    public void returnsWebhooks() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.webhooks(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabWebhooks.class)
            )
        );
    }

    /**
     * A GitlabRepo can return its Issues.
     */
    @Test
    public void returnsIssues() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.issues(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssues.class)
            )
        );
    }

    /**
     * A GitlabRepo can return its Pull Requests.
     */
    @Test
    public void returnsPullRequests() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.pullRequests(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssues.class)
            )
        );
    }

    /**
     * A GitlabRepo can return its Labels.
     */
    @Test
    public void returnsLabels() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080/repos/mihai/test/"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.labels(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabRepoLabels.class)
            )
        );
    }

    /**
     * A GitlabRepo can return its Stars.
     */
    @Test
    public void returnsStars() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("test/repo"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.stars(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabStars.class)
            )
        );
    }

    /**
     * A GitlabRepo can return its commits.
     */
    @Test
    public void returnsCommits() {
        final Repo repo = new GitlabRepo(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/1"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repo.commits(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabCommits.class)
            )
        );
    }

    /**
     * GitlabRepo can enable issues.
     */
    @Test
    public void canEnableIssues(){
        final MockJsonResources res = new MockJsonResources(
            request -> new MockJsonResources.MockResource(
                200,
                Json.createObjectBuilder().build()
            )
        );
        final Repo repo = new GitlabRepo(
            res,
            URI.create("https://gitlab.com/api/v4/projects/1"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            repo.enableIssues(),
            Matchers.instanceOf(Issues.class)
        );

        final MockJsonResources.MockRequest request = res.requests().first();
        MatcherAssert.assertThat(
            request.getUri(),
            Matchers.equalTo(URI.create("https://gitlab.com/api/v4/projects/1"))
        );
        MatcherAssert.assertThat(
            request.getMethod(),
            Matchers.equalTo("PUT")
        );
        MatcherAssert.assertThat(
            request.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("issues_access_level", "enabled")
                    .build()
            )
        );
    }

    /**
     * GitlabRepo throws IllegalStateException if enableIssues returns status
     * != 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIllegalStateExceptionIfEnableIssueNotOk() {
        final JsonResources res = new MockJsonResources(
            request -> new MockJsonResources.MockResource(
                410,
                JsonValue.EMPTY_JSON_OBJECT
            )
        );
        final Repo repo = new GitlabRepo(
            res,
            URI.create("https://gitlab.com/api/v4/projects/1"),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        repo.enableIssues();
    }
}
