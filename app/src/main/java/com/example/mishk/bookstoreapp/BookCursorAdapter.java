package com.example.mishk.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mishk.bookstoreapp.R;
import com.example.mishk.bookstoreapp.data.BookContract;

//Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
public class BookCursorAdapter extends CursorAdapter {
    private int mQuantity;

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView bookName = view.findViewById(R.id.name);
        TextView bookPrice = view.findViewById(R.id.price);

        final TextView bookQuantity = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);
        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_ID);
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_QUANTITY);
        int bookId = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        Double price = cursor.getDouble(priceColumnIndex);
        mQuantity = cursor.getInt(quantityColumnIndex);
        bookName.setText("Title: " + name);
        bookPrice.setText("Price: $" + String.valueOf(price));
        bookQuantity.setText("Quantity: " + String.valueOf(mQuantity));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cursor.moveToFirst()) {
                    if (mQuantity <= 0) {
                        Toast.makeText(view.getContext(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mQuantity--;
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.PRODUCT_QUANTITY, mQuantity);
                    int updRow = view.getContext().getContentResolver().update(BookContract.BookEntry.CONTENT_URI, values, null, null);
                    if (updRow == 0) {
                        Toast.makeText(view.getContext(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), R.string.quantity_update, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}
