package no.fjeld.feed;

import java.util.*;

import com.google.gson.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.view.animation.*;
import android.view.GestureDetector.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class NavigationDrawer {

    private Activity activity;
    private View view;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    private FeedApplication mApplication;

    /**
     * Constructor for the class NavgigationDrawer.
     *
     * @param activity The FeedActivity.
     * @param view     The FeedActivity content view.
     */
    public NavigationDrawer(Activity activity, View view, FeedApplication mApplication) {

        this.activity = activity;
        this.view = view;
        this.mApplication = mApplication;

        initDrawerLayout();
        initDrawerToggle();
        initDrawerListView();
        initDrawerAdapter();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerListView.setAdapter(mDrawerAdapter);

        mApplication.getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Initializes the DrawerLayout.
     */
    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

    }

    /**
     * Initializes the DrawerLayouts DrawerToggle.
     */
    private void initDrawerToggle() {

        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, 
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_closed) {

            public void onDrawerClosed(View mainView) {
                activity.getActionBar().setSubtitle(R.string.drawer_closed);
            }

            public void onDrawerOpened(View drawerView) {
                activity.getActionBar().setSubtitle(R.string.drawer_open);
            }

        };

    }

    /**
     * Initializes the DrawerLayouts ListView.
     */ 
    private void initDrawerListView() {

        mDrawerListView = (ListView) view.findViewById(R.id.drawer_list);

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initDrawerAdapter() {

        mDrawerAdapter = new DrawerAdapter(
                activity, R.layout.drawer_item, getDrawerList());

    }

    /**
     * Returns an ArrayList loaded from the SharedPreferences-set "drawer_items".
     */
    private ArrayList <DrawerItem> getDrawerList() {

        ArrayList <DrawerItem> mDrawerList = new ArrayList <DrawerItem> (); 

        Gson mGson = new GsonBuilder().setPrettyPrinting().create();
        SharedPreferences mSharedPrefs = PreferenceManager.
            getDefaultSharedPreferences(activity.getBaseContext());

        Set <String> mDrawerSet = mSharedPrefs.
            getStringSet("drawer_items", new LinkedHashSet <String> ());

        for (String item : mDrawerSet)
            mDrawerList.add(mGson.fromJson(item, DrawerItem.class));

        if (mDrawerList.size() > 1)
            mDrawerList = sorted(mDrawerList);

        return mDrawerList;

    }

    /**
     * Sorts the items alphabetically based on their titles. 
     *
     * @param  mTempItems The list to be sorted.
     * @return mTempItems The sorted list.
     */
    private ArrayList <DrawerItem> sorted(ArrayList <DrawerItem> mTempList) {

        Collections.sort(mTempList, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo(
                    second.getFeedName().toLowerCase());
            }

        });

        return mTempList;

    }

}
