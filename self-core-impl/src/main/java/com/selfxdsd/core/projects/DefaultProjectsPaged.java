package com.selfxdsd.core.projects;

import com.selfxdsd.api.*;

import java.util.Iterator;

/**
 * Default implementation of {@link ProjectsPaged}.
 * @author criske
 * @version $Id$
 * @since 0.0.13
 * @todo #345:30min Write unit tests for the default implementation
 *  of {@link ProjectsPaged}.
 */
public final class DefaultProjectsPaged implements ProjectsPaged {

    /**
     * Current page.
     */
    private final Page current;

    /**
     * Total number of Projects across all pages.
     */
    private final int totalRecords;

    /**
     * Projects in the current Page.
     */
    private final Projects projects;

    /**
     * Ctor.
     * @param current Current page.
     * @param totalRecords Total number of Projects across all pages.
     * @param projects Projects in the current Page.
     */
    public DefaultProjectsPaged(final Page current,
                                final int totalRecords,
                                final Projects projects) {
        this.current = current;
        this.projects = projects;
        this.totalRecords = totalRecords;
        final int totalPages = this.totalPages();
        if(current.getNumber() < 1 && current.getNumber() > totalPages){
            throw new IllegalStateException("Invalid page number "
                + current.getNumber() + ". Must be between 1 and "
                + totalPages);
        }
    }

    @Override
    public Page current() {
        return this.current;
    }

    @Override
    public int totalPages() {
        return Math.max(1, this.totalRecords / current.getSize());
    }

    @Override
    public Project register(final Repo repo,
                            final ProjectManager manager,
                            final String webHookToken) {
        return this.projects.register(repo, manager, webHookToken);
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        return this.projects.assignedTo(projectManagerId);
    }

    @Override
    public Projects ownedBy(final User user) {
        return this.projects.ownedBy(user);
    }

    @Override
    public Project getProjectById(final String repoFullName,
                                  final String repoProvider) {
        return this.projects.getProjectById(repoFullName, repoProvider);
    }

    @Override
    public ProjectsPaged page(final Page page) {
        throw new IllegalStateException("Already called page!");
    }

    @Override
    public Iterator<Project> iterator() {
        return this.projects.iterator();
    }
}
