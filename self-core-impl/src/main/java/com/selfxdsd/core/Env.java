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

/**
 * String constants for all env variables used in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.24
 */
public final class Env {

    /**
     * Hidden ctor.
     */
    private Env(){ }

    /**
     * Self Web base API uri.
     */
    public static final String SELF_BASE_URL = "self_xdsd_base_url";

    /**
     * DB Url.
     */
    public static final String DB_URL = "self_db_url";

    /**
     * DB User.
     */
    public static final String DB_USER = "self_db_user";

    /**
     * DB Password.
     */
    public static final String DB_PASSWORD = "self_db_password";

    /**
     * Github Client ID (Oauth2).
     */
    public static final String GITHUB_CLIENT_ID = "gh_client_id";

    /**
     * Github Client Secret (Oauth2).
     */
    public static final String GITHUB_CLIENT_SECRET = "gh_client_secret";

    /**
     * API Token for Stripe.
     */
    public static final String STRIPE_API_TOKEN = "self_stripe_token";

    /**
     * Webhook Base URL. E.g. http://self-xdsd.go.ro/pm
     */
    public static final String WEBHOOK_BASE_URL = "self_webhook_base_url";

    /**
     * Host of the server where we clone the repo and discover puzzles.
     */
    public static final String PDD_HOST = "self_pdd_host";

    /**
     * Port of the server where we clone the repo and discover puzzles.
     */
    public static final String PDD_PORT = "self_pdd_port";

    /**
     * Username of the server where we clone the repo and discover puzzles.
     */
    public static final String PDD_USERNAME = "self_pdd_username";

    /**
     * Path to the private key of the server where we clone the repo
     * and discover puzzles.
     */
    public static final String PDD_PRIVATE_KEY = "self_pdd_privatekey";

}
