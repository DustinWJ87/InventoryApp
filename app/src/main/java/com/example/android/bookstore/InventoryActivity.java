package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bookstore.data.BookContract.BookEntry;

/**
 * Displays list of books that were entered and stored in the app
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    BookCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_inventory );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( InventoryActivity.this, EditorActivity.class );
                startActivity( intent );
            }
        } );

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById( R.id.text_view_inventory );
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        // View emptyView = findViewById(R.id.empty_view); [IMPLEMENT THIS with an image for empty]
        // bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet(until the loader finishes) so pass in null for the Cursor.
        cursorAdapter = new BookCursorAdapter( this, null );
        bookListView.setAdapter( cursorAdapter );

        // Setup item click listener
        bookListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent( InventoryActivity.this, EditorActivity.class );

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the {@link BookEntry#CONTENT_URI}.
                Uri currentBookUri = ContentUris.withAppendedId( BookEntry.CONTENT_URI, id );

                // Set the URI on the data field of the intent
                intent.setData( currentBookUri );

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity( intent );
            }
        } );

        // Kick off the loader
        getLoaderManager().initLoader( BOOK_LOADER, null, this );
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and The Great Gatsby book attributes are the values.
        ContentValues values = new ContentValues();

        values.put( BookEntry.COLUMN_PRODUCT_NAME, "The Great Gatsby" );
        values.put( BookEntry.COLUMN_PRODUCT_PRICE, 19.99 );
        values.put( BookEntry.COLUMN_PRODUCT_QUANTITY, 1 );
        values.put( BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Scholastic" );
        values.put( BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "8001234567" );

        // Insert a new row for The Great Gatsby into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access The Great Gatsby's data in the future.
        getContentResolver().insert( BookEntry.CONTENT_URI, values );
    }

    /**
     * Helper method to delete all books in the database
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete( BookEntry.CONTENT_URI, null, null );
        Log.v( "InventoryActivity", rowsDeleted + " rows deleted from book database" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_inventory.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_inventory, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu options
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all books" menu option
            case R.id.action_delete_all_entries:
                // Delete all books
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY};

        // This Loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader( this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        cursorAdapter.swapCursor( cursor );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        cursorAdapter.swapCursor( null );
    }

    public void decreaseQuantity(int columnId, int quantity){
        quantity = quantity - 1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, columnId);

        getContentResolver().update(updateUri, contentValues, null, null);
    }

}