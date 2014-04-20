package no.fjeld.feed;

import java.util.*;

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

public class Feed {

    private View view;
    private FeedApplication mApp;

    private ListView mFeedListView;
    private FeedAdapter mFeedAdapter;

    private ArrayList <ArrayList <FeedItem>> mFeeds;
    private ArrayList <FeedItem> mFeedsCombined;

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

        mFeeds = new ArrayList <ArrayList <FeedItem>> ();  
        mFeedsCombined = new ArrayList <FeedItem> ();

    }

    /**
     * Initializes the ListView.
     */
    public void initFeedListView() {

        mFeedListView = (ListView) view.findViewById(R.id.feed_list);

        View mListMargin = ((LayoutInflater) mApp.getFeedActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.list_margin, null);

        mFeedListView.addHeaderView(mListMargin);
        mFeedListView.addFooterView(mListMargin);

    }

    /**
     * Initializes the ListViews adapter.
     */
    public void initFeedAdapter() {

        mFeedAdapter = new FeedAdapter(mApp.getFeedActivity(), 
                R.layout.list_item, new ArrayList <FeedItem> () {

                    @Override
                    public boolean add(FeedItem item) {

                        boolean added = false;

                        /* If the item is already in the list, don't add it. */
                        for (int i = 0; i < super.size() - 1; i++) {
                            if (item.getTitle().equals(super.get(i).getTitle())) {
                                return false;       
                            }
                        }

                        /* Adds the items by date */
                        for (int i = 0; i < super.size() - 1; i++) {
                            if (item.compareTo(super.get(i)) >= 0) {
                                super.add(i, item);
                                added = true;
                                break;
                            }
                        }

                        if (!added) {
                            super.add(item);
                        }

                        return true;

                    }

                }); 

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
     * @param mFeedname The feeds title
     * @param mUrl      The feeds url
     * @param mEncoding The feeds encoding
     * @param position  The feeds position
     */
    public void loadFeed(String mFeedName, String mUrl, String mEncoding, 
            int position) {

            

    }

}
