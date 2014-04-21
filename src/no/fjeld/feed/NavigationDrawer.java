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

    private View view;
    private FeedApplication mApp;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    /**
     * Constructor for the class NavgigationDrawer.
     *
     * @param view  The FeedActivity content view.
     * @param mApp  The Application-object for this app.
     */
    public NavigationDrawer(View view, FeedApplication mApp) {

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

        mDrawerToggle = new ActionBarDrawerToggle(mApp.getFeedActivity(), mDrawerLayout, 
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_closed) {

            public void onDrawerClosed(View mainView) {
                mApp.getActionBar().setSubtitle(R.string.drawer_closed);
            }

            public void onDrawerOpened(View drawerView) {
                mApp.getActionBar().setSubtitle(R.string.drawer_open);
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

        /* Headers */
        mDrawerListView.addHeaderView(getView(R.layout.drawer_item,
                    R.id.drawer_item_text,
                    R.string.drawer_header_all_feeds, 0));

        mDrawerListView.addHeaderView(getView(R.layout.drawer_item,
                    R.id.drawer_item_text,
                    R.string.drawer_header_casual_feed, 0));

        mDrawerListView.addHeaderView(getView(R.layout.drawer_group,
                    R.id.drawer_group_text,
                    R.string.drawer_group_subfeeds, 0));

        /* Footers */
        mDrawerListView.addFooterView(((LayoutInflater) mApp.getFeedActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.drawer_footer_divider, null));

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

        LayoutInflater inflater = (LayoutInflater) mApp.getFeedActivity()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mListItem = (LinearLayout) inflater.inflate(
                mLayoutResId, null);

        TextView mItemText = (TextView) mListItem.findViewById(mTextResId);
        mItemText.setText(mApp.getFeedActivity().getResources()
                .getString(mStringResId));

        if (mImgResId != 0) {

            ImageView mItemImage = (ImageView) mListItem.findViewById(
                    R.id.drawer_footer_image);
            mItemImage.setImageResource(mImgResId);

        }

        /* This is the case where we add the "Add Feed"-button */
        if (mStringResId == R.string.drawer_group_subfeeds) {

            ((LinearLayout) mListItem.findViewById(R.id.drawer_group_new_feed))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newFeed();
                    }
                });

        }

        return mListItem;

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initDrawerAdapter() {

        mDrawerAdapter = new DrawerAdapter(
                mApp.getFeedActivity(), R.layout.drawer_item, getDrawerList());

    }

    /**
     * Returns an ArrayList loaded from the SharedPreferences-set "drawer_items".
     */
    private ArrayList <DrawerItem> getDrawerList() {

        ArrayList <DrawerItem> mDrawerList = new ArrayList <DrawerItem> (); 

        Gson mGson = new GsonBuilder().setPrettyPrinting().create();

        SharedPreferences mSharedPrefs = PreferenceManager.
            getDefaultSharedPreferences(mApp.getFeedActivity().getBaseContext());

        Set <String> mDrawerSet = mSharedPrefs.
            getStringSet("drawer_items", new LinkedHashSet <String> ());

        for (String item : mDrawerSet)
            mDrawerList.add(mGson.fromJson(item, DrawerItem.class));

        if (mDrawerList.size() > 1)
            mDrawerList = sorted(mDrawerList);

        return mDrawerList;

    }

    /**
     * Sorts the mTempList-list alphabetically based on the feedname. 
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
     * Gets called from the OnItemClickListener when the user has 
     * clicked one of the items in the drawer.
     */
    private void drawerItemClicked(int position) {

        int mDrawerSize = mDrawerListView.getChildCount();

        if (position == 0) {                        // All feeds


        } else if (position == 1) {                 // Casual feeds


        } else if (position == mDrawerSize - 2) {   // Preferences

            mApp.getFeedActivity().startActivity(new Intent(
                        mApp.getFeedActivity(), PreferencesActivity.class));

        } else if (position == mDrawerSize - 1) {   // About


        } else if (position > 2 && position < mDrawerSize - 2) { // Feed

            DrawerItem mDrawerItem = mDrawerAdapter
                .getDrawerList().get(position - 3);

            mApp.getFeed().loadFeed(mDrawerItem, false); 

        }

    }


    public void newFeed() {

        final Activity activity = mApp.getFeedActivity();

        final EditText mInput = new EditText(activity);
        mInput.setHint(R.string.new_feed_hint);

        /* Shows the keyboard */
        mInput.postDelayed(new Runnable() {

            @Override
            public void run() {
                ((InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE)).showSoftInput(mInput, 0);
            }

        }, 50); 

        /* The dialog */
        AlertDialog.Builder mDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(activity, R.style.DefaultTheme));
        mDialog.setTitle(R.string.new_feed_title);
        mDialog.setView(mInput);

        /* The "OK"-button */
        mDialog.setPositiveButton(R.string.new_feed_ok,
                new DialogInterface.OnClickListener() {

                    @Override 
                    public void onClick(DialogInterface dialog, int button) {
                        NewFeed newFeed = new NewFeed(mApp);
                        newFeed.execute(mInput.getText().toString());
                    }

                });

        /* The "Cancel"-button */
        mDialog.setNegativeButton(R.string.new_feed_cancel, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                    }
                });

        mDialog.show();

    }

    /**
     * The ClickListener for the Navigation Drawers ListView.
     */
    private class DrawerClickListener implements OnItemClickListener {

        private View lastClickedView;

        @Override
        public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

            setItemView(view, position);
            drawerItemClicked(position);

        }

        /**
         * Sets the clicked views fontstyle to bold on click,
         * and reset the last clicked.
         */
        public void setItemView(View view, int position) {

            int listSize = mDrawerListView.getCount();

            if (position != 2 && position <= listSize - 3) {

                if (lastClickedView != null) {

                    TextView mTextView = (TextView) lastClickedView.findViewById(
                            R.id.drawer_item_text);
                    mTextView.setTypeface(((TextView) view.findViewById(
                            R.id.drawer_item_text)).getTypeface(), 
                            Typeface.NORMAL);

                }

                TextView mTextView = (TextView) view.findViewById(
                        R.id.drawer_item_text);
                mTextView.setTypeface(mTextView.getTypeface(), Typeface.BOLD);

                lastClickedView = view;

            }

        }

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

}
