package com.example.mishk.bookstoreapp.data;
import android.provider.BaseColumns;

public final class BookContract {
    //Constructor for the class
    private BookContract() {}
    //Book database schema
    public static abstract class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "Books";
        public final static String PRODUCT_NAME = "Product_Name";
        public final static String PRODUCT_PRICE = "Price";
        public final static String PRODUCT_QUANTITY = "Quantity";
        public final static String SUPPLIER_NAME = "Supplier_Name";
        public final static String SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
    }
}
