package no.fjeld.feed;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "drawerItemsManager";
    private static final String TABLE_DRAWER_ITEMS = "drawerItems";

    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_ENCODING = "encoding";

    public DBManager(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    } 

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_DRAWER_ITEMS + "("
                + KEY_NAME + " TEXT PRIMARY KEY, "
                + KEY_URL + " TEXT,"
                + KEY_ENCODING + " TEXT" 
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWER_ITEMS);
        onCreate(db);

    }

    /**
     * Adds a DrawerItem to the database.
     *
     * @param item The DrawerItem-object to get the info from.
     */
    public void addItem(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getFeedName());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_ENCODING, item.getEncoding());

        db.insert(TABLE_DRAWER_ITEMS, null, values);
        db.close();

    }

    /**
     * Deletes a DrawerItem from the database.
     *
     * @param item the DrawerItem-object to delete.
     */
    public void deleteItem(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DRAWER_ITEMS, KEY_NAME + " = ?", new String [] {item.getFeedName()});
        db.close();

    }

    /**
     * Returns an ArrayList with DrawerItem-objects.
     * All params for the DrawerItem-objects are from
     * the db except the list with FeedItems.
     *
     * @return mDrawerItems A list with DrawerItems-objects.
     */
    public ArrayList <DrawerItem> getItems() {

        ArrayList <DrawerItem> mDrawerItems = new ArrayList <DrawerItem> ();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT *"
                + " FROM " + TABLE_DRAWER_ITEMS
                + " ORDER BY " + KEY_NAME + " COLLATE NOCASE", null);

        if (cursor.moveToFirst()) {
            do {

                mDrawerItems.add(new DrawerItem(cursor.getString(0), 
                            cursor.getString(1), cursor.getString(2), 
                            new ArrayList <FeedItem> ()));

            } while (cursor.moveToNext()); 
        }

        db.close();
        return mDrawerItems;

    }

}
