package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class InventoryActivity extends AppCompatActivity {

    private BookDbHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        DbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayDatabaseInfo() {

        SQLiteDatabase db = DbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        TextView dV = findViewById(R.id.text_view_inventory);

        try (Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null)) {
            dV.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            dV.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_PRICE + " - " +
                    BookEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + "\n");

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneNumberColumnIndex);

                dV.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone));
            }
        }
    }

    private void insertBook() {
        SQLiteDatabase db = DbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, "The Great Gatsby");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, 19.99);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 1);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Scholastic");
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "1-800-123-BOOK");

        db.insert(BookEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}