package no.fjeld.feed;

import android.support.v4.widget.*;
import android.view.*;

public class SwipeRefresh implements SwipeRefreshLayout.OnRefreshListener {

    private FeedApplication mApp;
    private View mView;

    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Constructor for the class "SwipeToRefresh".
     *
     * @param app  The Application-object for this app.
     * @param view The apps main-view.
     */
    public SwipeRefresh(FeedApplication app, View view) {

        mApp = app;
        mView = view;

        initSwipeLayout();
        setSwipeLayoutListener();
        setSwipeLayoutAppearance();

    }   

    /**
     * Initializes the SwipeRefreshLayout.
     */
    private void initSwipeLayout() {

        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_layout);

    }

    /**
     * Sets a RefreshListener for the SwipeRefreshLayout.
     */
    private void setSwipeLayoutListener() {

        mSwipeLayout.setOnRefreshListener(this);

    }

    /**
     * Sets the Progress Bar's colors.
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

        DrawerItem drawerItem = mApp.getFeed().lastDrawerItem;

        if (drawerItem != null)
            mApp.getFeed().loadFeed(drawerItem, true);
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
