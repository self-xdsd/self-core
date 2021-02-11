package com.selfxdsd.core;

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import java.net.URI;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A Bitbucket Provider Organization Repos.
 *
 * @author Nikita Monokov (nmonokov@gmail.com)
 * @version $Id$
 * @since 0.0.62
 * todo #978:60min Continue class implementation
 */
public class BitbucketOrganizationRepos implements Repos {

    /**
     * Organization Repos URI.
     */
    private final URI reposUri;

    /**
     * Current authenticated User.
     */
    private final User owner;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Storage used by Organization Repo.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param uri Organization Repos URI.
     * @param owner Current authenticated User.
     * @param resources Bitbucket's JSON Resources.
     * @param storage Storage used by Organization Repo.
     */
    public BitbucketOrganizationRepos(final URI uri,
                                      final User owner,
                                      final JsonResources resources,
                                      final Storage storage) {
        this.reposUri = uri;
        this.owner = owner;
        this.resources = resources;
        this.storage = storage;
    }

    @Override
    public Iterator<Repo> iterator() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void forEach(final Consumer<? super Repo> action) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Spliterator<Repo> spliterator() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
