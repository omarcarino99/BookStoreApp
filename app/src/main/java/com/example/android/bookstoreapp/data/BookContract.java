package com.example.android.bookstoreapp.data;

import android.net.Uri;
import android.provider.BaseColumns;


public final class BookContract {

    private BookContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS= "books";

    public static final class BookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;

        public static final String BOOK_NAME = "product_name";

        public static final String AUTHOR = "author";

        public static final String BOOK_PRICE = "price";

        public static final String QUANTITY = "quantity";

        public static final String SUPPLIER_NAME = "supplier_name";

        public static final String SUPPLIER_NUMBER = "supplier_phone_number";
    }
}
