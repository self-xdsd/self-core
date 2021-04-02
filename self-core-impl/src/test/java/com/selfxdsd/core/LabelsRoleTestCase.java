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

import com.selfxdsd.api.Contract;
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
 * Unit tests for {@link LabelsRole}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.72
 */
public final class LabelsRoleTestCase {

    /**
     * The default DEV role is returned if the Issue is NOT a PR and
     * has no Labels.
     */
    @Test
    public void defaultDevRoleNoLabels() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.FALSE);
        final Labels empty = Mockito.mock(Labels.class);
        Mockito.when(empty.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(empty);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * The default REV role is returned if the Issue is a PR and has
     * no Labels.
     */
    @Test
    public void defaultRevRoleNoLabels() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Labels empty = Mockito.mock(Labels.class);
        Mockito.when(empty.iterator()).thenReturn(
            new ArrayList<Label>().iterator()
        );
        Mockito.when(issue.labels()).thenReturn(empty);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.REV)
        );
    }

    /**
     * It can take the QA role from te labels.
     */
    @Test
    public void returnsQaRole() {
        final Issue issue = Mockito.mock(Issue.class);
        final Labels labels = Mockito.mock(Labels.class);

        final Label qaLabel = Mockito.mock(Label.class);
        Mockito.when(qaLabel.name()).thenReturn("qa");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(qaLabel).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.QA)
        );
    }

    /**
     * It can take the DEV role from te labels.
     */
    @Test
    public void returnsDevRole() {
        final Issue issue = Mockito.mock(Issue.class);
        final Labels labels = Mockito.mock(Labels.class);

        final Label devLabel = Mockito.mock(Label.class);
        Mockito.when(devLabel.name()).thenReturn("dev");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(devLabel).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * It can take the REV role from te labels.
     */
    @Test
    public void returnsRevRole() {
        final Issue issue = Mockito.mock(Issue.class);
        final Labels labels = Mockito.mock(Labels.class);

        final Label revLabel = Mockito.mock(Label.class);
        Mockito.when(revLabel.name()).thenReturn("rev");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(revLabel).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.REV)
        );
    }

    /**
     * It can take the PO role from te labels.
     */
    @Test
    public void returnsPoRole() {
        final Issue issue = Mockito.mock(Issue.class);
        final Labels labels = Mockito.mock(Labels.class);

        final Label poLabel = Mockito.mock(Label.class);
        Mockito.when(poLabel.name()).thenReturn("po");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(poLabel).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.PO)
        );
    }

    /**
     * It can take the ARCH role from te labels.
     */
    @Test
    public void returnsArchRole() {
        final Issue issue = Mockito.mock(Issue.class);
        final Labels labels = Mockito.mock(Labels.class);

        final Label archLabel = Mockito.mock(Label.class);
        Mockito.when(archLabel.name()).thenReturn("arch");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(archLabel).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.ARCH)
        );
    }

    /**
     * The default DEV role is returned if the Issue is NOT a PR, it has
     * some Labels, but none of them are valid role.
     */
    @Test
    public void defaultDevRoleWithLabels() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.FALSE);
        final Labels labels = Mockito.mock(Labels.class);

        final Label estimation = Mockito.mock(Label.class);
        Mockito.when(estimation.name()).thenReturn("90 min");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(estimation).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * The default REV role is returned if the Issue is a PR, it has
     * some Labels, but none of them are valid role.
     */
    @Test
    public void defaultRevRoleWithLabels() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.isPullRequest()).thenReturn(Boolean.TRUE);
        final Labels labels = Mockito.mock(Labels.class);

        final Label estimation = Mockito.mock(Label.class);
        Mockito.when(estimation.name()).thenReturn("90 min");

        Mockito.when(labels.iterator()).thenReturn(
            List.of(estimation).iterator()
        );
        Mockito.when(issue.labels()).thenReturn(labels);

        MatcherAssert.assertThat(
            new LabelsRole(issue).asString(),
            Matchers.equalTo(Contract.Roles.REV)
        );
    }
}
