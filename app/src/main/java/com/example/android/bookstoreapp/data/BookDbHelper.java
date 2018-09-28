package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "books.db";

    public static final int VERSION_NUMBER = 3;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.AUTHOR + " TEXT NOT NULL, "
                + BookContract.BookEntry.BOOK_PRICE + " INTEGER DEFAULT 0, "
                + BookContract.BookEntry.QUANTITY + " INTEGER DEFAULT 0, "
                + BookContract.BookEntry.SUPPLIER_NAME + " TEXT, "
                + BookContract.BookEntry.SUPPLIER_NUMBER + " INTEGER);";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
