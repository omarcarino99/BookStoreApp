package com.example.android.bookstoreapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;


public class BookCursorAdapter extends CursorAdapter {

    Context context;
    Cursor mCursor;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        this.mCursor = cursor;

        TextView bookTitle = view.findViewById(R.id.book_name_details_list);
        TextView bookPrice = view.findViewById(R.id.book_price_details_list);
        final TextView bookQuantity = view.findViewById(R.id.quantity_list);
        Button buyButton = view.findViewById(R.id.purchase_button_list);

        final int position = cursor.getPosition();
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor.moveToPosition(position);
                int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
                int quantityColumn = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY);
                cursor.getPosition();

                String column = cursor.getString(idColumnIndex);
                int quantity = cursor.getInt(quantityColumn);

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.decreaseCount(Integer.valueOf(column), quantity);
            }
        });

        String title = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
        final String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));

        bookTitle.setText(title);
        bookPrice.setText(context.getString(R.string.dollar_sign) + String.valueOf(price));
        bookQuantity.setText(String.valueOf(quantity));
    }
}

