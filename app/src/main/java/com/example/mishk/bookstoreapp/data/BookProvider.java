package com.example.mishk.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

//Reference used for this code: Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course
public class BookProvider extends ContentProvider {
    //Tag for the log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    //Uri matcher code for all books in the table
    private static final int BOOKS = 100;
    //Uri matcher code for one book from the table
    private static final int BOOK_ID = 101;
    //Uri matcher object to match a URI to the content
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer that is used when anything is called for the first time from the BookProvider class
    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper bookDbHelper;

    @Override
    //Initializing the BookProvider and the helper object
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    //Query the URI using projection, selection, selectionArgs, etc.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        //Declare cursor that will hold the result of the query
        Cursor cursor;
        //Check if cursor matcher can match URI to the code
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry.PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //Return MIME type for content URI
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    //Insert data into provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        String bookName = contentValues.getAsString(BookContract.BookEntry.PRODUCT_NAME);
        if (TextUtils.isEmpty(bookName)) {
            throw new IllegalArgumentException("Book name required");
        }
        Double bookPrice = contentValues.getAsDouble(BookContract.BookEntry.PRODUCT_PRICE);
        if (bookPrice != null && bookPrice < 0) {
            throw new IllegalArgumentException("Valid book price required");
        }
        Integer bookQuantity = contentValues.getAsInteger(BookContract.BookEntry.PRODUCT_QUANTITY);
        if (bookQuantity != null && bookQuantity < 0) {
            throw new IllegalArgumentException("Valid quantity of books required");
        }
        String supplierName = contentValues.getAsString(BookContract.BookEntry.SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("Supplier name required");
        }
        String supplierPhoneNumber = contentValues.getAsString(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER);
        if (TextUtils.isEmpty(supplierPhoneNumber)) {
            throw new IllegalArgumentException("Supplier phone number required");
        }
        //Get a writable database and insert new book with given values
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert the row for " + uri);
            return null;
        }
        //Notify listeners about the data changes
        getContext().getContentResolver().notifyChange(uri, null);
        //Return new Uri with ID
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry.PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    //Update data for selection and selectionArgs with new content values
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry.PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(BookContract.BookEntry.PRODUCT_NAME)) {
            String bookName = contentValues.getAsString(BookContract.BookEntry.PRODUCT_NAME);
            if (TextUtils.isEmpty(bookName)) {
                throw new IllegalArgumentException("Book name required");
            }
        }
        if (contentValues.containsKey(BookContract.BookEntry.PRODUCT_PRICE)) {
            Double price = contentValues.getAsDouble(BookContract.BookEntry.PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Valid book price required");
            }
        }
        if (contentValues.containsKey(BookContract.BookEntry.PRODUCT_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(BookContract.BookEntry.PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Valid quantity of books required");
            }
        }
        if (contentValues.containsKey(BookContract.BookEntry.SUPPLIER_NAME)) {
            String supplier = contentValues.getAsString(BookContract.BookEntry.SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplier)) {
                throw new IllegalArgumentException("Supplier name required");
            }
        }
        if (contentValues.containsKey(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = contentValues.getAsString(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                throw new IllegalArgumentException("Supplier phone number required");
            }
        }
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        //Notify listeners of the data changes
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
