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
package com.selfxdsd.core;

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link WithContributorLabel}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.34
 */
public final class WithContributorLabelTestCase {

    /**
     * WithContributorLabel adds the label if the assignment
     * is successful.
     */
    @Test
    public void addsLabelOnAssignmentOk() {
        final Labels labels = Mockito.mock(Labels.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.labels()).thenReturn(labels);
        Mockito.when(decorated.assign("mihai")).thenReturn(true);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.assign("mihai"),
            Matchers.is(true)
        );
        Mockito.verify(labels, Mockito.times(1)).add("@mihai");
    }

    /**
     * WithContributorLabel does not add the label if the assignment
     * is unsuccessful.
     */
    @Test
    public void doesNotAddLabelOnAssignmentFailed() {
        final Labels labels = Mockito.mock(Labels.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.labels()).thenReturn(labels);
        Mockito.when(decorated.assign("mihai")).thenReturn(false);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.assign("mihai"),
            Matchers.is(false)
        );
        Mockito.verify(labels, Mockito.times(0)).add(Mockito.anyString());
    }

    /**
     * WithContributorLabel removes the label if the unassignment
     * is successful.
     */
    @Test
    public void removesLabelOnUnassignmentOk() {
        final Labels labels = Mockito.mock(Labels.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.labels()).thenReturn(labels);
        Mockito.when(decorated.unassign("mihai")).thenReturn(true);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.unassign("mihai"),
            Matchers.is(true)
        );
        Mockito.verify(labels, Mockito.times(1)).remove("@mihai");
    }

    /**
     * WithContributorLabel does not remove the label if the unassignment
     * is unsuccessful.
     */
    @Test
    public void doesNotRemoveLabelOnUnassignmentFailed() {
        final Labels labels = Mockito.mock(Labels.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.labels()).thenReturn(labels);
        Mockito.when(decorated.unassign("mihai")).thenReturn(false);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.unassign("mihai"),
            Matchers.is(false)
        );
        Mockito.verify(labels, Mockito.times(0)).remove(Mockito.anyString());
    }

    /**
     * Delegates the issueId to the decorated object.
     */
    @Test
    public void delegatesIssueId() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.issueId()).thenReturn("123");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.issueId(),
            Matchers.equalTo("123")
        );
        Mockito.verify(decorated, Mockito.times(1)).issueId();
    }

    /**
     * Delegates the provider to the decorated object.
     */
    @Test
    public void delegatesProvider() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.provider()).thenReturn(Provider.Names.GITHUB);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
        Mockito.verify(decorated, Mockito.times(1)).provider();
    }

    /**
     * Delegates the role to the decorated object.
     */
    @Test
    public void delegatesRole() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.role()).thenReturn("DEV");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.role(),
            Matchers.equalTo("DEV")
        );
        Mockito.verify(decorated, Mockito.times(1)).role();
    }

    /**
     * Delegates the repoFullName to the decorated object.
     */
    @Test
    public void delegatesRepoFullName() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.repoFullName()).thenReturn("mihai/test");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.repoFullName(),
            Matchers.equalTo("mihai/test")
        );
        Mockito.verify(decorated, Mockito.times(1)).repoFullName();
    }

    /**
     * Delegates the author to the decorated object.
     */
    @Test
    public void delegatesAuthor() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.author()).thenReturn("mihai");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.author(),
            Matchers.equalTo("mihai")
        );
        Mockito.verify(decorated, Mockito.times(1)).author();
    }

    /**
     * Delegates the body to the decorated object.
     */
    @Test
    public void delegatesBody() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.body()).thenReturn("some issue...");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.body(),
            Matchers.equalTo("some issue...")
        );
        Mockito.verify(decorated, Mockito.times(1)).body();
    }

    /**
     * Delegates the assignee to the decorated object.
     */
    @Test
    public void delegatesAssignee() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.assignee()).thenReturn("john");
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.assignee(),
            Matchers.equalTo("john")
        );
        Mockito.verify(decorated, Mockito.times(1)).assignee();
    }

    /**
     * Delegates the json to the decorated object.
     */
    @Test
    public void delegatesJson() {
        final JsonObject json = Json.createObjectBuilder().build();
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.json()).thenReturn(json);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.json(),
            Matchers.equalTo(json)
        );
        Mockito.verify(decorated, Mockito.times(1)).json();
    }

    /**
     * Delegates the comments to the decorated object.
     */
    @Test
    public void delegatesComments() {
        final Comments comments = Mockito.mock(Comments.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.comments()).thenReturn(comments);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.comments(),
            Matchers.is(comments)
        );
        Mockito.verify(decorated, Mockito.times(1)).comments();
    }

    /**
     * Delegates the isClosed to the decorated object.
     */
    @Test
    public void delegatesIsClosed() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.isClosed()).thenReturn(true);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.isClosed(),
            Matchers.is(true)
        );
        Mockito.verify(decorated, Mockito.times(1)).isClosed();
    }

    /**
     * Delegates the isPullRequest to the decorated object.
     */
    @Test
    public void delegatesIsPullRequest() {
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.isPullRequest()).thenReturn(true);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.isPullRequest(),
            Matchers.is(true)
        );
        Mockito.verify(decorated, Mockito.times(1)).isPullRequest();
    }

    /**
     * Delegates the estimation to the decorated object.
     */
    @Test
    public void delegatesEstimation() {
        final Estimation estimation = Mockito.mock(Estimation.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.estimation()).thenReturn(estimation);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.estimation(),
            Matchers.is(estimation)
        );
        Mockito.verify(decorated, Mockito.times(1)).estimation();
    }

    /**
     * Delegates the labels to the decorated object.
     */
    @Test
    public void delegatesLabels() {
        final Labels labels = Mockito.mock(Labels.class);
        final Issue decorated = Mockito.mock(Issue.class);
        Mockito.when(decorated.labels()).thenReturn(labels);
        final Issue withLabel = new WithContributorLabel(decorated);
        MatcherAssert.assertThat(
            withLabel.labels(),
            Matchers.is(labels)
        );
        Mockito.verify(decorated, Mockito.times(1)).labels();
    }

    /**
     * Delegates close to the decorated object.
     */
    @Test
    public void delegatesClose() {
        final Issue decorated = Mockito.mock(Issue.class);
        final Issue withLabel = new WithContributorLabel(decorated);
        withLabel.close();
        Mockito.verify(decorated, Mockito.times(1)).close();
    }

    /**
     * Delegates reopen to the decorated object.
     */
    @Test
    public void delegatesReopen() {
        final Issue decorated = Mockito.mock(Issue.class);
        final Issue withLabel = new WithContributorLabel(decorated);
        withLabel.reopen();
        Mockito.verify(decorated, Mockito.times(1)).reopen();
    }
}
