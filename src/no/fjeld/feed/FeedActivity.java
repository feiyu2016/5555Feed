package no.fjeld.feed;

import android.app.*;
import android.os.*;
import android.view.*;

public class FeedActivity extends Activity {

    private View mView;
    private FeedApplication mApp;

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


    private void initApp() {

        mView = getWindow().getDecorView();
        mApp = (FeedApplication) getApplication();

        mApp.mDatabase = new DBManager(this);

        mApp.mFeedActivity = this; 
        mApp.mActionBar = getActionBar();
        mApp.mNavDrawer = new NavigationDrawer(mView, mApp);
        mApp.mSwipeRefresh = new SwipeRefresh(mView, mApp);
        mApp.mFeed = new Feed(mView, mApp);

    }



}
