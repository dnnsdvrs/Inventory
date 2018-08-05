package com.example.android.p7_1_inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.p7_1_inventory.data.BooksContract;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data to the given list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView bookTitle = view.findViewById(R.id.text_book_title);
        TextView bookAuthor = view.findViewById(R.id.text_book_writer);
        final TextView bookQuantity = view.findViewById(R.id.text_instock_number);
        TextView bookPrice = view.findViewById(R.id.text_price_number);

        //Find the volumes of Bookentries we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_AUTHOR);
        int quantityColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_PRICE);

        // Extract properties from cursor
        String name = cursor.getString(titleColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        int price = cursor.getInt(priceColumnIndex);

        // Populate fields with extracted properties
        bookTitle.setText(name);
        bookAuthor.setText(author);
        bookQuantity.setText(String.valueOf(quantity));
        bookPrice.setText(String.valueOf(price));

        //Add the sale button to the listview.
        final Button orderButton = view.findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int QuantityAfterSale = Integer.parseInt(bookQuantity.getText().toString().trim());
                if (QuantityAfterSale > 0) {
                    bookQuantity.setText(String.valueOf(QuantityAfterSale - 1));
                    Toast.makeText(context, R.string.toast_book_sold,
                            Toast.LENGTH_SHORT).show();
                }
                if (QuantityAfterSale == 0) {
                    orderButton.setEnabled(false);
                    Toast.makeText(context, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
                }
                ContentValues values = new ContentValues();
                values.put(BooksContract.BooksEntry.COLUMN_BOOK_QUANTITY, QuantityAfterSale);
            }
        });
    }
}

