package com.selfxdsd.core.mock;

import com.selfxdsd.api.storage.Storage;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for class {@link InMemoryProjectManagers}.
 *
 * @author hpetrila
 * @version $Id$
 * @since 0.0.1
 */

public final class InMemoryProjectManagersTestCase {
    /**
     * Get project manager by id.
     */
    @Test
    public void getProjectManagerById() {
        final Storage storage = new InMemory();
        InMemoryProjectManagers inMemoryPm =
                new InMemoryProjectManagers(storage);

        assertThat(inMemoryPm.getById(1).id(),
                equalTo(storage.projectManagers().getById(1).id()));
    }

    /**
     * Get inexistent project manager by id should return null.
     */
    @Test
    public void getInexistentProjectManagerById() {
        final Storage storage = new InMemory();
        new InMemoryProjectManagers(storage);
        assertThat(storage.projectManagers().getById(3), equalTo(null));
    }

    /**
     * Calling the class twice should not create a new PM with incremented ID.
     */
    @Test
    public void createTwoPMs() {
        final Storage storage = new InMemory();
        new InMemoryProjectManagers(storage);
        new InMemoryProjectManagers(storage);
        assertThat(storage.projectManagers(), iterableWithSize(2));
    }

    /**
     * Pick project manager should return Project Manager with id 1.
     */
    @Test
    public void pickProjectManager() {
        final Storage storage = new InMemory();
        InMemoryProjectManagers inMemoryPm =
                new InMemoryProjectManagers(storage);
        assertThat(inMemoryPm.pick("github"), is(notNullValue()));


    }


}
