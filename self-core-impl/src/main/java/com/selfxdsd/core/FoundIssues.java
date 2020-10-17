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
import com.selfxdsd.api.storage.Labels;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Some Issues found after search.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.28
 */
final class FoundIssues implements Issues {

    /**
     * Original Issues, where the Search was performed.
     */
    private final Issues original;

    /**
     * Issues found.
     */
    private final List<Issue> found;

    /**
     * Ctor.
     * @param original Original Issues, where the search was performed.
     * @param found Found issues.
     */
    FoundIssues(final Issues original, final List<Issue> found) {
        this.original = original;
        this.found = new ArrayList<>();
        this.found.addAll(found);
    }

    @Override
    public Issue getById(final String issueId) {
        Issue found = null;
        for(final Issue issue : this.found) {
            if(issue.issueId().equalsIgnoreCase(issueId)) {
                found = issue;
                break;
            }
        }
        return found;
    }

    @Override
    public Issue received(final JsonObject issue) {
        return this.original.received(issue);
    }

    @Override
    public Issue open(
        final String title,
        final String body,
        final String... labels
    ) {
        return this.original.open(title, body, labels);
    }

    @Override
    public Issues search(
        final String text,
        final String... labels
    ) {
        return this.original.search(text, labels);
    }

    @Override
    public Labels labels() {
        return this.original.labels();
    }

    @Override
    public Iterator<Issue> iterator() {
        return this.found.iterator();
    }
}
