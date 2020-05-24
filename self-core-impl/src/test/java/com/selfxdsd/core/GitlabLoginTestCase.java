package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URL;

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
        MatcherAssert.assertThat(
            amihaiemil.username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            amihaiemil.provider(),
            Matchers.instanceOf(Gitlab.class)
        );
        MatcherAssert.assertThat(
            amihaiemil.provider().name(),
            Matchers.equalTo("gitlab")
        );
        MatcherAssert.assertThat(
            storage.users(), Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.users().user("amihaiemil", "gitlab").username(),
            Matchers.equalTo("amihaiemil")
        );
    }
}
