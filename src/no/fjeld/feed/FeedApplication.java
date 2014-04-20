package no.fjeld.feed;

public class FeedApplication extends android.app.Application {

    private static FeedApplication singleton;

    public android.app.Activity mFeedActivity;
    public android.app.ActionBar mActionBar;
    public NavigationDrawer mNavDrawer;
    public SwipeRefresh mSwipeRefresh;
    public Feed mFeed;
    public android.webkit.WebView mWebView;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public FeedApplication getInstance() {
        return singleton;
    }

    public android.app.Activity getFeedActivity() {
        return mFeedActivity;
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

    public android.webkit.WebView getWebView() {
        return mWebView;
    }


}
