package com.example.android.p7_1_inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.p7_1_inventory.data.BooksContract.BooksEntry;

public class NewBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    //Edittexts, spinner and spinnervalue in NewBookActivity
    private EditText mTitleEditText;
    private EditText mAuthorEdiText;
    private Spinner mGenreSpinner;
    private int mGenre;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    //Set a boolean and touchListeners to check if user has changed anything in the edit fields
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
        setContentView(R.layout.newbook_activity);
        //See if we're creating a book or editing an existing one
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            // If the Uri is null, it's a new book. Change app bar and hide 'delete' button
            setTitle("Add a book");
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar and initize loader to read db
            setTitle("Edit book");
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = findViewById(R.id.edittext_title);
        mAuthorEdiText = findViewById(R.id.edittext_author);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mPriceEditText = findViewById(R.id.edittext_price);
        mQuantityEditText = findViewById(R.id.edittext_quantity);
        mSupplierNameEditText = findViewById(R.id.edittext_supl_name);
        mSupplierPhoneEditText = findViewById(R.id.edittext_supl_phone);

        setupSpinner();

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEdiText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        //Orderbutton fires an intent to dial the number provided in the database
        Button orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String suplPhoneString = mSupplierPhoneEditText.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                if (suplPhoneString == null || TextUtils.isEmpty(suplPhoneString)) {
                    //If there is no number provided, only display a toast
                    Toast.makeText(getApplicationContext(), R.string.toast_no_phonenmbr, Toast.LENGTH_SHORT).show();
                } else {
                    callIntent.setData(Uri.parse("tel:" + suplPhoneString));
                    startActivity(callIntent);
                }
            }
        });

        // Setup quantity decrease button and clicks
        findViewById(R.id.button_qty_minus_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString) || quantityString == null) {
                    quantityString = String.valueOf(0);
                }
                int quantityInt = Integer.parseInt(quantityString);
                if (quantityInt == 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_already_out_stock, Toast.LENGTH_SHORT).show();
                } else {
                    quantityInt--;
                    mQuantityEditText.setText(String.valueOf(quantityInt));
                }
            }
        });

        // Setup quantity increase button and clicks
        findViewById(R.id.button_qty_plus_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString) || quantityString == null) {
                    quantityString = String.valueOf(0);
                }
                int quantityInt = Integer.parseInt(quantityString);
                quantityInt++;
                mQuantityEditText.setText(String.valueOf(quantityInt));
            }
        });

    }

    /**
     * Setup the dropdown spinner that allows the user to select the genre of the book
     */
    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenreSpinner.setAdapter(genderSpinnerAdapter);
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_fiction_string))) {
                        mGenre = BooksEntry.GENRE_FICTION; // Bookgenre is fiction
                    } else if (selection.equals(getString(R.string.genre_nonfiction_string))) {
                        mGenre = BooksEntry.GENRE_NONFICTION; // Bookgenre is non-fiction
                    } else {
                        mGenre = BooksEntry.GENRE_UNKNOWN; // Booksgenre is unknown
                    }
                }
            }

            // If nothing is selected, the genre will be assumed unknown
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = 0;
            }
        });
    }

    /**
     * Create or update a book
     **/

    private void saveBook() {

        //Store the user's entry in variables
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEdiText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString();
        String quantityString = mQuantityEditText.getText().toString();
        String suplNameString = mSupplierNameEditText.getText().toString();
        String suplPhoneString = mSupplierPhoneEditText.getText().toString();

        //Create a ContentValue object where columns are keys
        //and the entries from the user are the values
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BooksEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BooksEntry.COLUMN_BOOK_GENRE, mGenre);
        values.put(BooksEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BooksEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BooksEntry.COLUMN_BOOK_SUPL_NAME, suplNameString);
        values.put(BooksEntry.COLUMN_BOOK_SUPL_PHONE, suplPhoneString);

        if (mCurrentBookUri == null) {
            // If required fields are emtpy when a new book is saved, stay in activity and display a toast.
            if (TextUtils.isEmpty(titleString) ||
                    TextUtils.isEmpty(authorString) ||
                    mGenre == BooksEntry.GENRE_UNKNOWN ||
                    TextUtils.isEmpty(priceString) ||
                    TextUtils.isEmpty(quantityString) ||
                    TextUtils.isEmpty(suplNameString) ||
                    TextUtils.isEmpty(suplPhoneString)) {
                Toast.makeText(this, "Please fill in all the fields",
                        Toast.LENGTH_SHORT).show();
            } else {
                //Insert a new book
                Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);

                if (newUri != null) {
                    Toast.makeText(this, R.string.toast_saved_success,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.toast_saved_error, Toast.LENGTH_LONG).show();
                }
                finish();

            }

        } else {
            //when saving an existing book, but not all fields are filled in, stay in activity and display a toast.
            if (TextUtils.isEmpty(titleString) ||
                    TextUtils.isEmpty(authorString) ||
                    mGenre == BooksEntry.GENRE_UNKNOWN ||
                    TextUtils.isEmpty(priceString) ||
                    TextUtils.isEmpty(quantityString) ||
                    TextUtils.isEmpty(suplNameString) ||
                    TextUtils.isEmpty(suplPhoneString)) {
                Toast.makeText(this, "Please fill in all the fields",
                        Toast.LENGTH_SHORT).show();
            } else {
                //Edit an existing book
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, R.string.toast_update_fail,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.toast_update_success,
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        }
    }

    /**
     * Delete a book
     */
    private void deleteBook() {
        // Only perform the delete if it is an existing book
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.toast_delete_fail,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_delete_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Inflate and add the new_book_menu save button in the actionbar
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_book_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hide the deletebutton in 'Add a book'.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Add functionality to the save button
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Save book to database
            case R.id.action_save:
                saveBook();
                return true;
            // Delete book from database
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Up arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(NewBookActivity.this);
                    return true;
                }
                //If there are unsaved changes, notify the user and present dialog box
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(NewBookActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that contains all columns to be shown in NewBookActivity
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_BOOK_TITLE,
                BooksEntry.COLUMN_BOOK_AUTHOR,
                BooksEntry.COLUMN_BOOK_GENRE,
                BooksEntry.COLUMN_BOOK_PRICE,
                BooksEntry.COLUMN_BOOK_QUANTITY,
                BooksEntry.COLUMN_BOOK_SUPL_NAME,
                BooksEntry.COLUMN_BOOK_SUPL_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of attributes
            int titleColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_AUTHOR);
            int genreColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_QUANTITY);
            int suplNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_SUPL_NAME);
            int suplPhoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_SUPL_PHONE);

            // Extract out the value from the Cursor
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String suplName = cursor.getString(suplNameColumnIndex);
            String suplPhone = cursor.getString(suplPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mAuthorEdiText.setText(author);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(suplName);
            mSupplierPhoneEditText.setText(suplPhone);

            // Map the constant value from the database to dropdown options in Genre-spinner
            switch (genre) {
                case BooksEntry.GENRE_FICTION:
                    mGenreSpinner.setSelection(1);
                    break;
                case BooksEntry.GENRE_NONFICTION:
                    mGenreSpinner.setSelection(2);
                    break;
                default:
                    mGenreSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mAuthorEdiText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mGenreSpinner.setSelection(0); // Select "Unknown" genre
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}