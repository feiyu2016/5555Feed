package no.fjeld.feed;

import android.app.Activity;
import android.support.v4.widget.*;
import android.view.*;

public class SwipeRefresh implements SwipeRefreshLayout.OnRefreshListener {

    private FeedApplication mApp;
    private View mView;

    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Constructor for the class "SwipeToRefresh".
     *
     * @param activity The main activity for this app.
     * @param view The apps main-view.
     */
    public SwipeRefresh(Activity activity, View view) {

        mApp = (FeedApplication)activity.getApplication();
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
                R.color.accent_dark,
                R.color.background,
                R.color.accent_dark,
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
