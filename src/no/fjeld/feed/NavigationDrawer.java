package no.fjeld.feed;

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

import com.google.gson.*;

import java.util.*;

public class NavigationDrawer {

    private Activity activity;
    private View view;
    private FeedApplication mApp;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    /**
     * Constructor for the class NavgigationDrawer.
     *
     * @param activity The FeedActivity.
     * @param view     The FeedActivity content view.
     */
    public NavigationDrawer(Activity activity, View view, FeedApplication mApp) {

        this.activity = activity;
        this.view = view;
        this.mApp = mApp;

        initDrawerLayout();
        initDrawerToggle();
        initDrawerListView();
        initDrawerAdapter();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerListView.setAdapter(mDrawerAdapter);

        mApp.getActionBar().setDisplayHomeAsUpEnabled(true);

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
        addViews(); 

        mDrawerListView.setOnItemClickListener(new DrawerClickListener());
        mDrawerListView.setOnItemLongClickListener(new DrawerLongClickListener());

    }

    /**
     * Adds header- and footer-views to the ListView in the
     * NavigationDrawer.
     */
    private void addViews() {

        mDrawerListView.addHeaderView(getView(R.layout.drawer_item,
                    R.id.drawer_item_text,
                    R.string.drawer_header_all_feeds, 0));

        mDrawerListView.addHeaderView(getView(R.layout.drawer_item,
                    R.id.drawer_item_text,
                    R.string.drawer_header_casual_feed, 0));

        mDrawerListView.addHeaderView(getView(R.layout.drawer_group,
                    R.id.drawer_group_text,
                    R.string.drawer_group_subfeeds, 0));

        mDrawerListView.addFooterView(getView(R.layout.drawer_footer,
                    R.id.drawer_footer_text,
                    R.string.drawer_footer_preferences,
                    R.drawable.ic_icon_settings));

        mDrawerListView.addFooterView(getView(R.layout.drawer_footer,
                    R.id.drawer_footer_text,
                    R.string.drawer_footer_about,
                    R.drawable.ic_icon_about));

    } 

    /**
     * Returns an inflated LinearLayout with the correct content
     * according to the parameters.
     *
     * @param  mLayoutResId The layout-xml to be inflated.
     * @param  mTextResId   The TextView to be set.
     * @param  mStringResId The String-resource for the TextView.
     * @param  mImgResId    The Drawable-resource for the layout.
     *
     * @return mListItem    The new LinearLayout.
     */
    private LinearLayout getView(int mLayoutResId, int mTextResId, 
            int mStringResId, int mImgResId) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mListItem = (LinearLayout) inflater.inflate(
                mLayoutResId, null);

        TextView mItemText = (TextView) mListItem.findViewById(mTextResId);
        mItemText.setText(activity.getResources().getString(mStringResId));
    
        if (mImgResId != 0) {
       
            ImageView mItemImage = (ImageView) mListItem.findViewById(
                    R.id.drawer_footer_image);
            mItemImage.setImageResource(mImgResId);
        
        }

        return mListItem;

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

    /**
     * Returns the DrawerLayout for the NavDrawer.
     */
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    /**
     * Returns the DrawerLayouts DrawerToggle.
     */
    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    /**
     * Returns the ListViews adapter.
     */
    public DrawerAdapter getDrawerAdapter() {
        return mDrawerAdapter;
    }

    /** 
     * The LongClickListener for the Navigation Drawers ListView.
     */
    private class DrawerLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView <?> parent, View view,
                int position, long id) {

            position -= 3;

            if (position > -1 && position < mDrawerAdapter.getDrawerList().size()) {
                
                mDrawerAdapter.getDrawerList().remove(position);
                mDrawerAdapter.notifyDataSetChanged();

            }

            return true;

        }

    }

    /**
     * The ClickListener for the Navigation Drawers ListView.
     */
    private class DrawerClickListener implements OnItemClickListener {
    
        @Override
        public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

        }

    }

}
