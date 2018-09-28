package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bookstoreapp.R;

import static com.example.android.bookstoreapp.data.BookContract.CONTENT_AUTHORITY;
import static com.example.android.bookstoreapp.data.BookContract.PATH_BOOKS;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private BookDbHelper mBookDbHelper;
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_message) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.insertion_failed_message) + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String bookName = values.getAsString(BookContract.BookEntry.BOOK_NAME);
        String author = values.getAsString(BookContract.BookEntry.AUTHOR);
        String bookPrice = values.getAsString(BookContract.BookEntry.BOOK_PRICE);
        String quantity = values.getAsString(BookContract.BookEntry.QUANTITY);
        String supplierName = values.getAsString(BookContract.BookEntry.SUPPLIER_NAME);
        String supplierNumber = values.getAsString(BookContract.BookEntry.SUPPLIER_NUMBER);

        if (bookName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.book_name_empty_mesage));
        }
        if (author == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.please_add_author));

        }
        if (bookPrice == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.add_price_message));
        }
        if (quantity == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.add_quantity_message));
        }
        if (supplierName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.add_supplier_name_message));
        }
        if (supplierNumber == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.add_supplier_number_message));
        }

        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.failed_to_save_message) + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.delete_failed_message) + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.update_failed_message));
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookContract.BookEntry.BOOK_NAME)) {
            String bookName = values.getAsString(BookContract.BookEntry.BOOK_NAME);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_name_empty_mesage));
            }
        }

        if (values.containsKey(BookContract.BookEntry.AUTHOR)) {
            String bookName = values.getAsString(BookContract.BookEntry.AUTHOR);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.please_add_author));
            }
        }

        if (values.containsKey(BookContract.BookEntry.BOOK_PRICE)) {
            String bookName = values.getAsString(BookContract.BookEntry.BOOK_PRICE);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.add_price_message));
            }
        }

        if (values.containsKey(BookContract.BookEntry.QUANTITY)) {
            String bookName = values.getAsString(BookContract.BookEntry.QUANTITY);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.add_quantity_message));
            }
        }

        if (values.containsKey(BookContract.BookEntry.SUPPLIER_NAME)) {
            String bookName = values.getAsString(BookContract.BookEntry.SUPPLIER_NAME);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.add_supplier_name_message));
            }
        }

        if (values.containsKey(BookContract.BookEntry.SUPPLIER_NUMBER)) {
            String bookName = values.getAsString(BookContract.BookEntry.SUPPLIER_NUMBER);
            if (bookName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_name_empty_mesage));
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
