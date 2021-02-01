package com.selfxdsd.api;

/**
 * API Tokens.
 */
public interface ApiTokens extends Iterable<ApiToken> {
    /**
     * API tokens of a user.
     *
     * @param user User.
     * @return ApiTokens.
     */
    ApiTokens ofUser(User user);
}
