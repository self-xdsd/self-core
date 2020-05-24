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
 * Unit tests for {@link GithubLogin}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubLoginTestCase {

    /**
     * SelfCore can sign up a Github User.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void githubLoginWorks() throws Exception {
        final Storage storage = new InMemory();
        final Self self = new SelfCore(storage);
        final Login githubLogin = new GithubLogin(
            "amihaiemil", "amihaiemil@gmail.com",
            new URL("https://gravatar.com/amihaiemil"), "gh123token"
        );
        final User amihaiemil = self.login(githubLogin);
        MatcherAssert.assertThat(
            amihaiemil.username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            amihaiemil.provider(),
            Matchers.instanceOf(Github.class)
        );
        MatcherAssert.assertThat(
            amihaiemil.provider().name(),
            Matchers.equalTo("github")
        );
        MatcherAssert.assertThat(
            storage.users(), Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.users().user("amihaiemil", "github").username(),
            Matchers.equalTo("amihaiemil")
        );
    }
}
