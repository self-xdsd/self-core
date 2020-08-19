/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Event;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.pm.Step;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link SendReply}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class SendReplyTestCase {

    /**
     * SendReply can post a reply in the Issue's Comments and forward
     * the event to the next Step in the chain.
     */
    @Test
    public void postsCommentReplyAndForwardsEvent() {
        final Comments comments = Mockito.mock(Comments.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.comments()).thenReturn(comments);

        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.body()).thenReturn("How are you?");
        Mockito.when(comment.author()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.comment()).thenReturn(comment);

        final String expected = new StringBuilder()
            .append("> How are you?").append("\n\n")
            .append("@mihai I'm fine, thank you!")
            .toString();

        final Step step = new SendReply(
            "I'm fine, thank you!",
            next -> MatcherAssert.assertThat(next, Matchers.is(event))
        );

        step.perform(event);
        Mockito.verify(comments, Mockito.times(1)).post(expected);
    }

}
