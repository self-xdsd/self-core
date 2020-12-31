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
package com.selfxdsd.core;

import org.junit.Assume;
import org.junit.rules.ExternalResource;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * JUnit rule which finds a free port for integration testing.
 * If no port is found, the test is skipped.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public class RandomPort extends ExternalResource {
    @Override
    public final Statement apply(
        final Statement base, final Description description
    ) {
        return new Statement() {
            @Override
            // @checkstyle IllegalThrowsCheck (1 line)
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (final BindException ignored) {
                    Assume.assumeTrue(false);
                }
            }
        };
    }

    /**
     * Returns available port number.
     * @return Available port number
     * @throws IOException in case of IO error.
     */
    public final int port() throws IOException {
        final ServerSocket socket = new ServerSocket();
        try  {
            socket.setReuseAddress(true);
            socket.bind(
                new InetSocketAddress("localhost", 0)
            );
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }
}
