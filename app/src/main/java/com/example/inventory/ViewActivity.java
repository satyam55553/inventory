package com.example.inventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventory.data.InventoryContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri currentUri;
    TextView name, qty, price;
    ImageView imageView,noImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view);
            //Getting Uri data from MainActivity
            Intent intent = getIntent();
            currentUri = intent.getData();

            name = findViewById(R.id.item_name_view);
            qty = findViewById(R.id.item_qty_view);
            price = findViewById(R.id.item_price_view);
            imageView = findViewById(R.id.item_image_view);
            noImage=(ImageView) findViewById(R.id.noImageView);

            FloatingActionButton fabEdit = findViewById(R.id.fab_edit);
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewActivity.this, EditorActivity.class);
                    i.setData(currentUri);
                    startActivity(i);
                }
            });
            getSupportLoaderManager().initLoader(1, null, this);
        } catch (Exception e) {
            Toast.makeText(ViewActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_view:
                Intent i = new Intent(ViewActivity.this, EditorActivity.class);
                i.setData(currentUri);
                startActivity(i);
                return true;
            case R.id.delete_view:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                getContentResolver().delete(currentUri, null, null);
                Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //        create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean hasImage(@NonNull ImageView view){
        Drawable drawable=view.getDrawable();
        boolean hasImage=(drawable!=null);
        if(hasImage && (drawable instanceof BitmapDrawable)){
            hasImage=((BitmapDrawable)drawable).getBitmap()!=null;
        }
        return  hasImage;
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
        return new CursorLoader(this, currentUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
//Setting the TextViews using Cursor data
        //Ensure that your cursor points to the first(only) row
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

            if(hasImage(imageView)) {
                byte[] imgByte = cursor.getBlob(imageId);
                if (imgByte != null) {
                    //Byte[] to Bitmap Image
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    imageView.setImageBitmap(imgBitmap);
                    noImage.setVisibility(View.GONE);
                }
            }

            name.setText(vName);
            qty.setText(String.valueOf(vQty));
            price.setText(String.valueOf(vPrice));

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//Clear all textviews
        name.setText("");
        qty.setText(null);
        price.setText(null);
    }
}
