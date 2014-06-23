package no.fjeld.feed;

import android.app.*;
import android.os.*;
import android.view.*;

public class FeedActivity extends Activity {

    private FeedApplication mApp;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initApp();

        mApp.getNavDrawer().getDrawerLayout().openDrawer(Gravity.START);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        mApp.mNavDrawer.getDrawerToggle().syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean drawerOpen = mApp.mNavDrawer.getDrawerLayout()
            .isDrawerOpen(Gravity.LEFT);

        /* Sets the 'Mark all as read'-option invsible when Drawer is open. */
        menu.findItem(R.id.action_mark_all).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mApp.mNavDrawer.getDrawerToggle().onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.action_mark_all) 
            mApp.getFeed().markAllAsRead();

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
   
        if (FeedItemPopup.isVisible())
            FeedItemPopup.getInstance().hideView();
        else
            super.onBackPressed();

    }

    /**
     * Initializes the global values for the application.
     */
    private void initApp() {

        mView = getWindow().getDecorView();
        mApp = (FeedApplication) getApplication();

        mApp.mDatabase = new DBManager(this);

        mApp.mActionBar = getActionBar();
        mApp.mNavDrawer = new NavigationDrawer(this, mView);
        mApp.mSwipeRefresh = new SwipeRefresh(this, mView);
        mApp.mFeed = new Feed(this, mView);

    }

}
