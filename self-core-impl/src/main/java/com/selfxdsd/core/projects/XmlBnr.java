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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Bnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * BNR gives us an XML response.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.51
 * @checkstyle ReturnCount (200 lines)
 * @checkstyle IllegalCatch (200 lines)
 */
public final class XmlBnr implements Bnr {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        XmlBnr.class
    );

    /**
     * BNR exchange rate URI.
     */
    private final URI uri;

    /**
     * Ctor. Uses BNR's real API by default.
     */
    public XmlBnr() {
        this(URI.create("https://www.bnr.ro/nbrfxrates.xml"));
    }

    /**
     * Ctor.
     * @param uri API Uri.
     */
    XmlBnr(final URI uri) {
        this.uri = uri;
    }

    @Override
    public BigDecimal euroToRon() {
        try {
            final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder()
                        .uri(this.uri)
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
            return readEurFromXml(response.body());
        } catch (final Exception ex) {
            LOG.error(
                "[BNR] Could not get EUR-RON exchange rate: ", ex
            );
            LOG.error("[BNR] Returning 492 as default exchange rate.");
            return BigDecimal.valueOf(492);
        }
    }

    /**
     * Parse the response XML to get the EUR -> RON exchange rage.
     * @param xml XML String.
     * @return BigDecimal.
     * @checkstyle LineLength (100 lines)
     */
    private BigDecimal readEurFromXml(final String xml) {
        try {
            final Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
            final NodeList rates = document
                .getDocumentElement()
                .getElementsByTagName("Rate");
            for(int idx=0; idx<rates.getLength(); idx++) {
                final Node rate = rates.item(idx);
                final String name = rate.getAttributes()
                    .getNamedItem("currency").getTextContent();
                if("EUR".equalsIgnoreCase(name)) {
                    final String text = rate.getTextContent();
                    LOG.info("[BNR] Found EUR-RON exchange rate: " + text);
                    LOG.info("[BNR] Rounding Half-Up.");
                    return new BigDecimal(text)
                        .setScale(2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                }
            }
            LOG.warn("[BNR] EUR-RON not found! Returning 492 as default.");
            return BigDecimal.valueOf(492);
        } catch (final Exception ex) {
            LOG.error("[BNR] Exception while parsing XML: ", ex);
            LOG.error("[BNR] Returning 492 as default exchange rate.");
            return BigDecimal.valueOf(492);
        }
    }
}
