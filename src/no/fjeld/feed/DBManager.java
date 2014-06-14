package no.fjeld.feed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.*;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "feedDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DRAWER_ITEMS = "drawerItems";
    private static final String TABLE_SAVED_ITEMS = "savedItems";
    private static final String TABLE_READ_ITEMS = "readItems";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_URL= "url";
    private static final String KEY_ENCODING = "encoding";

    /**
     * Constructor for the class DBManager.
     *
     * @param context The app-context.
     */
    public DBManager(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    } 

    /**
     * Creates all the tables for the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_DRAWER_ITEMS + "("
                + KEY_TITLE + " TEXT, "
                + KEY_URL + " TEXT PRIMARY KEY,"
                + KEY_ENCODING + " TEXT" 
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_SAVED_ITEMS + "("
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_URL + " TEXT PRIMARY KEY"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_READ_ITEMS + "("
                + KEY_URL + " TEXT PRIMARY KEY"
                + ")"); 

    }

    //TODO Needs functionality for updating the tables when the database changes.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READ_ITEMS);

        onCreate(db);

    }

    /**
     * Deletes a row from a table if an url is provided
     * and matched. If not, the whole table is cleared.
     *
     * @param table The table to delete from.
     * @param url   The url-value in the row to delete.
     */
    public void delete(String table, String url) {

        SQLiteDatabase db = getWritableDatabase();

        if (url != null)
            db.delete(table, KEY_URL + " = ?", new String [] {url});
        else
            db.delete(table, null, null);

        db.close();

    }

    /**
     * Updates the values for a DrawerItem.
     *
     * @param item The DrawerItem with the new values.
     */
    public void update(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getFeedName());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_ENCODING, item.getEncoding());

        db.update(TABLE_DRAWER_ITEMS, values, KEY_URL + " = ?",
                new String [] { item.getUrl() });

        db.close();

    }

    /**
     * Adds a new DrawerItem to the database.
     *
     * @param item The new DrawerItem
     */
    public void add(DrawerItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getFeedName());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_ENCODING, item.getEncoding());

        db.insert(TABLE_DRAWER_ITEMS, null, values);
        db.close();

    }

    /**
     * Adds a new FeedItem to the list of saved articles.
     *
     * @param item The FeedItem with the values to save.
     */
    public void add(FeedItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_URL, item.getUrl());

        db.insert(TABLE_SAVED_ITEMS, null, values);
        db.close();

    }

    /**
     * Adds an url to the list of read articles.
     *
     * @param url Url of the item that is marked as read.
     */
    public void add(String url) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_URL, url); 

        db.insert(TABLE_READ_ITEMS, null, values);
        db.close();

    }

    /**
     * Adds a list of urls to the list of read articles.
     *
     * @param list The list which contains the articles.
     */
    public void add(ArrayList <FeedItem> list) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        for (FeedItem item : list) {
            values.put(KEY_URL, item.getUrl());
            db.insert(TABLE_READ_ITEMS, null, values);
        }

        db.close();

    }

    /**
     * Returns a list of all items in the NavigationDrawer.
     *
     * @return drawerItems The list with DrawerItems.
     */
    public ArrayList <DrawerItem> getDrawerItems() {

        ArrayList <DrawerItem> drawerItems = new ArrayList <DrawerItem> ();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_DRAWER_ITEMS, 
                null, null, null, null, null, 
                KEY_TITLE + " COLLATE NOCASE", null);

        if (cursor.moveToFirst()) {
            do {

                drawerItems.add(new DrawerItem(cursor.getString(0), 
                            cursor.getString(1), cursor.getString(2), 
                            new ArrayList <FeedItem> ()));

            } while (cursor.moveToNext()); 
        }

        db.close();
        return drawerItems;

    }

    /**
     * Returns a list of all saved articles.
     *
     * @return savedItems The list with the saved FeedItems.
     */
    public ArrayList <FeedItem> getSavedItems() {

        ArrayList <FeedItem> savedItems = new ArrayList <FeedItem> ();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_SAVED_ITEMS,
                null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                savedItems.add(new FeedItem(cursor.getString(0), cursor.getString(1), 
                            cursor.getString(2), null, null, null));

            } while(cursor.moveToNext());
        }

        db.close();

        Collections.reverse(savedItems);
        return savedItems;

    }

    /**
     * Returns a list with the urls of read articles. 
     *
     * @return readItems The list with the urls.
     */
    public ArrayList <String> getReadItems() {

        ArrayList <String> readItems = new ArrayList <String> ();
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_READ_ITEMS,
                null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                readItems.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }

        db.close();
        return readItems;

    }

}
