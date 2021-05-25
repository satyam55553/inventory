package com.example.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//This class contains all constants required for our database
public final class InventoryContract {
    InventoryContract() {
    }

    //content uri syntax content://authority/path/id
    public static final String CONTENT_AUTHORITY = "com.example.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    //Table in our database
    public static final class Items implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        //MIME Type,used by getType() of Content Provider
        public  static  final  String CONTENT_LIST_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+PATH_ITEMS;
        public  static  final  String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+PATH_ITEMS;

        public static final String TABLE_NAME = "items";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IMAGE = "image";

        public static final int FOOD = 1;
        public static final int UTILITIES = 2;
        public static final int OTHERS = 0;
    }
}
