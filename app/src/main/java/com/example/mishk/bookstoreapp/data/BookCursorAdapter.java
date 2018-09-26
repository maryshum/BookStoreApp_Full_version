package com.example.mishk.bookstoreapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mishk.bookstoreapp.R;

//Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
public class BookCursorAdapter extends CursorAdapter {
    private int quantityNew;

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookName = view.findViewById(R.id.name);
        TextView bookPrice = view.findViewById(R.id.price);
        final TextView bookQuantity = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_QUANTITY);
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        bookName.setText(name);
        bookPrice.setText(price);
        bookQuantity.setText(quantity);
        quantityNew = Integer.valueOf(quantity);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityNew < 0) {
                    throw new IllegalArgumentException("Quantity can`t be a negative value");
                } else {
                    quantityNew = quantityNew - 1;
                    bookQuantity.setText(quantityNew);
                }
            }
        });
    }
}
