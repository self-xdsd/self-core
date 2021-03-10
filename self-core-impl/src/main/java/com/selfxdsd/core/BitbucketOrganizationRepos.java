package com.selfxdsd.core;

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A Bitbucket Provider Organization Repos.
 *
 * @author Nikita Monokov (nmonokov@gmail.com)
 * @version $Id$
 * @since 0.0.64
 */
public final class BitbucketOrganizationRepos implements Repos {

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
        final Resource resource = resources.get(this.reposUri);
        final int statusCode = resource.statusCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            return resource.asJsonObject()
                .getJsonArray("values")
                .stream()
                .map(this::buildRepo)
                .iterator();
        } else {
            throw new IllegalStateException("Unable to fetch Bitbucket "
                + "organization Repos for current User. Expected 200 OK, "
                + "but got: " + statusCode);
        }
    }

    /**
     * Builds a repo from provided JSON data.
     *
     * @param repoData Repo as JSON.
     * @return Repo.
     */
    private Repo buildRepo(final JsonValue repoData) {
        final String repoUri =  this.reposUri.toString() + "/"
            + repoData.asJsonObject().getString("slug");
        return new BitbucketRepo(
            this.resources,
            URI.create(repoUri),
            this.owner,
            this.storage
        );
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
