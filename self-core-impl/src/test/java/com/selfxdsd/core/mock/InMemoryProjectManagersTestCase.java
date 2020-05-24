package com.selfxdsd.core.mock;

import com.selfxdsd.api.storage.Storage;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests for class {@link InMemoryProjectManagers}.
 *
 * @author hpetrila
 * @version $Id$
 * @todo #82:5min Rename InMemoryTestCase to InMemoryUsersTestCase.
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
        assertThat(inMemoryPm.getById(1),
                is(inMemoryPm.pick()));
    }

    /**
     * Get inexistent project manager by id should return null.
     */
    @Test
    public void getInexistentProjectManagerById() {
        final Storage storage = new InMemory();
        new InMemoryProjectManagers(storage);
        assertThat(storage.projectManagers().getById(2), equalTo(null));
    }

    /**
     * Calling the class twice should not create a new PM with incremented ID.
     */
    @Test
    public void createTwoPMs() {
        final Storage storage = new InMemory();
        new InMemoryProjectManagers(storage);
        new InMemoryProjectManagers(storage);
        assertThat(storage.projectManagers().getById(2), equalTo(null));
    }

    /**
     * Pick project manager should return Project Manager with id 1.
     */
    public void pickProjectManager() {
        final Storage storage = new InMemory();
        InMemoryProjectManagers inMemoryPm =
                new InMemoryProjectManagers(storage);
        assertThat(inMemoryPm.pick(),
                is(inMemoryPm.getById(1)));

    }


}
