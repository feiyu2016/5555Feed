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

    private ArrayList <ArrayList <FeedItem>> mFeedList;
    private ArrayList <FeedItem> mFeedsCombinedList;

    public int lastFeedPosition;

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

        mFeedList = new ArrayList <ArrayList <FeedItem>> ();

        /* Adds the same number of ArrayLists to the mFeedList as 
         * there are items in the drawerlist */
        for (int i = 0; i < mApp.getNavDrawer().getDrawerAdapter()
                .getDrawerList().size(); i++)
            mFeedList.add(new ArrayList <FeedItem> ()); 

        mFeedsCombinedList = new ArrayList <FeedItem> ();

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
     * Returns the ArrayList containing lists of FeedItems.
     */
    public ArrayList <ArrayList <FeedItem>> getFeedList() {
        return mFeedList;
    }

    /**
     * Returns the list that should contain the FeedItems
     * from the lists of mFeedList.
     */
    public ArrayList <FeedItem> getFeedsCombinedList() {
        return mFeedsCombinedList;
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
            int position, boolean shouldRefresh) {

        if (position != lastFeedPosition)
            mFeedAdapter.getFeedList().clear();

        if (mFeedList.get(position).size() > 0 && !shouldRefresh) {

            for (FeedItem item : mFeedList.get(position))
               mFeedAdapter.getFeedList().add(item); 

            mFeedAdapter.notifyDataSetChanged();

        } else {

            mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
            new GetFeed(mApp, mFeedName, mEncoding, position).execute(mUrl); 
    
        }

        lastFeedPosition = position;

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article.
     */
    public void readNow(String url) {

        Activity activity = mApp.getFeedActivity();

        SharedPreferences mSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(activity.getBaseContext());

        if (mSharedPrefs.getBoolean("preference_open_in", true)) 
            activity.startActivity(new Intent(activity, WebViewActivity.class)
                    .putExtra("url", url));
        else
            activity.startActivity(new Intent(Intent.ACTION_VIEW, 
                        Uri.parse(url)));

    }

    /**
     * Gets called from the DragListener if the user has chosen to read
     * the article later.
     */
    public void readLater(String title, String url) {


    }

}
