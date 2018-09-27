package com.example.mishk.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mishk.bookstoreapp.data.BookContract;
//Reference used for this code:
// Pets apps from lessons 4 and 5 of Udacity Android Basics Nanodegree Course,
//https://github.com/grazytiburcio/InventoryApp
//https://github.com/Clacli/MyBookInventory

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private int mBookQuantity;
    private String mSupplierPhone;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mDecrementButton;
    private Button mIncrementButton;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        //Get data from Intent
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            setTitle(R.string.details_activity_add_book);
        } else {
            setTitle(R.string.details_activity_edit_book);
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }
        mNameEditText = findViewById(R.id.name);
        mPriceEditText = findViewById(R.id.price);
        mQuantityEditText = findViewById(R.id.quantity);
        mSupplierNameEditText = findViewById(R.id.supplier);
        mSupplierPhoneEditText = findViewById(R.id.supplier_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mDecrementButton = findViewById(R.id.decrement_button);
        mIncrementButton = findViewById(R.id.increment_button);

        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString().trim();
                String quantityValue = quantity.matches("") ? "0" : quantity;
                int quantityFin = Integer.parseInt(quantityValue);
                updateQuantity(quantityFin, false);

            }
        });
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString().trim();
                if (quantity.matches("")) {
                    mQuantityEditText.setText("0");
                }
                String quantityValue = quantity.matches("") ? "0" : quantity;
                int quantityFin = Integer.parseInt(quantityValue);
                updateQuantity(quantityFin, true);

            }
        });
    }

    private void updateQuantity(int quantityFin, boolean decrease) {
        if (decrease) {
            quantityFin--;
        } else {
            quantityFin++;
        }
        if (mCurrentBookUri != null) {
            if (quantityFin >= 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(BookContract.BookEntry.PRODUCT_QUANTITY, quantityFin);
                getContentResolver().update(mCurrentBookUri, contentValues, null, null);
                if (quantityFin == 0) {
                    Toast.makeText(this, getString(R.string.none), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private void saveBook() {
        String bookName = mNameEditText.getText().toString().trim();
        String bookPrice = mPriceEditText.getText().toString().trim();
        final String bookQuantity = mQuantityEditText.getText().toString().trim();
        String bookSupplier = mSupplierNameEditText.getText().toString().trim();
        mSupplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(bookName) && TextUtils.isEmpty(bookPrice) &&
                TextUtils.isEmpty(bookQuantity) && TextUtils.isEmpty(bookSupplier) &&
                TextUtils.isEmpty(mSupplierPhone)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookEntry.PRODUCT_NAME, bookName);
        contentValues.put(BookContract.BookEntry.PRODUCT_PRICE, bookPrice);
        contentValues.put(BookContract.BookEntry.PRODUCT_QUANTITY, bookQuantity);
        contentValues.put(BookContract.BookEntry.SUPPLIER_NAME, bookSupplier);
        contentValues.put(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER, mSupplierPhone);
        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, contentValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_book_option:
                saveBook();
                finish();
                return true;
            case R.id.delete_book_option:
                showDeleteConfirmationWindow();
            case R.id.order_book_option:
                callSupplier();
                finish();
                return true;
            case R.id.homeAsUp:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.positive_message, discardButtonClickListener);
        builder.setNegativeButton(R.string.negative_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alert);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.dont_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_book_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookContract.BookEntry.PRODUCT_ID,
                BookContract.BookEntry.PRODUCT_NAME,
                BookContract.BookEntry.PRODUCT_PRICE,
                BookContract.BookEntry.PRODUCT_QUANTITY,
                BookContract.BookEntry.SUPPLIER_NAME,
                BookContract.BookEntry.SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER);

            String bookName = cursor.getString(nameColumnIndex);
            double bookPrice = cursor.getDouble(priceColumnIndex);
            int booksQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            mSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
            mNameEditText.setText(bookName);
            mPriceEditText.setText(Double.toString(bookPrice));
            mQuantityEditText.setText(Integer.toString(booksQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(mSupplierPhone);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");

    }

    public void callSupplier() {
        Intent callSupplier = new Intent(Intent.ACTION_DIAL);
        callSupplier.setData(Uri.parse("tel:" + mSupplierPhone));
        startActivity(callSupplier);
    }
}
