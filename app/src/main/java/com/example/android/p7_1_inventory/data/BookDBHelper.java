package com.example.android.p7_1_inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.p7_1_inventory.data.BooksContract.BooksEntry;

public class BookDBHelper extends SQLiteOpenHelper {

    //Database info
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public BookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BooksEntry.TABLE_NAME +
                "(" +
                BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                BooksEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, " + //Title of the book
                BooksEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, " + //Author name of the book
                BooksEntry.COLUMN_BOOK_GENRE + " INTEGER NOT NULL," + //Genre fiction, non-fiction or unknown
                BooksEntry.COLUMN_BOOK_PRICE + " INTEGER, " + //Price of the book
                BooksEntry.COLUMN_BOOK_QUANTITY + " INTEGER, " + //Quantity in stock
                BooksEntry.COLUMN_BOOK_SUPL_NAME + " TEXT, " + //Name of the supplier
                BooksEntry.COLUMN_BOOK_SUPL_PHONE + " TEXT" + //Phone number of the supplier
                ")";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BooksEntry.TABLE_NAME);
            onCreate(db);
        }
    }

}
