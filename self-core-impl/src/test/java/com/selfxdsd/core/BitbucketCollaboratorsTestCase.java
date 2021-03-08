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

import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;


/**
 * Unit tests for {@link BitbucketCollaborators}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.68
 */
public final class BitbucketCollaboratorsTestCase {

    /**
     * Can't send invite for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void invite() {
        new BitbucketCollaborators(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/workspaces/test/members"),
            Mockito.mock(Storage.class)
        ).invite("user");
    }

    /**
     * Cant remove a collaborator for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        new BitbucketCollaborators(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/workspaces/test/members"),
            Mockito.mock(Storage.class)
        ).remove("user");
    }

    /**
     * Can't iterate all collaborator for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void iterator() {
        new BitbucketCollaborators(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/workspaces/test/members"),
            Mockito.mock(Storage.class)
        ).iterator();
    }
}
