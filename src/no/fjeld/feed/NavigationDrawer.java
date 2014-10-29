package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class NavigationDrawer {

    private FeedApplication mApp;
    private Activity mActivity;
    private View mView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    /**
     * Constructor for the class NavgigationDrawer.
     *
     * @param activity The main activity for this app.
     * @param view     The FeedActivity content view.
     */
    public NavigationDrawer(Activity activity, View view) {

        mApp = (FeedApplication)activity.getApplication();
        mActivity = activity;
        mView = view;

        initDrawerLayout();
        initDrawerToggle();
        initDrawerAdapter();
        initDrawerListView();
        initDrawerItems();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerListView.setAdapter(mDrawerAdapter);

        mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Initializes the DrawerLayout.
     */
    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) mView.findViewById(R.id.drawer_layout);

    }

    /**
     * Initializes the DrawerLayouts DrawerToggle.
     */
    private void initDrawerToggle() {

        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, 
                android.R.color.transparent, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View mainView) {

                DrawerItem drawerItem = mApp.getFeed().lastDrawerItem;

                if (drawerItem != null)
                    mApp.setActionBarTitle(drawerItem.getFeedName());

                mApp.setActionBarIndicator();
                mActivity.invalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {

                mApp.setActionBarTitle(mActivity.getResources().getString(R.string.app_name));
                mApp.setActionBarIndicator();
                mActivity.invalidateOptionsMenu();

            }

        };

    }

    /**
     * Initializes the DrawerLayouts ListView.
     */ 
    private void initDrawerListView() {

        mDrawerListView = (ListView) mView.findViewById(R.id.drawer_list);
        mDrawerListView.setOnItemClickListener(new DrawerClickListener());
        mDrawerListView.setOnItemLongClickListener(new DrawerLongClick(mActivity, mDrawerAdapter));

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initDrawerAdapter() {

        mDrawerAdapter = new DrawerAdapter(
                mActivity, R.layout.drawer_item, 
                mApp.getDatabase().getDrawerItems());

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

        mDrawerLayout.closeDrawer(Gravity.LEFT);

        DrawerItem drawerItem = mDrawerAdapter
            .getDrawerList().get(position);

        mApp.getFeed().loadFeed(drawerItem, false); 

    }

    /**
     * Displays a dialog which prompts the user for an
     * url. 
     * The url will be sent to the NewFeed-class, and from
     * there a new DrawerItem will be called.
     */ 
    public void newFeed() {

        /* Some final-modifiers needed for inner class access. */
        final EditText input = new EditText(mActivity);
        input.setHint(R.string.new_feed_hint);

        /* Shows the keyboard */
        input.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) mActivity.getSystemService(
                    Context.INPUT_METHOD_SERVICE)).showSoftInput(input, 0);
            }

        }, 50); 

        /* The dialog */
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(mActivity, R.style.DefaultTheme));
        dialog.setTitle(R.string.new_feed_title);
        dialog.setView(input);

        /* The "OK"-button */
        dialog.setPositiveButton(R.string.new_feed_ok,
                new DialogInterface.OnClickListener() {
                    @Override 
                    public void onClick(DialogInterface dialog, int button) {
                        addFeed(input.getText().toString());
                    }

                });

        /* The "Cancel"-button */
        dialog.setNegativeButton(R.string.new_feed_cancel, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {}
                });

        dialog.show();

    }

    /**
     * Adds a new Feed to the drawer list and calls a
     * new AsyncTask to get the data needed.
     *
     * @param url The url for the new Feed.
     */
    public void addFeed(String url) {

        DrawerItem newItem = new DrawerItem(mActivity
                .getString(R.string.drawer_item_loading), null, null, 
                new ArrayList <FeedItem> ());
        mDrawerAdapter.getDrawerList().add(newItem); 
        mDrawerAdapter.notifyDataSetChanged();

        NewFeed newFeed = new NewFeed(mActivity, newItem);
        newFeed.execute(url);

    }

    /**
     * The ClickListener for the Navigation Drawers ListView.
     */
    private class DrawerClickListener implements OnItemClickListener {

        private View mLastItemView;

        @Override
        public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

            setItemView(view, position);
            drawerItemClicked(position);

        }

        /**
         * Sets the clicked views fontstyle to bold on click,
         * and resets the last clicked items fontstyle.
         */
        public void setItemView(View view, int position) {

            int listSize = mDrawerListView.getCount();

            if (mLastItemView != null) {

                TextView textView = (TextView) mLastItemView.findViewById(
                        R.id.drawer_item_text);
                textView.setTypeface(((TextView) view.findViewById(
                                R.id.drawer_item_text)).getTypeface(), 
                        Typeface.NORMAL);

            }

            TextView textView = (TextView) view.findViewById(
                    R.id.drawer_item_text);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

            mLastItemView = view;

        }

    }

}
