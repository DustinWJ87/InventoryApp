package com.example.android.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API contract for the BookStore app.
 */
public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class
    private BookContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.books";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY );

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represent a single book.
     */
    public static abstract class BookEntry implements BaseColumns {

        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath( BASE_CONTENT_URI, PATH_BOOKS );

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books
         */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Name of the book
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        /**
         * Price of the book
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";
        /**
         * Quantity of the book
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        /**
         * Supplier name for the book
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        /**
         * Supplier phone number for the book
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}