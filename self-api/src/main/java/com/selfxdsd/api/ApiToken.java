package com.selfxdsd.api;

import java.time.LocalDateTime;

/**
 * API Token.
 */
public interface ApiToken {
    /**
     * The name of token.
     *
     * @return String.
     */
    String name();

    /**
     * Secret key.
     *
     * @return Byte array.
     */
    byte[] secret();

    /**
     * The expiration date.
     *
     * @return LocalDateTime.
     */
    LocalDateTime expiration();
}
