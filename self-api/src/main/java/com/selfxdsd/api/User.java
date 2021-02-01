/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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
 * User.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface User {

    /**
     * User's name.
     * @return String.
     */
    String username();

    /**
     * User's email address.
     * @return String.
     */
    String email();

    /**
     * User's role in Self (simple user, admin etc).
     * @return String.
     */
    String role();

    /**
     * Provider. Github, Bitbucket, Gitlab etc.
     * @return String.
     */
    Provider provider();

    /**
     * A User's projects (activated repositories), managed
     * by the platform.
     * @return Projects.
     */
    Projects projects();

    /**
     * Returns the Contributor if this User is one.
     * @return Contributor or null, if this User is not one.
     */
    Contributor asContributor();

    /**
     * Returns the Admin if this User is one.
     * @return Admin or null, if this User is not one.
     */
    Admin asAdmin();

    /**
     * User's API tokens.
     *
     * @return ApiTokens.
     * @todo #947:30min This method was left unimplemented.
     *  Start implementing this methods and add additional
     *  implementations of ApiToken/ApiTokens where needed.
     */
    ApiTokens apiTokens();
}
