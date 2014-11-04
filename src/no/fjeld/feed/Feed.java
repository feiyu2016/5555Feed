package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.net.*;
import android.preference.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class Feed {

    private FeedApplication mApp;
    private Activity mActivity;
    private View mView;

    private ListView mFeedListView;
    private FeedAdapter mFeedAdapter;

    private SharedPreferences mSharedPrefs;

    public DrawerItem lastDrawerItem; // The 'active' DrawerItem.

    /**
     * Constructor for the class Feed.
     *
     * @param activity The main activity for this app.
     * @param view     The FeedActivity content view.
     */
    public Feed(Activity activity, View view) {

        mApp = (FeedApplication)activity.getApplication();
        mActivity = activity;
        mView = view;

        initFeedListView();
        initFeedAdapter();

        mFeedListView.setAdapter(mFeedAdapter);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                mActivity.getBaseContext());

    }

    /**
     * Initializes the ListView.
     */
    private void initFeedListView() {

        mFeedListView = (ListView) mView.findViewById(R.id.feed_list);

        View listMargin = ((LayoutInflater) mActivity 
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.list_margin, null);

        mFeedListView.addHeaderView(listMargin);
        mFeedListView.addFooterView(listMargin);

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initFeedAdapter() {

        mFeedAdapter = new FeedAdapter(mActivity,
                R.layout.list_item, new FeedList());

    }

    /**
     * Returns the ListView.
     */
    public ListView getFeedListView() {
        return mFeedListView;
    }

    /**
     * Returns the ListViews adapter.
     */
    public FeedAdapter getFeedAdapter() {
        return mFeedAdapter;
    }

    /**
     * Displays the saved articles. 
     */
    public void savedFeeds() {

        DrawerItem drawerItem = new DrawerItem(mActivity
                .getString(R.string.drawer_footer_saved_lower), null, null, 
                mApp.getDatabase().getSavedItems());

        loadFeed(drawerItem, false);

    }

    /**
     * Gets called from the NavigationDrawer-class when a feed is clicked.
     *
     * @param item          The DrawerItem-object clicked
     * @param shouldRefresh If the app should force a refresh
     */
    public void loadFeed(DrawerItem item, boolean shouldRefresh) {

        /* If the clicked DrawerItem isn't the same as the last one,
         * clear the list. */
        if (item != lastDrawerItem) {
            mFeedAdapter.getFeedList().clear();
            mFeedAdapter.notifyDataSetChanged();
        }

        lastDrawerItem = item;

        /* Updates the 'readItems' and 'savedItems' lists in 
         * the FeedList-class. */
        mFeedAdapter.getFeedList().readItems 
            = mApp.getDatabase().getReadItemsSet();
        mFeedAdapter.getFeedList().savedItems 
            = mApp.getDatabase().getSavedItemsSet();

        /* If the items FeedList has items, and we have chosen
         * not to download anything new, fill the ListView with
         * existing items. */
        if (item.getFeedList().size() > 0 && !shouldRefresh) {

            for (FeedItem feedItem : item.getFeedList())
                mFeedAdapter.getFeedList().add(feedItem); 

            mFeedAdapter.notifyDataSetChanged();

        } else if ((item.getFeedList().size() == 0 || shouldRefresh)
                && item.getUrl() != null) {

            mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
            new GetFeed(mActivity, item).execute(item.getUrl()); 

        }

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article.
     */
    public void readNow(String title, String url) {

        /* Loads the url in the WebView-class displayed in the app. */ 
        if (mSharedPrefs.getBoolean("preference_open_in", true)) 
            mActivity.startActivity(new Intent(mActivity, WebViewActivity.class)
                    .putExtra("title", title).putExtra("url", url));

        /* Loads the url in the default browser. */
        else
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, 
                        Uri.parse(url)));

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article later.
     */
    public void readLater(FeedItem item) {

        mApp.getDatabase().add(item); 

    }

    /**
     * Marks all the articles in the listview as read.
     */
    public void markAllAsRead() {

        mApp.getDatabase().add(mFeedAdapter.getFeedList());

        if (lastDrawerItem.getUrl() == null)
            mApp.getDatabase().delete("savedItems", null);

    }

    /**
     * Marks an article as read by adding it to
     * the 'readItems' table in the database.
     *
     * @param item The FeedItem to add to the DB.
     */
    public void markAsRead(FeedItem item) {
        
        mApp.getDatabase().add(item.getUrl()); 

        if (lastDrawerItem.getUrl() == null)
            mApp.getDatabase().delete("savedItems", item.getUrl());

    }

}
