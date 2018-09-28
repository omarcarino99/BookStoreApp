package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookDbHelper;

import static android.content.ContentUris.withAppendedId;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookDbHelper dbHelper;
    private TextView bookQuantity;
    private BookCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);
        bookQuantity = findViewById(R.id.quantity_list);
        adapter = new BookCursorAdapter(this, null);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        View header = getLayoutInflater().inflate(R.layout.list_header, null);
        listView.addHeaderView(header, null, false);
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri uri = withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        dbHelper = new BookDbHelper(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {
                BaseColumns._ID,
                BookContract.BookEntry.BOOK_NAME,
                BookContract.BookEntry.BOOK_PRICE,
                BookContract.BookEntry.QUANTITY
        };
        return new CursorLoader(
                this,
                BookContract.BookEntry.CONTENT_URI,
                projections,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void decreaseCount(Integer columnId, Integer quantity) {
        if (quantity > 0) {
            quantity = quantity - 1;
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, columnId);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
        } else {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
        }
    }
}
