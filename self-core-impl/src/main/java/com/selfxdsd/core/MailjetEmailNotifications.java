/**
 * Copyright (c) 2020-2022, Self XDSD Contributors
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

import com.selfxdsd.api.EmailNotification;
import com.selfxdsd.api.EmailNotifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Using MailJet to send e-mail notifications.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.99
 * @todo #1298:60min Write unit tests for this class, using Grizzly mock server.
 * @todo #1296:60min Use this class to send e-mails as soon as a real payment
 *  is successful. We should do it in a Wallet decorator.
 */
public final class MailjetEmailNotifications implements EmailNotifications {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        MailjetEmailNotifications.class
    );

    /**
     * MailJet Mail-Sending endpoint.
     */
    private static final String MAILJET = "https://api.mailjet.com/v3.1/send";

    /**
     * API Public Key (username).
     */
    private final String apiKey;

    /**
     * API Private Key (password).
     */
    private final String apiSecretKey;

    /**
     * HTTP Version to use.
     */
    private final HttpClient.Version httpVersion;

    /**
     * Ctor.
     * @param apiKey MailJet's API Key.
     * @param apiSecretKey MailJet's API Secret Key.
     */
    public MailjetEmailNotifications(
        final String apiKey,
        final String apiSecretKey
    ) {
        this(apiKey, apiSecretKey, HttpClient.Version.HTTP_2);
    }

    /**
     * Ctor.
     * @param apiKey MailJet's API Key.
     * @param apiSecretKey MailJet's API Secret Key.
     * @param httpVersion HTTP Version to use.
     */
    MailjetEmailNotifications(
        final String apiKey,
        final String apiSecretKey,
        final HttpClient.Version httpVersion
    ) {
        this.apiKey = apiKey;
        this.apiSecretKey = apiSecretKey;
        this.httpVersion = httpVersion;
    }

    @Override
    public void send(final EmailNotification emailNotification) {
        try {
            final JsonObject messages = Json.createObjectBuilder()
                .add(
                    "Messages",
                    Json.createArrayBuilder()
                        .add(this.notificationToJsonMessage(emailNotification))
                        .build()
                ).build();
            LOG.debug(
                "Sending EmailNotification [" + emailNotification.type()
                + "] to " + emailNotification.to() + "... "
            );
            final HttpResponse<String> response = GlobalHttpClient.instance(
                this.httpVersion
            ).send(
                this.request(
                    URI.create(MAILJET),
                    "POST",
                    HttpRequest.BodyPublishers.ofString(messages.toString())
                ),
                HttpResponse.BodyHandlers.ofString()
            );
            if(response.statusCode() == 200) {
                LOG.debug("Notification sent successfully!");
            } else {
                LOG.error(
                    "Failed sending " + emailNotification.type()
                    + "  notification! Status: "
                    + response.statusCode()
                );
                LOG.error("MailJet Response: " + response.body());
                LOG.error("MailJet Payload: " + messages);
            }
        } catch (final IOException | InterruptedException ex) {
            LOG.error(
                "Caught exception when sending " + emailNotification.type()
                + " to " + emailNotification.toName(), ex
            );
        }
    }

    /**
     * Build and return the HTTP Request.
     * @param uri URI.
     * @param method Method.
     * @param body Body.
     * @return HttpRequest.
     * @checkstyle LineLength (100 lines)
     */
    private HttpRequest request(
        final URI uri,
        final String method,
        final HttpRequest.BodyPublisher body
    ) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .method(method, body)
            .header("Content-Type", "application/json")
            .header(
                "User-Agent",
                "Self XDSD; https://self-xdsd.com; "
                    + "https://github.com/self-xdsd"
            )
            .header(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString(
                    (this.apiKey + ":" + this.apiSecretKey).getBytes()
                )
            );
        return requestBuilder.build();
    }

    /**
     * Turn an EmailNotification to a MailJet Message JSON.
     * @param emailNotification EmailNotification.
     * @return JsonObject.
     */
    private JsonObject notificationToJsonMessage(
        final EmailNotification emailNotification
    ) {
        return Json.createObjectBuilder()
            .add(
                "From",
                Json.createObjectBuilder()
                    .add("Email", emailNotification.from())
                    .add("Name", emailNotification.fromName())
                    .build()
            ).add(
                "To",
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("Email", emailNotification.to())
                        .add("Name", emailNotification.toName())
                        .build()
                )
            ).add(
                "Subject",
                emailNotification.subject()
            ).add(
                "TextPart",
                emailNotification.body()
            ).build();
    }

}
