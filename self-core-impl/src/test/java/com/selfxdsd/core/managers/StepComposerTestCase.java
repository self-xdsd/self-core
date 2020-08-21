package com.selfxdsd.core.managers;

import com.selfxdsd.api.Event;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for {@link StepComposer}.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class StepComposerTestCase {

    /**
     * Executes the steps in order of chaining.
     */
    @Test
    public void executesStepsInOrder() {
        final StringBuilder builder = new StringBuilder();
        final AtomicInteger counter = new AtomicInteger();
        new StepComposer()
            .next(s -> e -> {
                builder.append(counter.incrementAndGet());
                s.perform(e);
            })
            .next(s -> e -> {
                builder.append(counter.incrementAndGet());
                s.perform(e);
            })
            .next(s -> e -> {
                builder.append(counter.incrementAndGet());
                s.perform(e);
            })
            .next(s -> e -> {
                builder.append(counter.incrementAndGet());
                s.perform(e);
            })
            .finish(lastly -> builder.append(counter.incrementAndGet()))
            .perform(Mockito.mock(Event.class));
        final String order = builder.toString();
        MatcherAssert.assertThat(order, Matchers.equalTo("12345"));
    }

}
