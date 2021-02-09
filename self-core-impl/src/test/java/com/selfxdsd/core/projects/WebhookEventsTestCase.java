/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Provider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link WebhookEvents}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.61
 */
public final class WebhookEventsTestCase {

    /**
     * It can create the GithubWebhookEvent.
     */
    @Test
    public void createsGithubWebhookEvent() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);

        final Event created = WebhookEvents.create(
            project,
            "push",
            "{}"
        );

        MatcherAssert.assertThat(
            created,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GithubWebhookEvent.class)
            )
        );
        MatcherAssert.assertThat(
            created.project(),
            Matchers.is(project)
        );
    }

    /**
     * It can create the GitlabWebhookEvent.
     */
    @Test
    public void createsGitlabWebhookEvent() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITLAB);

        final Event created = WebhookEvents.create(
            project,
            "Push Hook",
            "{}"
        );

        MatcherAssert.assertThat(
            created,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabWebhookEvent.class)
            )
        );
        MatcherAssert.assertThat(
            created.project(),
            Matchers.is(project)
        );
    }

    /**
     * It complains if the provider is unknown.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsIseOnUnknownProvider() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn("unknown");
        WebhookEvents.create(
            project,
            "event type",
            "{}"
        );
    }

}
