package com.selfxdsd.api.storage;

/**
 * Contains the necessary info for clients to access a {@link Storage} resource
 * (Projects, Contributors, Tasks etc...) in a paged manner.
 * @author criske
 * @version $Id$
 * @since 0.0.13
 * @todo #360:30min Provider paging implementation for other Self entities:
 *  Collaborators, Contracts, Contributors, Tasks, Invoices, InvoicedTasks.
 */
public interface Paged {

    /**
     * Current page. Page must be between 1 and totalPages.
     * @return Page.
     */
    Page current();

    /**
     * Total number of pages.
     * @return Integer.
     */
    int totalPages();

    /**
     * Data class that encapsulates the number and the size of a page.
     */
    final class Page {

        /**
         * Page number.
         */
        private final int number;

        /**
         * Page size.
         */
        private final int size;

        /**
         * Ctor.
         * @param number Page number.
         * @param size Page size.
         */
        public Page(final int number, final int size) {
            this.number = number;
            this.size = size;
        }

        /**
         * Page number.
         * @return Integer.
         */
        public int getNumber() {
            return number;
        }

        /**
         * Page size.
         * @return Integer.
         */
        public int getSize() {
            return size;
        }
    }

}
