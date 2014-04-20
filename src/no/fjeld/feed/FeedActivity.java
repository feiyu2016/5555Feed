package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class FeedActivity extends Activity {

    private FeedActivity mActivity;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mApp.mNavDrawer.getDrawerToggle().onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);

    }


    private void initApp() {
    
        mView = getWindow().getDecorView();
        mApp = (FeedApplication) getApplication();
  
        mApp.mFeedActivity = this; 
        mApp.mActionBar = getActionBar();
        mApp.mNavDrawer = new NavigationDrawer(mView, mApp);
        mApp.mSwipeRefresh = new SwipeRefresh(mView, mApp);
        mApp.mFeed = new Feed(mView, mApp);

    }

}
