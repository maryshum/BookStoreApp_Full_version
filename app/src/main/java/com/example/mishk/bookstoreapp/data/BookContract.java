package com.example.mishk.bookstoreapp.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
//Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
public final class BookContract {
    //Constructor for the class
    private BookContract() {}
    public final static String CONTENT_AUTHORITY = "com.example.mishk.bookstoreapp";
    public final static String PATH_BOOKS = "Books";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Book database schema
    public static abstract class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "Books";
        public final static String PRODUCT_ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "Product_Name";
        public final static String PRODUCT_PRICE = "Price";
        public final static String PRODUCT_QUANTITY = "Quantity";
        public final static String SUPPLIER_NAME = "Supplier_Name";
        public final static String SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        //MIME type for the list of books
        public final static String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_BOOKS;
        //MIME type for one book
        public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_BOOKS;
    }
}
