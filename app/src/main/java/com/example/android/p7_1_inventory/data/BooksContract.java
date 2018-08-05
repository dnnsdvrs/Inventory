package com.example.android.p7_1_inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BooksContract {
    // Empty constructor, to prevent someone from accidentally instantiating the contract class
    private BooksContract() {}

    /**
     * Construct the Uri
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.p7_1_inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     */

    public static abstract class BooksEntry implements BaseColumns {

        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //Name of the table inside the database
        public static final String TABLE_NAME = "Books";

        //Columns in the books table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "Title";
        public static final String COLUMN_BOOK_AUTHOR = "Author";
        public static final String COLUMN_BOOK_GENRE = "Genre";
        public static final String COLUMN_BOOK_SUPL_NAME = "Supplier_name";
        public static final String COLUMN_BOOK_SUPL_PHONE = "Supplier_phonenumber";
        public static final String COLUMN_BOOK_PRICE = "Price";
        public static final String COLUMN_BOOK_QUANTITY = "Quantity";

        //Constants for genre column
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_FICTION = 1;
        public static final int GENRE_NONFICTION = 2;

        //Sanity check to make sure genre returns a valid value
        public static boolean isGenreValid(int genre) {
            if (genre == GENRE_UNKNOWN || genre == GENRE_FICTION || genre == GENRE_NONFICTION) {
                return true;
            }
            return false;
        }

    }
}
