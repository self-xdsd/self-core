package com.selfxdsd.core.mock;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class InMemoryContributorsTestCase {

    /**
     * Register a project.
     */
    @Test
    public void contributorRegister() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors().register("horea",
                "github");
        assertThat(registered.username(),
                is("horea"));

    }

    @Test
    public void contributorGetById() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors().
            register("horea", "github");
        assertThat(storage.contributors().
            getById("horea", "github"),
            is(registered));

    }

    @Test
    public void iteratorWorks() {
        final Storage storage = new InMemory();
        Contributor registered = storage.contributors().
            register("horea", "github");
        MatcherAssert.assertThat(storage.contributors(), Matchers.iterableWithSize(1));
    }
}
