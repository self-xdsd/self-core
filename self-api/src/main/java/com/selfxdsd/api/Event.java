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

/**
 * Event received from the Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.9
 */
public interface Event {

    /**
     * Type of the event.
     * @return String.
     */
    String type();

    /**
     * Issue where the event happened.
     * @return Issue.
     */
    Issue issue();

    /**
     * Comment, present if this event is
     * related to the Issue's comments (created, deleted etc).
     * @return Comment.
     */
    Comment comment();

    /**
     * Last Commit that triggered the Event, in case the
     * Event is "push".
     * @return Commit.
     */
    Commit commit();

    /**
     * New name (simple name, without owner) of the Repo.
     * @return String if this event is REPO_RENAMED, null otherwise.
     */
    String repoNewName();

    /**
     * Project where this event occured.
     * @return Project.
     */
    Project project();

    /**
     * Event types.
     */
    final class Type {

        /**
         * Hidden ctor.
         */
        private Type(){}

        /**
         * Activate repo event.
         */
        public static final String ACTIVATE = "activate";

        /**
         * Event for reviewing and assigning any unassigned tasks.
         */
        public static final String UNASSIGNED_TASKS = "unassigned";

        /**
         * Event for reviewing the assigned tasks.
         */
        public static final String ASSIGNED_TASKS = "assigned";

        /**
         * Event for a newly opened Issue or PR.
         */
        public static final String NEW_ISSUE = "newIssue";

        /**
         * Event for a reopened Issue or PR.
         */
        public static final String REOPENED_ISSUE = "reopened";

        /**
         * Event for a comment.
         */
        public static final String ISSUE_COMMENT = "issue_comment";

        /**
         * Event for renaming a repo.
         */
        public static final String REPO_RENAMED = "repo_renamed";

        /**
         * Hello comment event.
         */
        public static final String HELLO = "hello";

        /**
         * Confused comment event.
         */
        public static final String CONFUSED = "confused";

        /**
         * Resign comment event.
         */
        public static final String RESIGN = "resign";

        /**
         * Deregister comment event.
         */
        public static final String DEREGISTER = "deregister";

        /**
         * Register comment event.
         */
        public static final String REGISTER = "register";

        /**
         * Status comment event.
         */
        public static final String STATUS = "status";

        /**
         * Available commands event.
         */
        public static final String COMMANDS = "commands";

        /**
         * Issue label event.
         */
        public static final String LABEL = "label";

    }
}
