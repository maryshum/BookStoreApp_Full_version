package com.example.mishk.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mishk.bookstoreapp.data.BookContract.BookEntry;
import com.example.mishk.bookstoreapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new BookDbHelper(this);
        setContentView(R.layout.activity_main);
        insertData();
        displayBooksList();
    }
    //Reference used for the code below: Lesson 2 in Data Storage section of the Android Basics Nanodegree
    private void insertData(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BookEntry.PRODUCT_NAME, "Martin Eden");
        values.put(BookEntry.PRODUCT_PRICE, 20);
        values.put(BookEntry.PRODUCT_QUANTITY, 100);
        values.put(BookEntry.SUPPLIER_NAME, "New Books");
        values.put(BookEntry.SUPPLIER_PHONE_NUMBER, 2024999167);
        long newRow = db.insert(BookEntry.TABLE_NAME, null, values);
        }

        private Cursor queryData() {
        //Get a writable database using SQLiteOpenHelper
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //Select rows and columns
            String[] projection = {
                    BookEntry.PRODUCT_NAME,
                    BookEntry.PRODUCT_PRICE,
                    BookEntry.PRODUCT_QUANTITY,
                    BookEntry.SUPPLIER_NAME,
                    BookEntry.SUPPLIER_PHONE_NUMBER,
            };
          return db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);
        }
        private void displayBooksList (){
            Cursor cursor = queryData();
            TextView testView = findViewById(R.id.test_text);
            try {
                //Display a header text
                testView.setText("Available number of books: " + cursor.getCount());
                //Display headers of the columns
                testView.append("\n" + BookEntry.PRODUCT_NAME + " - " + BookEntry.PRODUCT_PRICE +
                        " - " + BookEntry.PRODUCT_QUANTITY + " - " + BookEntry.SUPPLIER_NAME +
                        " - " + BookEntry.SUPPLIER_PHONE_NUMBER);
                //Get index of each column
                int nameIndex = cursor.getColumnIndex(BookEntry.PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndex(BookEntry.PRODUCT_PRICE);
                int quantityIndex = cursor.getColumnIndex(BookEntry.PRODUCT_QUANTITY);
                int supplierNameIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_NAME);
                int supplierPhoneIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_PHONE_NUMBER);
                //Go trough all the returned rows of cursor
                while (cursor.moveToNext()) {
                    //Use index to get values from the rows the cursor is currently on
                    String productName = cursor.getString(nameIndex);
                    int productPrice = cursor.getInt(priceIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String supplier = cursor.getString(supplierNameIndex);
                    int supplierPhoneNumber = cursor.getInt(supplierPhoneIndex);
                    //Display given values in the test TextView
                    testView.append(("\n" + productName + " - " + productPrice + " - " + quantity +
                            " - " + supplier + " - " + supplierPhoneNumber));
                }
            }
            finally {
                cursor.close();
            }
}
}
