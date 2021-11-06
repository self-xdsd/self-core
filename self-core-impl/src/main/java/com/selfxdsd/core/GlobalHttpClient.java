package com.selfxdsd.core;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Global singleton {@link java.net.http.HttpClient} to be used for making
 * HTTP Requests throughout the application, especially to the providers'
 * APIs (Github, Gitlab, BitBucket etc).
 */
public final class GlobalHttpClient {

    /**
     * The HttpClient.
     */
    private static HttpClient client;

    /**
     * Hidden ctor.
     */
    private GlobalHttpClient() {}

    /**
     * Get the instance of the HttpClient.
     * @param version HTTP Version 1.1 or 2.
     * @return HttpClient.
     */
    public static HttpClient instance(final HttpClient.Version version) {
        if(client == null || !client.version().equals(version)) {
            client = HttpClient
                .newBuilder()
                .version(version)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        }
        return client;
    }
}
