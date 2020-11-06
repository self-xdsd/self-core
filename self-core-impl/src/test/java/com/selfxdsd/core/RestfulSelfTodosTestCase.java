/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.SelfTodos;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link RestfulSelfTodos}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.33
 */
public final class RestfulSelfTodosTestCase {

    /**
     * Posts the push payload to the /pdd endpoint OK.
     */
    @Test
    public void postsToPddOk() {
        final SelfTodos todos = new RestfulSelfTodos(
            URI.create("http://localhost:8282"),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken(),
                        Matchers.nullValue()
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("event", "push")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost:8282/pdd/github/mihai/test"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        todos.post(project, "{\"event\":\"push\"}");
    }

    /**
     * Posts the push payload to the /pdd endpoint and receives NOT FOUND.
     * In this case, it should not throw any exception, just silently log it.
     */
    @Test
    public void postsToPddNotFound() {
        final SelfTodos todos = new RestfulSelfTodos(
            URI.create("http://localhost:8282"),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken(),
                        Matchers.nullValue()
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("event", "push")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost:8282/pdd/github/mihai/test"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        todos.post(project, "{\"event\":\"push\"}");
    }

}
