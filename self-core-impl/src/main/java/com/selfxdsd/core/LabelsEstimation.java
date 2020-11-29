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

import com.selfxdsd.api.Estimation;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Label;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Estimation of an Issue/PR read from its labels.<br><br>
 *
 * If no estimation Label is present, the default estimation is
 * 60min for Issues and 30min for PRs.<br><br>
 *
 * Format of an estimation label should be 60 min|minutes|m.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.39
 */
final class LabelsEstimation implements Estimation {

    /**
     * Label regex.
     * @checkstyle LineLength (5 lines)
     */
    private static final String ESTIMATION = "^([1-9]+[0-9]*)[ ]*(minutes|min|m)$";

    /**
     * Maximum estimation allowed.
     */
    private static final int MAX_ESTIMATION = 360;

    /**
     * Issue being estimated.
     */
    private final Issue issue;

    /**
     * Ctor.
     * @param issue Estimated issue.
     */
    LabelsEstimation(final Issue issue) {
        this.issue = issue;
    }

    @Override
    public int minutes() {
        int minutes = 0;
        final Pattern reg = Pattern.compile(
            ESTIMATION,
            Pattern.CASE_INSENSITIVE
        );
        for(final Label label : this.issue.labels()) {
            final Matcher match = reg.matcher(label.name());
            if(match.find()) {
                minutes = Integer.valueOf(match.group(1));
                break;
            }
        }
        if(minutes == 0) {
            if(this.issue.isPullRequest()) {
                minutes = 30;
            } else {
                minutes = 60;
            }
        } else if(minutes > MAX_ESTIMATION) {
            minutes = MAX_ESTIMATION;
        }
        return minutes;
    }
}
