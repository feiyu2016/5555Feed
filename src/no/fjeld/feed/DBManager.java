package no.fjeld.feed;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "feedDatabase";
    private static final int DATABASE_VERSION = 1;

    /* Values for the DrawerItems */
    private static final String TABLE_DRAWER_ITEMS = "drawerItems";
    private static final String KEY_DRAWER_TITLE = "title";
    private static final String KEY_DRAWER_URL = "url";
    private static final String KEY_ENCODING = "encoding";

    /* Values for saved items */
    private static final String TABLE_SAVED_ITEMS = "savedItems";
    private static final String KEY_SAVED_TITLE = "title";
    private static final String KEY_SAVED_URL = "url";

    /* Values for read items */
    private static final String TABLE_READ_ITEMS = "readItems";
    private static final String KEY_READ_URL = "url";


    public DBManager(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    } 

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_DRAWER_ITEMS + "("
                + KEY_DRAWER_TITLE + " TEXT, "
                + KEY_DRAWER_URL + " TEXT PRIMARY KEY,"
                + KEY_ENCODING + " TEXT" 
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_SAVED_ITEMS + "("
                + KEY_SAVED_TITLE + " TEXT, "
                + KEY_SAVED_URL + " TEXT PRIMARY KEY"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_READ_ITEMS + "("
                + KEY_READ_URL + " TEXT PRIMARY KEY"
                + ")"); 



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READ_ITEMS);

        onCreate(db);

    }

    /**
     * Adds a DrawerItem to the database.
     *
     * @param item The DrawerItem-object to get the info from.
     */
    public void addDrawerItem(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DRAWER_TITLE, item.getFeedName());
        values.put(KEY_DRAWER_URL, item.getUrl());
        values.put(KEY_ENCODING, item.getEncoding());

        db.insert(TABLE_DRAWER_ITEMS, null, values);
        db.close();

    }

    /**
     * Deletes a DrawerItem from the database.
     *
     * @param item the DrawerItem-object to delete.
     */
    public void deleteDrawerItem(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DRAWER_ITEMS, KEY_DRAWER_URL + " = ?", new String [] {item.getUrl()});
        db.close();

    }

    /**
     * Returns an ArrayList with DrawerItem-objects.
     *
     * @return mDrawerItems A list with DrawerItems-objects.
     */
    public ArrayList <DrawerItem> getDrawerItems() {

        ArrayList <DrawerItem> mDrawerItems = new ArrayList <DrawerItem> ();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT *"
                + " FROM " + TABLE_DRAWER_ITEMS
                + " ORDER BY " + KEY_DRAWER_TITLE + " COLLATE NOCASE", null);

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

    public void addSavedItem(FeedItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SAVED_TITLE, item.getTitle());
        values.put(KEY_SAVED_URL, item.getUrl());

        db.insert(TABLE_SAVED_ITEMS, null, values);
        db.close();
   
    }

    public void deleteSavedItem(FeedItem item) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SAVED_ITEMS, KEY_SAVED_URL + " = ?", new String [] {item.getUrl()});
        db.close();

    }

    public ArrayList <FeedItem> getSavedItems() {

    }

    public void addReadItem(FeedItem item) {

    }

    public void deleteReadItem(FeedItem item) {

    }

    public ArrayList <FeedItem> getReadItems() {

    }

}
