package com.selfxdsd.core.projects;

import com.selfxdsd.api.Projects;

/**
 * Base paging implementation of Projects.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.13
 */
public abstract class ProjectsPaged implements Projects {

    /**
     * Current page.
     */
    private final Page current;

    /**
     * Total number of Project from data source.
     */
    private final int totalRecords;

    /**
     * Ctor.
     * @param current Current page.
     * @param totalRecords Total number of Project from data source.
     */
    protected ProjectsPaged(final Page current,
                            final int totalRecords) {
        this.current = current;
        this.totalRecords = totalRecords;
    }


    @Override
    public final Page current() {
        return this.current;
    }

    @Override
    public final int totalPages() {
        final int totalPages = Math.max(1,
            this.totalRecords / current.getSize());
        if (current.getNumber() < 1 || current.getNumber() > totalPages) {
            throw new IllegalStateException("Invalid page number "
                + current.getNumber() + ". Must be between 1 and "
                + totalPages);
        }
        return totalPages;
    }

}
