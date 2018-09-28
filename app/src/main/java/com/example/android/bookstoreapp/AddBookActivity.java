package com.example.android.bookstoreapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class AddBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    EditText bookName;
    EditText bookAuthor;
    EditText bookPrice;
    EditText bookQuantity;
    EditText supplierName;
    EditText supplierNumber;
    Button saveButton;
    private Uri bookUri;
    private Boolean bookHasChanged = false;
    BookDbHelper mBookDbHelper;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        mBookDbHelper = new BookDbHelper(this);
        bookName = findViewById(R.id.book_name_details_list);
        bookAuthor = findViewById(R.id.author_edit_text);
        bookPrice = findViewById(R.id.book_price_edit_text);
        bookQuantity = findViewById(R.id.book_quantity_details);
        supplierName = findViewById(R.id.suppliers_name_details);
        supplierNumber = findViewById(R.id.suppliers_number_details);

        bookName.setOnTouchListener(mOnTouchListener);
        bookAuthor.setOnTouchListener(mOnTouchListener);
        bookPrice.setOnTouchListener(mOnTouchListener);
        bookQuantity.setOnTouchListener(mOnTouchListener);
        supplierNumber.setOnTouchListener(mOnTouchListener);
        supplierName.setOnTouchListener(mOnTouchListener);

        saveButton = findViewById(R.id.save_button);
        bookUri = BookContract.BookEntry.CONTENT_URI;
        ListView list = findViewById(R.id.list);

        Intent intent = getIntent();
        bookUri = intent.getData();
        if (bookUri == null) {
            setTitle(getString(R.string.add_book_title_label));
            saveButton.setText(R.string.save_button_label);
        } else {
            setTitle(getString(R.string.edit_book_title_label));
            saveButton.setText(R.string.update_button_label);
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });


    }

    private void saveBook() {
        String stringBook = bookName.getText().toString();
        String stringAuthor = bookAuthor.getText().toString();
        String stringBookPrice = bookPrice.getText().toString();
        String stringBookQuantity = bookQuantity.getText().toString();
        String stringSupplierName = supplierName.getText().toString();
        String stringSuppliersNumber = supplierNumber.getText().toString();
        if (stringBook.isEmpty() || stringAuthor.isEmpty() || stringBookPrice.isEmpty() || stringBookQuantity.isEmpty()
                || stringSupplierName.isEmpty() || stringSuppliersNumber.isEmpty()) {
            Toast.makeText(this, R.string.complete_form_message, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.BOOK_NAME, stringBook);
        values.put(BookContract.BookEntry.AUTHOR, stringAuthor);
        values.put(BookContract.BookEntry.BOOK_PRICE, stringBookPrice);
        values.put(BookContract.BookEntry.QUANTITY, stringBookQuantity);
        values.put(BookContract.BookEntry.SUPPLIER_NAME, stringSupplierName);
        values.put(BookContract.BookEntry.SUPPLIER_NUMBER, stringSuppliersNumber);
        if (bookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
        } else {
            int rowsUpdated = getContentResolver().update(bookUri, values, null, null);
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {
                BaseColumns._ID,
                BookContract.BookEntry.BOOK_NAME,
                BookContract.BookEntry.AUTHOR,
                BookContract.BookEntry.BOOK_PRICE,
                BookContract.BookEntry.QUANTITY,
                BookContract.BookEntry.SUPPLIER_NAME,
                BookContract.BookEntry.SUPPLIER_NUMBER,
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
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int  price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierNameString = cursor.getString(supplierNameColumnIndex);
            int supplierNumberCursor = cursor.getInt(supplierNumberColumnIndex);

            bookName.setText(name);
            bookAuthor.setText(author);
            bookPrice.setText("$" + String.valueOf(price));
            bookQuantity.setText(String.valueOf(quantity));
            supplierName.setText(supplierNameString);
            supplierNumber.setText(String.valueOf(supplierNumberCursor));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
        supplierNumber.setText("");
        supplierName.setText("");
    }

    @Override
    public void onBackPressed() {
        if (bookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.delete, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel_label_delete_confirmation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = 0;
        rowsDeleted = getContentResolver().delete(bookUri, null, null);
        Intent mainActivityIntent = new Intent(AddBookActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_book_menu_item:
                confirmDelete();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
