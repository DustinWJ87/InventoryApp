package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

        Cursor c = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        TextView dV = findViewById(R.id.text_view_inventory);

        try {
            dV.setText("The books table contains " + c.getCount() + " books.\n\n");
            dV.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_PRICE + " - " +
                    BookEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + "\n");

            int idColumnIndex = c.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = c.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while (c.moveToNext()) {
                int currentID = c.getInt(idColumnIndex);
                String currentName = c.getString(nameColumnIndex);
                double currentPrice = c.getDouble(priceColumnIndex);
                int currentQuantity = c.getInt(quantityColumnIndex);
                String currentSupplier = c.getString(supplierNameColumnIndex);
                String currentSupplierPhone = c.getString(supplierPhoneNumberColumnIndex);

                dV.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone));
            }
        } finally {
            c.close();
        }
    }

    private void insertBook() {
        SQLiteDatabase db = DbHelper.getWritableDatabase();

        ContentValues v = new ContentValues();

        v.put(BookEntry.COLUMN_PRODUCT_NAME, "The Great Gatsby");
        v.put(BookEntry.COLUMN_PRODUCT_PRICE, 19.99);
        v.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 1);
        v.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Scholastic");
        v.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "1-800-123-BOOK");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, v);
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