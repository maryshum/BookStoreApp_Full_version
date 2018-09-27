package com.example.mishk.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.mishk.bookstoreapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "store.db";
private static final int DATABASE_VERSION = 1;
    //Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
//SQLiteOpenHelper constructor
    public BookDbHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a String that contains SQL statement for creating a database
        String SQL_CREATE_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookEntry.PRODUCT_NAME + " TEXT NOT NULL,"
                + BookEntry.PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.00,"
                + BookEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + BookEntry.SUPPLIER_NAME + " TEXT NOT NULL,"
                + BookEntry.SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_TABLE);
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

