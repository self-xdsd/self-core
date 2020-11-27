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

import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;
import java.net.URI;

/**
 * Unit tests for {@link GitlabIssues}.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
public final class GitlabIssuesTestCase {


    /**
     * GitlabIssues.getById(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void getByIdIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).getById("1");
    }

    /**
     * GitlabIssues.received(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void receivedIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).received(JsonObject.EMPTY_JSON_OBJECT);
    }

    /**
     * GitlabIssues.open(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void openIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).open("", "", "");
    }

    /**
     * GitlabIssues.getById(...) is not implemented yet.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void searchIsNotImplemented() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).search("", "");
    }

    /**
     * Iterating over all Gitlab issues is not allowed.
     */
    @Test(expected = IllegalStateException.class)
    public void iterationOverAllIssuesNotAllowed() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Storage.class)
        ).iterator();
    }
}
