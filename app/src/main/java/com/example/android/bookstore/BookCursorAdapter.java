package com.example.android.bookstore;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {
        super( context, cursor, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in the list_item.xml
        return LayoutInflater.from( context ).inflate( R.layout.list_item, parent, false );
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById( R.id.product_name );
        TextView priceTextView = view.findViewById( R.id.price );
        final TextView quantityTextView = view.findViewById( R.id.quantity );
        Button button = view.findViewById( R.id.sale_button );

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex( BookEntry._ID );
        int nameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_NAME );
        int priceColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_PRICE );
        final int quantityColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_PRODUCT_QUANTITY );

        // Read the pet attributes from the Cursor for the current book
        final String id = cursor.getString( idColumnIndex );
        String bookName = cursor.getString( nameColumnIndex );
        Double bookPrice = cursor.getDouble( priceColumnIndex );
        final Integer quantityBook = cursor.getInt( quantityColumnIndex );

        // Attach a "$" to the String bookPrice
        String bookPriceFinal = "$" + bookPrice;

        // Update the TextViews with the attributes for the current book
        nameTextView.setText( bookName );
        priceTextView.setText( bookPriceFinal );
        quantityTextView.setText( String.valueOf( quantityBook ) );

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityBook == 0) {
                    Toast.makeText( view.getContext(), R.string.book_sold_out, Toast.LENGTH_SHORT ).show();
                    return;
                } else {
                    InventoryActivity inventoryActivity = (InventoryActivity) context;
                    inventoryActivity.decreaseQuantity( Integer.valueOf( id ), Integer.valueOf( quantityBook ) );
                }
            }
        } );
    }
}
