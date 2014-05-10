package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class Feed {

    private final static int MAX_FEEDITEMS = 20;

    private View view;
    private FeedApplication mApp;

    private ListView mFeedListView;
    private FeedAdapter mFeedAdapter;

    private SharedPreferences mSharedPrefs;

    public DrawerItem lastDrawerItem;

    /**
     * Constructor for the class Feed.
     *
     * @param view The FeedActivity content view.
     * @param mApp The Application-object for this app.
     */
    public Feed(View view, FeedApplication mApp) {

        this.view = view;
        this.mApp = mApp;

        initFeedListView();
        initFeedAdapter();

        mFeedListView.setAdapter(mFeedAdapter);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                mApp.getFeedActivity().getBaseContext());

    }

    /**
     * Initializes the ListView.
     */
    private void initFeedListView() {

        mFeedListView = (ListView) view.findViewById(R.id.feed_list);

        View mListMargin = ((LayoutInflater) mApp.getFeedActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.list_margin, null);

        mFeedListView.addHeaderView(mListMargin);
        mFeedListView.addFooterView(mListMargin);

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

        mFeedListView.setOnDragListener(new FeedDragListener(view, mApp));

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initFeedAdapter() {

        mFeedAdapter = new FeedAdapter(mApp.getFeedActivity(), 
                R.layout.list_item, new FeedList ());

    }

    /**
     * Returns the ListViews adapter.
     */
    public FeedAdapter getFeedAdapter() {
        return mFeedAdapter;
    }

    /**
     * Gets called from the NavigationDrawer-class when a feed is clicked.
     *
     * @param item          The DrawerItem-object clicked
     * @param shouldRefresh If the app should force a refresh
     */
    public void loadFeed(DrawerItem item, boolean shouldRefresh) {

        if (item != lastDrawerItem)
            mFeedAdapter.getFeedList().clear();

        lastDrawerItem = item;
        
        if (item.getFeedList().size() > 0 && !shouldRefresh) {

            mFeedAdapter.getFeedList().mReadItems 
                = mApp.getDatabase().getReadItems();

            for (FeedItem feedItem : item.getFeedList())
                mFeedAdapter.getFeedList().add(feedItem); 

            mFeedAdapter.notifyDataSetChanged();

        } else {

            mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
            new GetFeed(mApp, item).execute(item.getUrl()); 

        }


    }

    /**
     * Loads all the feeds.
     * If any of the DrawerItems feedItem-list is empty,
     * it will be downloaded.
     */
    public void allFeeds() {

        lastDrawerItem = new DrawerItem(mApp.getFeedActivity().getString(
                    R.string.drawer_header_all_feeds), null, null, new ArrayList <FeedItem> ());
        
        mFeedAdapter.getFeedList().clear();

        mFeedAdapter.getFeedList().mReadItems 
            = mApp.getDatabase().getReadItems();

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
    
        DrawerItem mDrawerItem = new DrawerItem(mApp.getFeedActivity().getString(
                    R.string.drawer_header_saved_items), null, null, mApp.getDatabase().getSavedItems());
   
        loadFeed(mDrawerItem, false);
    
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


    public void markAllAsRead() {

        ArrayList <FeedItem> mFeedList = mFeedAdapter.getFeedList();

        mApp.getDatabase().add(mFeedList);

        mFeedList.clear();
        loadFeed(lastDrawerItem, false);

    }

}

