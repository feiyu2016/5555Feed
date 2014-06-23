package no.fjeld.feed;

public class FeedApplication extends android.app.Application {

    public android.app.ActionBar mActionBar;
    public NavigationDrawer mNavDrawer;
    public SwipeRefresh mSwipeRefresh;
    public Feed mFeed;
    public DBManager mDatabase;
    public android.webkit.WebView mWebView;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public android.app.ActionBar getActionBar() {
        return mActionBar;
    }

    public NavigationDrawer getNavDrawer() {
        return mNavDrawer;
    }

    public SwipeRefresh getSwipeRefresh() {
        return mSwipeRefresh;
    }

    public Feed getFeed() {
        return mFeed;
    }

    public DBManager getDatabase() {
        return mDatabase;
    }

    public android.webkit.WebView getWebView() {
        return mWebView;
    }


}

