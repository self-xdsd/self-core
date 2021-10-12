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

import com.selfxdsd.api.Estimation;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Label;
import com.selfxdsd.api.Labels;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link LabelsEstimation}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 */
public final class LabelsEstimationTestCase {

    /**
     * Returns the default Issue estimation when there are no
     * estimation labels.
     */
    @Test
    public void returnsDefaultIssueEstimation() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.FALSE);
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator())
            .thenReturn(new ArrayList<Label>().iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(60)
        );
    }

    /**
     * Returns the default PR estimation when there are no
     * estimation labels.
     */
    @Test
    public void returnsDefaultPrEstimation() {
        final Issue pull = Mockito.mock(Issue.class);
        Mockito.when(pull.isPullRequest()).thenReturn(Boolean.TRUE);
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator())
            .thenReturn(new ArrayList<Label>().iterator());
        Mockito.when(pull.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(pull);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(30)
        );
    }

    /**
     * Minimum estimation should be 15 minutes.
     */
    @Test
    public void doesNotGoBellowMinimumEstimation() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("7 minutes");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(15)
        );
    }

    /**
     * Returns a label estimation. If there multiple estimation labels
     * it returns the max value.
     */
    @Test
    public void returnsLabelEstimation() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("45 min");
        final Label labelB = Mockito.mock(Label.class);
        Mockito.when(labelB.name()).thenReturn("60min");
        final Label labelC = Mockito.mock(Label.class);
        Mockito.when(labelC.name()).thenReturn("15 min");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(
            List.of(label, labelB, labelC).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(60)
        );
    }

    /**
     * Returns a label estimation which has no spaces.
     */
    @Test
    public void returnsLabelEstimationNoSpace() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("120m");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(120)
        );
    }

    /**
     * Returns a label estimation which is uppercase.
     */
    @Test
    public void returnsLabelEstimationCaseInsensitive() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("125 MINUTES");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(125)
        );
    }

    /**
     * Maximum estimation (360min) is not exceeded.
     */
    @Test
    public void doesNotExceedMaximum() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("800 min");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(360)
        );
    }

    /**
     * Huge numbers are ignored and the default estimation is returned.
     */
    @Test
    public void returnsDefaultOnHugeNumber() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn(
            "100000000000000000000000000000000 min"
        );
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(30)
        );
    }

    /**
     * Comma numbers are ignored and the default estimation is returned.
     */
    @Test
    public void returnsDefaultOnCommaNumber() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("60.35 min");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(30)
        );
    }

    /**
     * Negative numbers are ignored and the default estimation is returned.
     */
    @Test
    public void returnsDefaultOnNegativeNumber() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("-30 min");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(30)
        );
    }

    /**
     * Zero estimation is ignored and the default estimation is returned.
     */
    @Test
    public void returnsDefaultOnZero() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Label label = Mockito.mock(Label.class);
        Mockito.when(label.name()).thenReturn("0 min");
        final Labels labels = Mockito.mock(Labels.class);
        Mockito.when(labels.iterator()).thenReturn(List.of(label).iterator());
        Mockito.when(issue.labels()).thenReturn(labels);

        final Estimation est = new LabelsEstimation(issue);

        MatcherAssert.assertThat(
            est.minutes(),
            Matchers.equalTo(30)
        );
    }
}
