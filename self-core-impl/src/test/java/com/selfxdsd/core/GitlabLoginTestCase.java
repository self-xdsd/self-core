package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link GitlabLogin}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabLoginTestCase {

    /**
     * SelfCore can sign up a GitLab User.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void gitlabLoginWorks() throws Exception {
        final Storage storage = new InMemory();
        final Self self = new SelfCore(storage);
        final Login gitlabLogin = new GitlabLogin(
            "amihaiemil", "amihaiemil@gmail.com",
            new URL("https://gravatar.com/amihaiemil"), "gl123token"
        );

        final User amihaiemil = self.login(gitlabLogin);
        assertThat(amihaiemil.username(), equalTo("amihaiemil"));
        assertThat(amihaiemil.email(), equalTo("amihaiemil@gmail.com"));
        assertThat(amihaiemil.avatar(),
            equalTo(new URL("https://gravatar.com/amihaiemil")));
        assertThat(amihaiemil.provider(), instanceOf(Gitlab.class));
        assertThat(amihaiemil.provider().name(), equalTo("gitlab"));

        assertThat(storage.users(), iterableWithSize(1));
        assertThat(storage.users().user("amihaiemil", "gitlab")
            .username(),
            equalTo("amihaiemil"));

    }
}
