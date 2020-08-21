package com.selfxdsd.core.managers;

import com.selfxdsd.api.pm.Step;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * Helper class that allows chaining {@link com.selfxdsd.api.pm.Intermediary}
 * steps.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class StepComposer {

    /**
     * Call stack of step functions. Usually this functions produce
     * {@link com.selfxdsd.api.pm.Intermediary} steps.
     */
    private final Deque<Function<Step, Step>> callStack = new ArrayDeque<>();

    /**
     * Next Step in chain.
     * @param stepFunction Step Function.
     * @return Step, usually an Intermediary.
     */
    public StepComposer next(final Function<Step, Step> stepFunction) {
        callStack.push(stepFunction);
        return this;
    }

    /**
     * Finishes the chain into one step.
     * @param lastStep Last Step.
     * @return Composed Step.
     */
    public Step finish(final Step lastStep) {
        Step composedStep = lastStep;
        while (!callStack.isEmpty()) {
            composedStep = callStack.pop().apply(composedStep);
        }
        return composedStep;
    }

    /**
     * Finishes the chain into one step.
     * @return Composed Step.
     */
    public Step finish(){
        return finish(lastly -> {});
    }

}

