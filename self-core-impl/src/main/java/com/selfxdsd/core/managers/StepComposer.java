package com.selfxdsd.core.managers;

import com.selfxdsd.api.pm.Step;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper class that allows chaining {@link com.selfxdsd.api.pm.Intermediary}
 * steps.
 * <br/>
 * It also provides composition support for
 * {@link com.selfxdsd.api.pm.PreconditionCheck}.
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
     * Stars a ConditionComposer.
     * @param when BiFunction providing onTrue, onFalse. Returning should be a
     *             {@link com.selfxdsd.api.pm.PreconditionCheck}.
     * @return ConditionComposer.
     */
    public ConditionComposer when(final BiFunction<Step, Step, Step> when) {
        if(!callStack.isEmpty()){
            throw new IllegalStateException("Condition composition can't be "
                + " called after an next() call!");
        }
        return new ConditionComposer(when);
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


    /**
     * StepComposer for a PreconditionCheck.
     */
    public static final class ConditionComposer {

        /**
         * BiFunction providing onTrue, onFalse steps. Returning should be a
         * PreconditionCheck.
         */
        private final BiFunction<Step, Step, Step> when;

        /**
         * OnTrue Step supplier.
         */
        private Supplier<Step> onTrue;

        /**
         * OnFalse Step supplier.
         */
        private Supplier<Step> onFalse;

        /**
         * Private constructor.
         * @param when BiFunction providing onTrue, onFalse. Returning
         *             should be a PreconditionCheck.
         */
        private ConditionComposer(final BiFunction<Step, Step, Step> when) {
            this.when = when;
        }

        /**
         * OnTrue setter.
         * @param onTrue OnTrue Step supplier.
         * @return This ConditionComposer.
         */
        public ConditionComposer onTrue(final Supplier<Step> onTrue){
            this.onTrue = onTrue;
            return this;
        }

        /**
         * OnFalse setter.
         * @param onFalse OnFalse Step supplier.
         * @return This ConditionComposer.
         */
        public ConditionComposer onFalse(final Supplier<Step> onFalse){
            this.onFalse = onFalse;
            return this;
        }

        /**
         * Finishes the composition into a composed Step provided by `when`.
         * @return Step.
         */
        public Step finish(){
            if (onFalse == null) {
                onFalse = () -> e -> { };
            }
            if (onTrue == null) {
                onTrue = () -> e -> { };
            }
            return when.apply(onTrue.get(), onFalse.get());
        }
    }

}

