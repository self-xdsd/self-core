package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matcher for {@link Comment}.
 *
 * @version $Id$
 * @since 0.0.41
 */
public final class IsComment extends TypeSafeDiagnosingMatcher<Comment> {

    /**
     * Matcher for id.
     */
    private final Matcher<?> id;

    /**
     * Matcher for author.
     */
    private final Matcher<?> author;

    /**
     * Matcher for body.
     */
    private final Matcher<?> body;


    /**
     * Ctor.
     *
     * @param id Matcher for id.
     * @param author Matcher for author.
     * @param body Matcher for body.
     */
    public IsComment(
        final Matcher<?> id,
        final Matcher<?> author,
        final Matcher<?> body
    ) {
        this.id = id;
        this.author = author;
        this.body = body;
    }

    /**
     * Ctor.
     *
     * @param id Id.
     * @param author Author.
     * @param body Body.
     */
    public IsComment(
        final String id,
        final String author,
        final String body
    ) {
        this(
            Matchers.is(id),
            Matchers.is(author),
            Matchers.is(body)
        );
    }

    @Override
    protected boolean matchesSafely(
        final Comment item,
        final Description mismatchDescription
    ) {
        boolean result = true;
        if (!this.id.matches(item.commentId())) {
            mismatchDescription.appendText("id: ");
            this.id.describeMismatch(item.commentId(), mismatchDescription);
            result = false;
        }
        if (!this.author.matches(item.author())) {
            mismatchDescription.appendText("author: ");
            this.author.describeMismatch(item.author(), mismatchDescription);
            result = false;
        }
        if (!this.body.matches(item.body())) {
            mismatchDescription.appendText("body: ");
            this.body.describeMismatch(item.body(), mismatchDescription);
            result = false;
        }
        return result;
    }

    @Override
    public void describeTo(final Description description) {
        description
            .appendText("author: ")
            .appendDescriptionOf(this.author)
            .appendText(", ")
            .appendText("id: ")
            .appendDescriptionOf(this.id)
            .appendText(", ")
            .appendText("body: ")
            .appendDescriptionOf(this.body);
    }
}
