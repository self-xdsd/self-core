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
package com.selfxdsd.api;

import javax.json.JsonObject;

/**
 * Issue in a repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #938:60min Add method etag() which will return the Issue's etag as
 *  it comes from the Provider's API.
 */
public interface Issue {

    /**
     * Issue ID.
     * @return String.
     */
    String issueId();

    /**
     * Issue provider.
     * @return Provider name.
     */
    String provider();

    /**
     * Role necessary to solve this Issue.
     * @return String.
     */
    String role();

    /**
     * Repo full name.
     * @return Repo full name.
     */
    String repoFullName();

    /**
     * The author's username.
     * @return String.
     */
    String author();

    /**
     * The issue's text body.
     * @return String.
     */
    String body();

    /***
     * Assignee's username. Might not be present.
     * @return String or null.
     */
    String assignee();

    /**
     * Assign this Issue to someone.
     * @param username Assignee's username.
     * @return True or false, depending on whether the operation succeeded.
     */
    boolean assign(final String username);

    /**
     * Unassign this Issue from someone.
     * @param username Assignee's username.
     * @return True or false, depending on whether the operation succeeded.
     */
    boolean unassign(final String username);

    /**
     * The Issue in JSON format as returned by the provider's API.
     * @return JsonObject.
     */
    JsonObject json();

    /**
     * Issue comments.
     * @return Comments.
     */
    Comments comments();

    /**
     * Close the Issue.
     */
    void close();

    /**
     * Reopen the Issue.
     */
    void reopen();

    /**
     * Is this Issue closed or not?
     * @return True or false.
     */
    boolean isClosed();

    /**
     * Should return true if this Issue is a Pull Request and false if
     * it's a normal Issue.
     * @return True or false.
     */
    boolean isPullRequest();

    /**
     * The Estimation of this issue.
     * @return Estimation.
     */
    Estimation estimation();

    /**
     * Labels of this Issue.
     * @return Issues.
     */
    Labels labels();
}
