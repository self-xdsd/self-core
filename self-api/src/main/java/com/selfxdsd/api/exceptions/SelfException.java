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
package com.selfxdsd.api.exceptions;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Base class for all Self exceptions.
 * @author criske
 * @version $Id$
 * @since 0.0.22
 * @checkstyle DesignForExtension (500 lines).
 */
public abstract class SelfException extends RuntimeException {

    /**
     * Self exception as json.
     * @return JsonObject.
     */
    public JsonObject json(){
        return Json.createObjectBuilder()
            .add("message", this.getSelfMessage())
            .build();
    }

    /**
     * The de facto way to display a Self exception message.
     * @return String.
     */
    abstract String getSelfMessage();

    @Override
    public final String getMessage() {
        return this.getSelfMessage();
    }

    @Override
    public final String getLocalizedMessage() {
        return this.getSelfMessage();
    }

    @Override
    public String toString() {
        return this.json().toString();
    }
}
