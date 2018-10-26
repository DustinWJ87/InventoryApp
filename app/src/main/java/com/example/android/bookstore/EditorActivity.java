package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

/**
 * Allow user to create a new book or edit an existing one or just view full product details
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * EditText field to enter the book's name
     */
    private EditText nameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText priceEditText;

    /**
     * EditText field to enter the quantity of the book
     */
    private EditText quantityEditText;

    /**
     * EditText field to enter the book's supplier
     */
    private EditText supplierEditText;

    /**
     * EditText field to enter the book's supplier's phone #
     */
    private EditText supplierPhoneEditText;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentBookUri;

    private boolean bookHasChanged = false;

    private boolean bookIsInvalid = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    /**
     * OnClickListener for the buttons in the activity_editor.xml file
     * onClick sends to buttonPressed method case for each button.
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonPressed( view );
        }
    };

    BookCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editor );

        cursorAdapter = new BookCursorAdapter( this, null );

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (currentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle( getString( R.string.editor_activity_title_new_book ) );

            // Invalidate the options menu, so the "Delete" menu option can be hidden
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change the app bar to say "Edit Book"
            setTitle( getString( R.string.editor_activity_title_existing_book ) );

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader( EXISTING_BOOK_LOADER, null, this );
        }

        // Find all the relevant views that we will need to read user input from
        nameEditText = findViewById( R.id.edit_product_name );
        priceEditText = findViewById( R.id.edit_price );
        quantityEditText = findViewById( R.id.edit_quantity );
        supplierEditText = findViewById( R.id.edit_supplier_name );
        supplierPhoneEditText = findViewById( R.id.edit_supplier_phone_number );

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener( touchListener );
        priceEditText.setOnTouchListener( touchListener );
        quantityEditText.setOnTouchListener( touchListener );
        supplierEditText.setOnTouchListener( touchListener );
        supplierPhoneEditText.setOnTouchListener( touchListener );

        // Setup onClickListeners on the buttons and ImageButtons within the layout.
        ImageButton incrementButton = findViewById( R.id.increment_quantity_button );
        incrementButton.setOnClickListener( onClickListener );
        ImageButton decrementButton = findViewById( R.id.decrement_quantity_button );
        decrementButton.setOnClickListener( onClickListener );
        Button contactButton = findViewById( R.id.contact_supplier );
        contactButton.setOnClickListener( onClickListener );

        // Set the quantity to "1" if there is no value given
        if (currentBookUri == null) {
            quantityEditText.setText( "1" );
        }
    }

    /**
     * Method declaring what will happen when each button within the layout is pressed.
     */
    public void buttonPressed(View view) {
        switch (view.getId()) {
            case R.id.increment_quantity_button:
                // If the quantity is empty set to "1"
                if (quantityEditText.length() == 0) {
                    quantityEditText.setText( "1" );
                } else {
                    // Otherwise Take given value of quantity add 1 to it and display new value
                    Integer quantity = Integer.valueOf( quantityEditText.getText().toString().trim() );
                    String incrementQuantity = String.valueOf( ++quantity );
                    quantityEditText.setText( incrementQuantity );
                }
                break;
            case R.id.decrement_quantity_button:
                // If the quantity is empty set to "0"
                if (quantityEditText.length() == 0) {
                    quantityEditText.setText( "0" );
                }
                // Grab the integer value of the quantityEditText field
                Integer quantity = Integer.valueOf( quantityEditText.getText().toString().trim() );
                // If the quantity is at "0" display a toast message saying " Sold Out "
                if (quantity == 0) {
                    Toast.makeText( this, R.string.book_sold_out, Toast.LENGTH_SHORT ).show();
                } else {
                    // Otherwise take the value of quantity subtract 1 and display the new value
                    String decrementQuantity = String.valueOf( --quantity );
                    quantityEditText.setText( decrementQuantity );
                }
                break;
            case R.id.contact_supplier:
                String phoneNumber = supplierPhoneEditText.getText().toString().trim();
                // If the phone # is 10 digits long, create an intent for a phone call
                // and have the user choose an app to handle the call intent.
                if (phoneNumber.length() == 10) {
                    Intent intent = new Intent( Intent.ACTION_DIAL );
                    intent.setData( Uri.parse( "tel:" + phoneNumber ) );
                    startActivity( intent );
                } else {
                    // Otherwise display a toast message saying invalid phone number.
                    Toast.makeText( this, getString( R.string.editor_activity_invalid_phone_number ),
                            Toast.LENGTH_SHORT ).show();
                }
                break;
            default:
                break;
        }
    }

    private void saveBook() {
        // Read from the input fields, Use trim to eliminate leading or trailing white space.
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check is all the fields in the editor are blank
        if (currentBookUri == null &&
                TextUtils.isEmpty( nameString ) &&
                TextUtils.isEmpty( priceString ) &&
                TextUtils.isEmpty( quantityString ) &&
                TextUtils.isEmpty( supplierNameString ) &&
                TextUtils.isEmpty( supplierPhoneString )) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider Operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();

        // Validate that all the fields have been filled in appropriately
        if (TextUtils.isEmpty( nameString ) || TextUtils.isEmpty( priceString ) ||
                TextUtils.isEmpty( quantityString ) || TextUtils.isEmpty( supplierNameString ) ||
                TextUtils.isEmpty( supplierPhoneString ) || Double.valueOf( priceString ) < 0 ||
                Long.valueOf( supplierPhoneString ) < 0 || supplierPhoneString.length() != 10) {
            // Tag book as Invalid to make sure the correct toast appears and user cannot exit.
            bookIsInvalid = true;
        } else {
            contentValues.put( BookEntry.COLUMN_PRODUCT_NAME, nameString );
            contentValues.put( BookEntry.COLUMN_PRODUCT_PRICE, priceString );
            contentValues.put( BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString );
            contentValues.put( BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString );
            contentValues.put( BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString );

            // Set book as Valid so it will continue with saving process and take user to inventory.
            bookIsInvalid = false;

            // Determine is this is a new or existing book by checking id currentBookUri is null or not
            if (currentBookUri == null) {
                // This is a NEW BOOK, so insert a new book into the provider,
                // returning the content URI for the new book.
                Uri newUri = getContentResolver().insert( BookEntry.CONTENT_URI, contentValues );

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText( this, R.string.editor_activity_insert_book_failed,
                            Toast.LENGTH_SHORT ).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText( this, R.string.editor_activity_insert_book_successful,
                            Toast.LENGTH_SHORT ).show();
                }
            } else {
                // Otherwise this is an EXISTING BOOK, so update the book with content URI: currentBookUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because currentBookUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update( currentBookUri, contentValues, null, null );

                // Show a toast message depending on whether or not the update was successful
                if (rowsAffected == 0) {
                    // If the rowsAffected equals 0 then there was an error with the update
                    Toast.makeText( this, R.string.editor_activity_update_book_failed,
                            Toast.LENGTH_SHORT ).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast
                    Toast.makeText( this, R.string.editor_activity_update_book_successful,
                            Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu.menu_editor.xml file.
        getMenuInflater().inflate( R.menu.menu_editor, menu );
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that that menu can be updated.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu( menu );
        // If this is a new book, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem( R.id.action_delete );
            menuItem.setVisible( false );
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save a book to the database
                saveBook();
                if (bookIsInvalid) {
                    showIncompleteFieldsDialog();
                } else {
                    // Exit the activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" button in the app bar
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to the parent activity
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask( EditorActivity.this );
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discard.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask( EditorActivity.this );
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog( discardButtonClickListener );
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * This method is called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discard.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog( discardButtonClickListener );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all the columns from the books table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader( this,    // Parent Activity context
                currentBookUri,                  // Query for the content URI for the current book
                projection,                      // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null );                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_NAME );
            int priceColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_QUANTITY );
            int supplierNameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME );
            int supplierPhoneColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER );

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString( nameColumnIndex );
            double price = cursor.getDouble( priceColumnIndex );
            int quantity = cursor.getInt( quantityColumnIndex );
            String supplierName = cursor.getString( supplierNameColumnIndex );
            String supplierPhone = cursor.getString( supplierPhoneColumnIndex );

            // Update the views on the screen with the values from the database
            nameEditText.setText( name );
            priceEditText.setText( Double.toString( price ) );
            quantityEditText.setText( Integer.toString( quantity ) );
            supplierEditText.setText( supplierName );
            supplierPhoneEditText.setText( supplierPhone );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText( "" );
        priceEditText.setText( "" );
        quantityEditText.setText( "" );
        supplierEditText.setText( "" );
        supplierPhoneEditText.setText( "" );
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when the
     *                                   user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.unsaved_changes_dialog_message );
        builder.setPositiveButton( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton( R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked the "Keep Editing" button, so dismiss the dialog and continue editing
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        } );

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.delete_dialog_message );
        builder.setPositiveButton( R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        } );
        builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        } );

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user that they must complete all fields before saving the book.
     */
    private void showIncompleteFieldsDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.editor_activity_empty_fields_message );
        builder.setNegativeButton( R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked the "Keep Editing" button, so dismiss the dialog and continue editing
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        } );

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the currentBookUri
            // content URI already identifies the book we want.
            int rowsDeleted = getContentResolver().delete( currentBookUri, null, null );

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText( this, R.string.editor_activity_delete_book_failed,
                        Toast.LENGTH_SHORT ).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText( this, R.string.editor_activity_delete_book_successful,
                        Toast.LENGTH_SHORT ).show();
            }
        }

        // Close the activity
        finish();
    }
}
