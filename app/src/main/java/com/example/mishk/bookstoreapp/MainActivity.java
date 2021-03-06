package com.example.mishk.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mishk.bookstoreapp.data.BookContract.BookEntry;

//Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter bookCursorAdapter;
    private Uri currentBookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up FAB button to open ProductDetailsActivity
        FloatingActionButton addBook = findViewById(R.id.add_book_button);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openEditor = new Intent (MainActivity.this, ProductDetailsActivity.class);
                startActivity(openEditor);
            }
        });
        //Find ListView
        ListView booksList = findViewById(R.id.list);
        //Set up EmptyView for the case when there are no books in the list
        View emptyView = findViewById(R.id.empty_view);
        booksList.setEmptyView(emptyView);
        bookCursorAdapter = new BookCursorAdapter(this, null);
        booksList.setAdapter(bookCursorAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle bundle){
        String[] projection = {
                BookEntry.PRODUCT_ID,
                BookEntry.PRODUCT_NAME,
                BookEntry.PRODUCT_PRICE,
                BookEntry.PRODUCT_QUANTITY};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        bookCursorAdapter.swapCursor(data);
        }
        @Override
    public void onLoaderReset(Loader<Cursor> loader){
            bookCursorAdapter.swapCursor(null);
        }
}

