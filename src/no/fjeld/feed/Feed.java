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

    public DrawerItem lastDrawerItem;

    /**
     * Constructor for the class Feed.
     *
     * @param view The FeedActivity content view.
     * @param app The Application-object for this app.
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

        /* If the user does a LongClick on a item, the DragListener 
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
     * If any of the DrawerItems feedItem-list is empty,
     * it will be downloaded.
     */
    public void allFeeds() {

        lastDrawerItem = new DrawerItem(mApp.getFeedActivity().getString(
                    R.string.drawer_header_all_feeds), "", null, new FeedList());

        mFeedAdapter.getFeedList().clear();
        mFeedAdapter.notifyDataSetChanged();

        mFeedAdapter.getFeedList().readItems = mApp.getDatabase().getReadItems();

        mApp.getSwipeRefresh().getSwipeLayout().setEnabled(true);

        for (DrawerItem drawerItem : mApp.getNavDrawer().getDrawerAdapter()
                .getDrawerList()) {

            if (drawerItem.getFeedList().size() > 0) {

                for(FeedItem feedItem : drawerItem.getFeedList()) 
                    mFeedAdapter.getFeedList().add(feedItem);

                mFeedAdapter.notifyDataSetChanged();

            } else {

                mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
                new GetFeed(mApp, drawerItem).execute(drawerItem.getUrl());

            }

                } 

    }


    public void savedFeeds() {

        DrawerItem drawerItem = new DrawerItem(mApp.getFeedActivity().getString(
                    R.string.drawer_header_saved_items), null, null, mApp.getDatabase().getSavedItems());

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

        if (item != lastDrawerItem) {
            mFeedAdapter.getFeedList().clear();
            mFeedAdapter.notifyDataSetChanged();
        }

        if (item.getUrl() != null) 
            mApp.getSwipeRefresh().getSwipeLayout().setEnabled(true);

        lastDrawerItem = item;

        if (item.getFeedList().size() > 0 && !shouldRefresh) {

            mFeedAdapter.getFeedList().readItems 
                = mApp.getDatabase().getReadItems();

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
    public void readLater(FeedItem mFeedItem) {

        mApp.getDatabase().add(mFeedItem); 

    }

    /**
     * Marks all the articles as read.
     */
    public void markAllAsRead() {

        ArrayList <FeedItem> feedList = mFeedAdapter.getFeedList();

        mApp.getDatabase().add(feedList);
        feedList.clear();

        loadFeed(lastDrawerItem, false);

    }

}
