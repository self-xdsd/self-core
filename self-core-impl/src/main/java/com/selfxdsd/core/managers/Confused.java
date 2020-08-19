package com.selfxdsd.core.managers;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversation where the PM says "didn't understand".
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class Confused implements Conversation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        Confused.class
    );

    @Override
    public Step start(final Event event) {
        final Step steps;
        if (Event.Type.CONFUSED.equals(event.type())) {
            steps = new SendReply(
                event.project().language().reply("misunderstand.comment"),
                lastly -> LOG.debug("Finished conversation.")
            );
        } else {
            throw new IllegalStateException("Invalid event type: "
                + event.type() + ". Confused must end the chain.");
        }
        return steps;
    }

}
