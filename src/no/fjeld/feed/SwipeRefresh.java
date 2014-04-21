package no.fjeld.feed;

import android.app.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;

public class SwipeRefresh implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private FeedApplication mApp;

    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Constructor for the class "SwipeToRefresh".
     *
     * @param view  The apps main-view.
     * @param mApp  The Application-object for this app.
     */
    public SwipeRefresh(View view, FeedApplication mApp) {

        this.view = view;
        this.mApp = mApp;

        initSwipeLayout();
        setSwipeLayoutListener();
        setSwipeLayoutAppearance();

    }   

    /**
     * Initializes the SwipeRefreshLayout.
     */
    private void initSwipeLayout() {

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

    }

    /**
     * Sets a RefresListener for the SwipeRefreshLayout.
     */
    private void setSwipeLayoutListener() {

        mSwipeLayout.setOnRefreshListener(this);

    }

    /**
     * Sets the colors for the Layout.
     */
    private void setSwipeLayoutAppearance() {

        mSwipeLayout.setColorScheme(
                android.R.color.holo_blue_light,
                R.color.background,
                android.R.color.holo_blue_light,
                R.color.background);

    }

    /**
     * Handles the events on refresh.
     */
    public void onRefresh() {

        DrawerItem mDrawerItem = mApp.getFeed().lastDrawerItem;

        if (mDrawerItem != null)
            mApp.getFeed().loadFeed(mDrawerItem, true);
        else
            mSwipeLayout.setRefreshing(false);

    }

    /**
     * Returns the SwipeRefreshLayout.
     */ 
    public SwipeRefreshLayout getSwipeLayout() {
        return mSwipeLayout;
    }

}
