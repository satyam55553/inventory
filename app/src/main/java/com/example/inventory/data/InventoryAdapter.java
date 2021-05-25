package com.example.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventory.R;

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.item_name_list);
        TextView qty = view.findViewById(R.id.item_qty_list);
        TextView price = view.findViewById(R.id.item_price_list);

        name.setText(cursor.getString((cursor.getColumnIndexOrThrow(InventoryContract.Items.COLUMN_NAME))));
        qty.setText(String.valueOf(cursor.getInt((cursor.getColumnIndexOrThrow(InventoryContract.Items.COLUMN_QUANTITY)))));
        price.setText(String.valueOf(cursor.getInt((cursor.getColumnIndexOrThrow(InventoryContract.Items.COLUMN_PRICE)))));
    }
}
