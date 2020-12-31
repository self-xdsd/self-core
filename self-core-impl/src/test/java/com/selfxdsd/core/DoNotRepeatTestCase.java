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

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for {@link DoNotRepeat}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public final class DoNotRepeatTestCase {

    /**
     * DoNotRepeat iterates over the original comments.
     */
    @Test
    public void iteratesOverOriginalComments() {
        final Iterator<Comment> iterator = Mockito.mock(Iterator.class);
        final Comments origin = Mockito.mock(Comments.class);
        Mockito.when(origin.iterator()).thenReturn(iterator);
        final Comments doNotRepeat = new DoNotRepeat(origin);
        MatcherAssert.assertThat(
            doNotRepeat.iterator(),
            Matchers.is(iterator)
        );
    }

    /**
     * DoNotRepeat does not post duplicate comments.
     */
    @Test
    public void doesNotPostDuplicate() {
        final List<Comment> list = List.of(
            this.mockComment("hello world"),
            this.mockComment("hey there"),
            this.mockComment("hi")
        );
        final Comments origin = Mockito.mock(Comments.class);
        Mockito.when(origin.iterator())
            .thenReturn(list.iterator())
            .thenReturn(list.iterator());
        Mockito.when(origin.post(Mockito.anyString())).thenThrow(
            new IllegalStateException("Comment should not be posted!")
        );
        final Comments doNotRepeat = new DoNotRepeat(origin);
        final Comment comment = doNotRepeat.post("hey there");
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.equalTo("hey there")
        );
        MatcherAssert.assertThat(
            doNotRepeat,
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * DoNotRepeat does post a new comment.
     */
    @Test
    public void postsNewComment() {
        final Comment newComment = this.mockComment("new comment");
        final List<Comment> list = List.of(
            this.mockComment("hello world"),
            this.mockComment("hey there"),
            this.mockComment("hi")
        );
        final Comments origin = Mockito.mock(Comments.class);
        Mockito.when(origin.iterator())
            .thenReturn(list.iterator());
        Mockito.when(origin.post(Mockito.anyString())).thenReturn(
            newComment
        );
        final Comments doNotRepeat = new DoNotRepeat(origin);
        final Comment comment = doNotRepeat.post("new comment");
        MatcherAssert.assertThat(
            comment, Matchers.is(newComment)
        );
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.equalTo("new comment")
        );
    }

    /**
     * DoNotRepeat delegates comment receival to origin.
     */
    @Test
    public void receivesComment() {
        final JsonObject json = Mockito.mock(JsonObject.class);
        final Comment received = Mockito.mock(Comment.class);
        Mockito.when(received.json()).thenReturn(json);
        final Comments origin = Mockito.mock(Comments.class);
        Mockito.when(origin.received(json)).thenReturn(received);

        final Comments doNotRepeat = new DoNotRepeat(origin);
        MatcherAssert.assertThat(
            doNotRepeat.received(json),
            Matchers.is(received)
        );
        MatcherAssert.assertThat(
            doNotRepeat.received(json).json(),
            Matchers.is(json)
        );
    }

    /**
     * Mock a Comment for test.
     * @param body Comment body.
     * @return Comment.
     */
    public Comment mockComment(final String body) {
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.body()).thenReturn(body);
        return comment;
    }
}
