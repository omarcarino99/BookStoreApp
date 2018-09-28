package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    TextView bookName;
    TextView author;
    TextView bookPrice;
    TextView bookQuantity;
    TextView supplierName;
    TextView supplierNumber;
    private Button addQuantityButton;
    private Button subtractQuantityButton;
    private Button orderButton;
    Uri bookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final Intent intent = getIntent();
        bookUri = intent.getData();

        bookName = findViewById(R.id.book_name_details_list);
        author = findViewById(R.id.author_detail);
        bookPrice = findViewById(R.id.book_price_details_list);
        bookQuantity = findViewById(R.id.book_quantity_details);
        supplierName = findViewById(R.id.suppliers_name_details);
        supplierNumber = findViewById(R.id.suppliers_number_details);
        addQuantityButton = findViewById(R.id.add_quantity_button);
        subtractQuantityButton = findViewById(R.id.subtract_quantity_button);
        orderButton = findViewById(R.id.order_button);

        addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringBookQuantity = bookQuantity.getText().toString();
                int intQuantity = Integer.parseInt(stringBookQuantity);
                int intbookQuantity = intQuantity + 1;
                String finalBookQuantity = String.valueOf(intbookQuantity);
                bookQuantity.setText(finalBookQuantity);
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.QUANTITY, finalBookQuantity);
                getContentResolver().update(bookUri, values, null, null);
            }
        });

        subtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringBookQuantity = bookQuantity.getText().toString();
                int intQuantity = Integer.parseInt(stringBookQuantity);
                if (intQuantity > 0) {
                    int quantityLeft = intQuantity - 1;
                    String finalQuanity = String.valueOf(quantityLeft);
                    bookQuantity.setText(finalQuanity);
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.QUANTITY, finalQuanity);
                    getContentResolver().update(bookUri, values, null, null);
                } else {
                    Toast.makeText(DetailsActivity.this, "Out of Stock", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierNumberString = supplierNumber.getText().toString();
                Uri uri = Uri.parse("tel:" + supplierNumberString);
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(phoneIntent);
            }
        });
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.BOOK_NAME,
                BookContract.BookEntry.AUTHOR,
                BookContract.BookEntry.BOOK_PRICE,
                BookContract.BookEntry.QUANTITY,
                BookContract.BookEntry.SUPPLIER_NAME,
                BookContract.BookEntry.SUPPLIER_NUMBER
        };

        return new CursorLoader(
                this,
                bookUri,
                projections,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int bookNamecolumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.AUTHOR);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NUMBER);

            String name = cursor.getString(bookNamecolumnIndex);
            String stringAuthor = cursor.getString(authorColumnIndex);
            int price = cursor.getInt(bookPriceColumnIndex);
            int quantity = cursor.getInt(bookQuantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int supplierNumberCursor = cursor.getInt(supplierNumberColumnIndex);

            String stringPrice = String.valueOf(price);
            String stringQuantity = String.valueOf(quantity);
            String supplierNumberString = String.valueOf(supplierNumberCursor);

            bookName.setText(name);
            author.setText(stringAuthor);
            bookPrice.setText("$" + stringPrice);
            bookQuantity.setText(stringQuantity);
            supplierName.setText(supplier);
            supplierNumber.setText(supplierNumberString);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
        supplierName.setText("");
        supplierNumber.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_book_menu_button:
                Intent intent = new Intent(DetailsActivity.this, AddBookActivity.class);
                intent.setData(bookUri);
                startActivity(intent);
                return true;
            case R.id.delete_book_menu_item:
                confirmDelete();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel_label_delete_confirmation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = 0;
        rowsDeleted = getContentResolver().delete(bookUri, null, null);
        Intent mainActivityIntent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
