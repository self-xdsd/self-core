package com.selfxdsd.core;

import com.selfxdsd.api.storage.Paged;

import java.util.function.Supplier;

/**
 * Base implementation of {@link Paged}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.13
 */
public abstract class BasePaged implements Paged {

    /**
     * Current page.
     */
    private final Page current;

    /**
     * Total number of Projects across all pages obtained dynamically
     * from data source. This ensure we are in sync with the data source size.
     */
    private final Supplier<Integer> totalRecords;

    /**
     * Ctor.
     * @param current Current page.
     * @param totalRecords Total number of Projects across all pages
     *                     obtained dynamically from data source.
     */
    protected BasePaged(final Page current,
                        final Supplier<Integer> totalRecords) {
        this.current = current;
        this.totalRecords = totalRecords;
        final int totalPages = this.totalPages();
        if (current.getNumber() < 1 || current.getNumber() > totalPages) {
            throw new IllegalStateException("Invalid page number "
                + current.getNumber() + ". Must be between 1 and "
                + totalPages);
        }
    }

    @Override
    public final Page current() {
        return this.current;
    }

    @Override
    public final int totalPages() {
        final int size = this.current.getSize();
        return Math.max(1, (this.totalRecords.get() + size - 1) / size);
    }

}
