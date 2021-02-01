package com.selfxdsd.core;

import com.selfxdsd.api.ApiToken;
import com.selfxdsd.api.ApiTokens;
import com.selfxdsd.api.User;
import java.util.*;

/**
 * Stored API Tokens.
 */
public final class StoredApiTokens implements ApiTokens {
    /**
     * Storage of tokens.
     */
    private final Map<User, Collection<ApiToken>> tokens;

    /**
     * Ctor.
     *
     * @param tokens Map of Users and their tokens.
     */
    public StoredApiTokens(final Map<User, Collection<ApiToken>> tokens) {
        this.tokens = tokens;
    }

    @Override
    public ApiTokens ofUser(final User user) {
        if (!this.tokens.containsKey(user)) {
            throw new NoSuchElementException(
                String.format("Not found for %s", user)
            );
        }
        return new StoredApiTokens(
            Collections.singletonMap(
                user,
                this.tokens.get(user)
            )
        );
    }

    @Override
    public Iterator<ApiToken> iterator() {
        return this.tokens.values()
            .stream()
            .flatMap(Collection::stream)
            .iterator();
    }
}
