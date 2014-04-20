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

    }

    /**
     * Initializes the ListView.
     */
    public void initFeedListView() {

        mFeedListView = (ListView) view.findViewById(R.id.feed_list);

        View mListMargin = ((LayoutInflater) activity.getSystemService(Context.
                    LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_margin, null);

        mFeedList.addHeaderView(mListMargin);
        mFeedList.addFooterView(mListMargin);

    }

    /**
     * Initializes the ListViews adapter.
     */
    public void initFeedAdapter() {

        mFeedAdapter = new FeedAdapter(mApp.getFeedActivity(), 
                R.layout.list_item, new ArrayList <FeedItem> ()); 

    }

    /**
     * Returns the ListViews adapter.
     */
    public FeedAdapter getFeedAdapter() {
        return mFeedAdapter;
    }

}
