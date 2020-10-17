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

import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Issues;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link FoundIssues}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.28
 */
public final class FoundIssuesTestCase {

    /**
     * FoundIssues.getById returns a found Issue.
     */
    @Test
    public void returnsById() {
        final List<Issue> issues = new ArrayList<>();
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");
        issues.add(issue);
        final Issues found = new FoundIssues(
            Mockito.mock(Issues.class),
            issues
        );
        MatcherAssert.assertThat(
            found.getById("123"),
            Matchers.is(issue)
        );
    }

    /**
     * FoundIssues.getById returns null if the Issue is not found.
     */
    @Test
    public void returnsByIdNull() {
        final List<Issue> issues = new ArrayList<>();
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");
        issues.add(issue);
        final Issues found = new FoundIssues(
            Mockito.mock(Issues.class),
            issues
        );
        MatcherAssert.assertThat(
            found.getById("500"),
            Matchers.nullValue()
        );
    }

    /**
     * FoundIssue.received(...) should be delegated to the
     * original Issues.
     */
    @Test
    public void receivedDelegatesToOriginal() {
        final JsonObject json = Json.createObjectBuilder().build();

        final Issue issue = Mockito.mock(Issue.class);
        final Issues original = Mockito.mock(Issues.class);
        Mockito.when(original.received(json)).thenReturn(issue);

        final Issues found = new FoundIssues(
            original,
            new ArrayList<>()
        );

        MatcherAssert.assertThat(
            found.received(json),
            Matchers.is(issue)
        );
        Mockito.verify(original, Mockito.times(1)).received(json);
    }

    /**
     * FoundIssue.open(...) should be delegated to the
     * original Issues.
     */
    @Test
    public void openDelegatesToOriginal() {
        final Issue issue = Mockito.mock(Issue.class);
        final Issues original = Mockito.mock(Issues.class);
        Mockito.when(
            original.open("title", "body", "puzzle")
        ).thenReturn(issue);

        final Issues found = new FoundIssues(
            original,
            new ArrayList<>()
        );

        MatcherAssert.assertThat(
            found.open("title", "body", "puzzle"),
            Matchers.is(issue)
        );
        Mockito.verify(
            original, Mockito.times(1)
        ).open("title", "body", "puzzle");
    }

    /**
     * FoundIssue.search(...) should be delegated to the
     * original Issues.
     */
    @Test
    public void searchDelegatesToOriginal() {
        final Issues result = Mockito.mock(Issues.class);
        final Issues original = Mockito.mock(Issues.class);
        Mockito.when(
            original.search("text", "puzzle")
        ).thenReturn(result);

        final Issues found = new FoundIssues(
            original,
            new ArrayList<>()
        );

        MatcherAssert.assertThat(
            found.search("text", "puzzle"),
            Matchers.is(result)
        );
        Mockito.verify(
            original, Mockito.times(1)
        ).search("text", "puzzle");
    }

    /**
     * FoundIssues can have 0 elements.
     */
    @Test
    public void isIterableWithSizeZero() {
        final Issues found = new FoundIssues(
            Mockito.mock(Issues.class),
            new ArrayList<>()
        );
        MatcherAssert.assertThat(
            found,
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * FoundIssues can have elements.
     */
    @Test
    public void isIterableWithMoreElements() {
        final List<Issue> elements = new ArrayList<>();
        elements.add(Mockito.mock(Issue.class));
        elements.add(Mockito.mock(Issue.class));
        elements.add(Mockito.mock(Issue.class));

        final Issues found = new FoundIssues(
            Mockito.mock(Issues.class),
            elements
        );
        MatcherAssert.assertThat(
            found,
            Matchers.iterableWithSize(3)
        );
    }

}
