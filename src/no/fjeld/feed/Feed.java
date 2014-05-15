package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.net.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class Feed {

    private View mView;
    private FeedApplication mApp;

    private ListView mFeedListView;
    private FeedAdapter mFeedAdapter;

    private SharedPreferences mSharedPrefs;

    public DrawerItem lastDrawerItem; // The 'active' DrawerItem.

    /**
     * Constructor for the class Feed.
     *
     * @param view The FeedActivity content view.
     * @param app  The Application-object for this app.
     */
    public Feed(View view, FeedApplication app) {

        this.mView = view;
        this.mApp = app;

        initFeedListView();
        initFeedAdapter();

        mFeedListView.setAdapter(mFeedAdapter);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                app.getFeedActivity().getBaseContext());

    }

    /**
     * Initializes the ListView.
     */
    private void initFeedListView() {

        mFeedListView = (ListView) mView.findViewById(R.id.feed_list);

        View listMargin = ((LayoutInflater) mApp.getFeedActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.list_margin, null);

        mFeedListView.addHeaderView(listMargin);
        mFeedListView.addFooterView(listMargin);

        /* If the user does a LongClick on a item, the FeedDragListener 
         * for the list will be called. */
        mFeedListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView <?> parent, View view,
                int position, long id) {

                view.startDrag(null, new View.DragShadowBuilder(),
                    mFeedListView.getItemAtPosition(position), 0);

                return true;

            }

        });

        mFeedListView.setOnDragListener(new FeedDragListener(mView, mApp));

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initFeedAdapter() {

        mFeedAdapter = new FeedAdapter(mApp.getFeedActivity(), 
                R.layout.list_item, new FeedList());

    }

    /**
     * Returns the ListViews adapter.
     */
    public FeedAdapter getFeedAdapter() {
        return mFeedAdapter;
    }

    /**
     * Loads all the feeds.
     * If any of the DrawerItems feedItems-list is empty,
     * the data will be downloaded and the list will be
     * populated.
     */
    public void allFeeds() {

        lastDrawerItem = new DrawerItem(mApp.getFeedActivity().getString(
                    R.string.drawer_header_all_feeds), "", null, new FeedList());

        mFeedAdapter.getFeedList().clear();
        mFeedAdapter.notifyDataSetChanged();

        mFeedAdapter.getFeedList().readItems = mApp.getDatabase().getReadItems();
        mFeedAdapter.getFeedList().savedItems = mApp.getDatabase().getSavedItems();

        mApp.getSwipeRefresh().getSwipeLayout().setEnabled(true);

        for (DrawerItem drawerItem : mApp.getNavDrawer().getDrawerAdapter()
                .getDrawerList()) {

            /* If the DrawerItems list already has items, fill the 
             * ListView with these. */
            if (drawerItem.getFeedList().size() > 0) {

                for(FeedItem feedItem : drawerItem.getFeedList()) 
                    mFeedAdapter.getFeedList().add(feedItem);

                mFeedAdapter.notifyDataSetChanged();

                /* If the DrawerItems list is empty, create a new AsyncTask
                 * to download items. */
            } else {

                mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
                new GetFeed(mApp, drawerItem).execute(drawerItem.getUrl());

            }

                } 

    }

    /**
     * Loads the saved articles and disables the SwipeRefreshLayout.
     */
    public void savedFeeds() {

        DrawerItem drawerItem = new DrawerItem(mApp.getFeedActivity()
                .getString(R.string.drawer_header_saved_items), null, null, 
                mApp.getDatabase().getSavedItems());

        /* No need for the 'Refresh'-functionality here. */
        mApp.getSwipeRefresh().getSwipeLayout().setEnabled(false);

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

        /* Means that this item has an url, and we can refresh
         * the list if we want to. */
        if (item.getUrl() != null) 
            mApp.getSwipeRefresh().getSwipeLayout().setEnabled(true);

        lastDrawerItem = item;

        /* Updates the 'readItems' and 'savedItems' lists in 
         * the FeedList-class. */
        mFeedAdapter.getFeedList().readItems 
            = mApp.getDatabase().getReadItems();
        mFeedAdapter.getFeedList().savedItems 
            = mApp.getDatabase().getSavedItems();

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
            new GetFeed(mApp, item).execute(item.getUrl()); 

        }

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article.
     */
    public void readNow(String title, String url) {

        Activity activity = mApp.getFeedActivity();

        if (mSharedPrefs.getBoolean("preference_open_in", true)) 
            activity.startActivity(new Intent(activity, WebViewActivity.class)
                    .putExtra("title", title).putExtra("url", url));
        else
            activity.startActivity(new Intent(Intent.ACTION_VIEW, 
                        Uri.parse(url)));

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article later.
     */
    public void readLater(FeedItem item) {

        mFeedAdapter.getFeedList().remove(mFeedAdapter.getFeedList().indexOf(item));
        mFeedAdapter.notifyDataSetChanged();
        mApp.getDatabase().add(item); 

    }

    /**
     * Marks all the articles in the listview as read.
     */
    public void markAllAsRead() {

        mApp.getDatabase().add(mFeedAdapter.getFeedList());

        mFeedAdapter.getFeedList().clear();
        mFeedAdapter.notifyDataSetChanged();

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

        mFeedAdapter.getFeedList().remove(mFeedAdapter.getFeedList().indexOf(item));
        mFeedAdapter.notifyDataSetChanged();

        if (lastDrawerItem.getUrl() == null)
            mApp.getDatabase().delete("savedItems", item.getUrl());

    }

}
