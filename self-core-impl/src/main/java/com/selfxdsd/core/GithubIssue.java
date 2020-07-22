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

import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;
import java.net.URI;

/**
 * An Issue in a Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #128:30min Initially, all the Issues will have the DEV or REV role.
 *  In a future version, we will set an Issue's role based on labels set
 *  by the user.
 * @todo #305:30min Labels API: should be able to list and add labels for
 *  Provider resources (Repo, Issue). Labels will be used mainly for an Issue
 *  to get its estimation.
 */
final class GithubIssue implements Issue {

    /**
     * Issue base uri.
     */
    private final URI issueUri;

    /**
     * Issue JSON as returned by Github's API.
     */
    private final JsonObject json;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Ctor.
     * @param issueUri Issues base URI.
     * @param json Json Issue as returned by Github's API.
     * @param storage Storage.
     * @param resources Github's JSON Resources.
     */
    GithubIssue(
        final URI issueUri,
        final JsonObject json,
        final Storage storage,
        final JsonResources resources
    ) {
        this.issueUri = issueUri;
        this.json = json;
        this.storage = storage;
        this.resources = resources;
    }

    @Override
    public String issueId() {
        return String.valueOf(this.json.getInt("number"));
    }

    @Override
    public String provider() {
        return "github";
    }

    @Override
    public String role() {
        final String role;
        if(this.json.getJsonObject("pull_request") == null) {
            role = Contract.Roles.DEV;
        } else {
            role = Contract.Roles.REV;
        }
        return role;
    }

    @Override
    public String repoFullName() {
        return this.json.getString("repository_url").substring(
            "https://api.github.com/repos/".length()
        );
    }

    @Override
    public String author() {
        return this.json.getJsonObject("user").getString("login");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }

    @Override
    public Comments comments() {
        return new DoNotRepeat(
            new GithubIssueComments(this.issueUri, this.resources)
        );
    }

    @Override
    public int estimation() {
        return 60;
    }
}
