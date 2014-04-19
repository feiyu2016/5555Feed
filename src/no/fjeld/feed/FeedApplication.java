package no.fjeld.feed;

public class FeedApplication extends android.app.Application {

    private static FeedApplication singleton;

    private android.app.ActionBar mActionBar;
    private NavigationDrawer mNavDrawer;
    private android.webkit.WebView mWebView;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public FeedApplication getInstance() {
        return singleton;
    }

    public android.app.ActionBar getActionBar() {
        return mActionBar;
    }

    public NavigationDrawer getNavDrawer() {
        return mNavDrawer;
    }

    public android.webkit.WebView getWebView() {
        return mWebView;
    }


}

