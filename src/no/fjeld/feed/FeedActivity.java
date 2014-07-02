package no.fjeld.feed;

import android.app.*;
import android.content.*;
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
        handleIntent(getIntent());

        mApp.getNavDrawer().getDrawerLayout().openDrawer(Gravity.START);

    }

    /**
     * Checks if this intent contains a StringExtra, which means
     * that (hopefully) an RSS-url has been passed to the app.
     * In that case, the URL will be passed to addFeed() in 
     * NavigationDrawer, and it will be added to the list of feeds.
     */
    private void handleIntent(Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SEND)) {
            if (intent.getType().equals("text/plain")) {
                String url = intent.getStringExtra(Intent.EXTRA_TEXT);
                mApp.getNavDrawer().addFeed(url);
            }
        }
   
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

        if (SlidingView.getInstance() != null)
            SlidingView.getInstance().slideOut();
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
