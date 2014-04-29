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

    private ArrayList <FeedItem> mFeedsCombinedList;

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

                    // This function should be cleaned up.
                    @Override
                    public boolean add(FeedItem item) {

                        Set <String> mReadSet = mSharedPrefs
                            .getStringSet("read_items", 
                                new LinkedHashSet <String> ());

                        boolean added = false;

                        if (super.size() == 20)
                            return false;

                        /* If the item is marked as read, don't add it. */
                        for (String title : mReadSet) {
                            if (title.equals(item.getTitle())) {
                                System.out.println(title);
                                return false;
                            }
                        }

                        /* If the item is already in the list, don't add it. */
                        for (int i = 0; i < super.size(); i++) {
                            if (item.getTitle().equals(super.get(i).getTitle())) {
                                return false;       
                            }
                        }

                        /* Adds the items by date */
                        for (int i = 0; i < super.size(); i++) {
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
     * Returns the list that should contain the FeedItems
     * from the lists of mFeedList.
     */
    public ArrayList <FeedItem> getFeedsCombinedList() {
        return mFeedsCombinedList;
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

        if (item.getFeedList().size() > 0 && !shouldRefresh) {

            for (FeedItem feedItem : item.getFeedList())
                mFeedAdapter.getFeedList().add(feedItem); 

            mFeedAdapter.notifyDataSetChanged();

        } else {

            mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(true);
            new GetFeed(mApp, item).execute(item.getUrl()); 

        }

        lastDrawerItem = item;

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

        Set <String> mSavedSet = mSharedPrefs.getStringSet("saved_items",
               new LinkedHashSet <String> ()); 

        mSavedSet.add(mFeedItem.getTitle() + ";" + mFeedItem.getUrl()); 

        mSharedPrefs.edit().putStringSet("saved_items", mSavedSet).commit();

    }

    /**
     * Adds the current articles visible in the ListView to a
     * Stringset in SharedPreferences, preventing them from
     * being added to the ListView later on.
     */
    public void markAllAsRead() {

        Set <String> mReadSet = mSharedPrefs.getStringSet("read_items", 
                new LinkedHashSet <String> ());

        ArrayList <FeedItem> mFeedList = mFeedAdapter.getFeedList();

        for (int i = 0; i < mFeedList.size(); i++)
           mReadSet.add(mFeedList.get(i).getTitle()); 

        mSharedPrefs.edit().putStringSet("read_items", mReadSet).commit();

        mFeedList.clear();
        mFeedAdapter.notifyDataSetChanged();

    }

}
