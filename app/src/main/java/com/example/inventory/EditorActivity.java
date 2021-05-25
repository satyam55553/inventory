package com.example.inventory;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.inventory.data.InventoryContract;
import com.example.inventory.data.InventoryDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    InventoryDbHelper mDbHelper;
    EditText eName, eQty, ePrice;
    ImageView eImage, noImage;
    Uri eCurrentUri;
    private static int RESULT_LOAD_IMAGE = 0, RESULT_CAMERA_IMAGE = 1;
    final String[] Options = {"Gallery", "Camera"};
    AlertDialog.Builder window;
    Bitmap bitmap;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);
        FloatingActionButton fabAdd = findViewById(R.id.fab_edit);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(hasImage(eImage)){
                        saveItem();
                        finish();
                }else{
                    Toast.makeText(EditorActivity.this, "Please add image", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        eName = (EditText) findViewById(R.id.edit_item_name);
        eQty = (EditText) findViewById(R.id.edit_item_qty);
        ePrice = (EditText) findViewById(R.id.edit_item_price);
        eImage = (ImageView) findViewById(R.id.edit_image_view);
        noImage = (ImageView) findViewById(R.id.noImageEdit);

        eImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        Intent i = getIntent();
        eCurrentUri = i.getData();

        if (eCurrentUri == null) {
            setTitle("Add Item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Item");
            if (hasImage(eImage)) {
                noImage.setVisibility(View.GONE);
            }
            getSupportLoaderManager().initLoader(2, null, this);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (hasImage(eImage)) {
            eImage.setImageBitmap(bitmap);
            noImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //getting image from gallery
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                eImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == RESULT_CAMERA_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                eImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_edit:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //hiding the delete options,while adding new pet
        if (eCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_edit);
            menuItem.setVisible(false);
        }
        return true;
    }
//    private void setupSpinner() {
//        // Create adapter for spinner. The list options are from the String array it will use
//        // the spinner will use the default layout
//        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.array_gender_options, android.R.layout.simple_spinner_item);
//
//        // Specify dropdown layout style - simple list view with 1 item per line
//        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//        // Apply the adapter to the spinner
//        mGenderSpinner.setAdapter(genderSpinnerAdapter);
//
//        // Set the integer mSelected to the constant values
//        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selection = (String) parent.getItemAtPosition(position);
//                if (!TextUtils.isEmpty(selection)) {
//                    if (selection.equals(getString(R.string.gender_male))) {
//                        mGender = PetsContract.PetEntry.GENDER_MALE; // Male
//                    } else if (selection.equals(getString(R.string.gender_female))) {
//                        mGender = PetsContract.PetEntry.GENDER_FEMALE; // Female
//                    } else {
//                        mGender = PetsContract.PetEntry.GENDER_OTHERS; // Unknown
//                    }
//                }
//            }
//
//            // Because AdapterView is an abstract class, onNothingSelected must be defined
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mGender = 0; // Unknown
//            }
//        });
//    }

    public void saveItem() {
        if (eCurrentUri == null) {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.Items.COLUMN_NAME, eName.getText().toString().trim());
            values.put(InventoryContract.Items.COLUMN_QUANTITY, eQty.getText().toString().trim());
            values.put(InventoryContract.Items.COLUMN_PRICE, ePrice.getText().toString().trim());
            if (hasImage(eImage)) {
                values.put(InventoryContract.Items.COLUMN_IMAGE, getBitmapAsByte(bitmap));
            }

            Uri newUri = getContentResolver().insert(InventoryContract.Items.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Row adding failed for" + newUri, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Row added successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(InventoryContract.Items.COLUMN_NAME, eName.getText().toString().trim());
            updatedValues.put(InventoryContract.Items.COLUMN_QUANTITY, eQty.getText().toString().trim());
            updatedValues.put(InventoryContract.Items.COLUMN_PRICE, ePrice.getText().toString().trim());
            if (hasImage(eImage)) {
                updatedValues.put(InventoryContract.Items.COLUMN_IMAGE, getBitmapAsByte(bitmap));
            }

            int rowsUpdated = 0;
            String selection = InventoryContract.Items.COLUMN_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(eCurrentUri))};
            rowsUpdated = getContentResolver().update(InventoryContract.Items.CONTENT_URI, updatedValues,
                    selection, selectionArgs);

            if (rowsUpdated == 0) {
                Toast.makeText(this, "Row Update Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Row Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDialog() {
        window = new AlertDialog.Builder(this);
        window.setTitle("Choose Image from ");
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent selectImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(selectImg, RESULT_LOAD_IMAGE);
                } else if (i == 1) {
                    Intent takeImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takeImg, RESULT_CAMERA_IMAGE);
                } else {
                    Toast.makeText(getApplicationContext(), "Error while selecting ", Toast.LENGTH_SHORT);
                }
            }
        });
        window.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //User clicked "Keep Editing" ,so dismiss the dialog and continue editing the pet
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                getContentResolver().delete(eCurrentUri, null, null);
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //        create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //convert Bitmap image to Byte[] array
    public static byte[] getBitmapAsByte(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
        return outputStream.toByteArray();
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }
        return hasImage;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryContract.Items.COLUMN_ID,
                InventoryContract.Items.COLUMN_NAME,
                InventoryContract.Items.COLUMN_QUANTITY,
                InventoryContract.Items.COLUMN_PRICE,
                InventoryContract.Items.COLUMN_IMAGE};
        //Return Current Row
        return new CursorLoader(this, eCurrentUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            //getting column id of each item from cursor
            int nameId = cursor.getColumnIndex(InventoryContract.Items.COLUMN_NAME);
            int qtyId = cursor.getColumnIndex(InventoryContract.Items.COLUMN_QUANTITY);
            int priceId = cursor.getColumnIndex(InventoryContract.Items.COLUMN_PRICE);
            int imageId = cursor.getColumnIndex(InventoryContract.Items.COLUMN_IMAGE);
            //getting each item from cursor pointed by the id
            String vName = cursor.getString(nameId);
            int vQty = cursor.getInt(qtyId);
            int vPrice = cursor.getInt(priceId);
            byte[] imgByte = cursor.getBlob(imageId);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            eImage.setImageBitmap(imgBitmap);
            noImage.setVisibility(View.GONE);
            eName.setText(vName);
            eQty.setText(String.valueOf(vQty));
            ePrice.setText(String.valueOf(vPrice));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        eName.setText("");
        eQty.setText(null);
        ePrice.setText(null);
        eImage.setImageDrawable(null);
    }
}
