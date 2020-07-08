package com.selfxdsd.core;

/**
 * Representation of an access token in a http request header.
 * This is used by {@link JsonResources} to execute authenticated requests.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public interface AccessToken {

    /**
     * Header name.
     *
     * @return String.
     */
    String header();

    /**
     * Header value. Usually the token itself.
     *
     * @return String
     */
    String value();

    /**
     * A Github access token.
     */
    class Github implements AccessToken {

        /**
         * Header value.
         */
        private final String value;

        /**
         * Ctor.
         *
         * @param value Header value.
         */
        public Github(final String value) {
            this.value = "token " + value;
        }

        @Override
        public String header() {
            return "Authorization";
        }

        @Override
        public String value() {
            return this.value;
        }
    }

    /**
     * A Gitlab access token.
     */
    class Gitlab implements AccessToken {

        /**
         * Header value.
         */
        private final String value;

        /**
         * Ctor.
         *
         * @param value Header value.
         */
        public Gitlab(final String value) {
            this.value = value;
        }

        @Override
        public String header() {
            return "Private-Token";
        }

        @Override
        public String value() {
            return this.value;
        }
    }
}
