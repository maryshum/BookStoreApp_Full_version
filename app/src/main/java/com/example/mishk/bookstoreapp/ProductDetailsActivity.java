package com.example.mishk.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
//Implementation of buttons changing books quantity:
//https://stackoverflow.com/questions/51303831/how-to-use-edittext-user-input-increment-and-decrement-buttons-values-in-androi


public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    private Uri currentBookUri;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private String supplierPhone;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private Button decrementButton;
    private Button incrementButton;
    private boolean bookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        //Get data from Intent
        Intent intent = getIntent();
        currentBookUri = intent.getData();
        if (currentBookUri == null) {
            setTitle(R.string.details_activity_add_book);
        } else {
            setTitle(R.string.details_activity_edit_book);
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }
        nameEditText = findViewById(R.id.name);
        priceEditText = findViewById(R.id.price);
        quantityEditText = findViewById(R.id.quantity);
        supplierNameEditText = findViewById(R.id.supplier);
        supplierPhoneEditText = findViewById(R.id.supplier_number);
        nameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        decrementButton = findViewById(R.id.decrement_button);
        incrementButton = findViewById(R.id.increment_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int booksQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                booksQuantity++;
                ContentValues contentValues = new ContentValues();
                contentValues.put(BookContract.BookEntry.PRODUCT_QUANTITY, booksQuantity);
                int updateRow = v.getContext().getContentResolver().update(currentBookUri, contentValues, null, null);
            }
        });
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int booksQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                booksQuantity--;
                if (booksQuantity < 0) {
                    Toast.makeText(v.getContext(), getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookContract.BookEntry.PRODUCT_QUANTITY, booksQuantity);
                    int updateRow = v.getContext().getContentResolver().update(currentBookUri, contentValues, null, null);
                }
            }
        });
    }

    private void saveBook() {
        String bookName = nameEditText.getText().toString().trim();
        String bookPrice = priceEditText.getText().toString().trim();
        final String bookQuantity = quantityEditText.getText().toString().trim();
        String bookSupplier = supplierNameEditText.getText().toString().trim();
        supplierPhone = supplierPhoneEditText.getText().toString().trim();
        //Check if any of the field values are empty.
        //Show warning and stop saving if fields are missing.
        if (TextUtils.isEmpty(bookName) || TextUtils.isEmpty(bookPrice) ||
                TextUtils.isEmpty(bookQuantity) || TextUtils.isEmpty(bookSupplier) ||
                TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }
        //Otherwise, proceed with saving a book
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookEntry.PRODUCT_NAME, bookName);
        contentValues.put(BookContract.BookEntry.PRODUCT_PRICE, bookPrice);
        contentValues.put(BookContract.BookEntry.PRODUCT_QUANTITY, bookQuantity);
        contentValues.put(BookContract.BookEntry.SUPPLIER_NAME, bookSupplier);
        contentValues.put(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER, supplierPhone);
        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentBookUri, contentValues, null, null);
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
                break;
            case R.id.order_book_option:
                callSupplier();
                finish();
                return true;
            case R.id.homeAsUp:
                if (!bookHasChanged) {
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
        if (!bookHasChanged) {
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
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
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
        return new CursorLoader(this, currentBookUri, projection, null, null, null);
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
            supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            nameEditText.setText(bookName);
            priceEditText.setText(Double.toString(bookPrice));
            quantityEditText.setText(Integer.toString(booksQuantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");

    }

    public void callSupplier() {
        Intent callSupplier = new Intent(Intent.ACTION_DIAL);
        callSupplier.setData(Uri.parse("tel:" + supplierPhone));
        startActivity(callSupplier);
    }
}
