package no.fjeld.feed;

public class FeedApplication extends android.app.Application {

    public android.app.ActionBar mActionBar;
    public android.widget.TextView mActionBarTitle;
    public android.widget.ImageView mABIndicator;
    public NavigationDrawer mNavDrawer;
    public SwipeRefresh mSwipeRefresh;
    public Feed mFeed;
    public DBManager mDatabase;
    public android.webkit.WebView mWebView;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle.setText(title);
    }

    public void setActionBarIndicator() {
        if ((Integer) mABIndicator.getTag() == R.drawable.ic_action_navigation_menu) {
            mABIndicator.setImageResource(R.drawable.ic_action_navigation_arrow_back);
            mABIndicator.setTag(R.drawable.ic_action_navigation_arrow_back);
        } else {
            mABIndicator.setImageResource(R.drawable.ic_action_navigation_menu);
            mABIndicator.setTag(R.drawable.ic_action_navigation_menu);
        }
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

