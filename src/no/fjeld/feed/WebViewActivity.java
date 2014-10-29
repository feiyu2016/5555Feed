package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class WebViewActivity extends Activity {

    private WebView mWebView;
    private FeedApplication mApp;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
        mApp = (FeedApplication) getApplication();
        
        mProgressBar = (ProgressBar) findViewById (R.id.webview_progress);

        initActionBar();
        initWebView();

    }

    /**
     * Initializes the custom ActionBar.
     */
    private void initActionBar() {

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        getActionBar().setCustomView(inflater.inflate(
                    R.layout.action_bar, null));

        int abResId = getResources().getIdentifier(
                "action_bar_container", "id", "android");

        mApp.mActionBarTitle = (android.widget.TextView) 
            getWindow().getDecorView().findViewById(abResId)
            .findViewById(R.id.ab_title);

        mApp.mABIndicator = (android.widget.ImageView) 
            getWindow().getDecorView().findViewById(abResId)
            .findViewById(R.id.drawer_indicator);
        
        mApp.mABIndicator.setTag(R.drawable.ic_action_navigation_menu);
        mApp.setActionBarIndicator();

        mApp.mABIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.destroy();
                finish();
            }
        });

    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            
        finish();
        return super.onOptionsItemSelected(item);

    }

    /**
     * Initializes the WebView.
     */
    private void initWebView() {

        mWebView = (WebView) findViewById(R.id.webview);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setPadding(0, 0, 0, 0);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        /* Loads the url provided as an 'extra' to the WebView-intent. */
        mWebView.loadUrl(getIntent().getExtras().getString("url"));

    }

    /**
     * The custom WebViewClient which sets the ProgressBar visibility
     * when a page starts to load, and when it has finished loading.
     * The subtitle of the Action Bar is also set to the title of the page.
     */
    private class MyWebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favIcon) {
            super.onPageStarted(view, url, favIcon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            mApp.setActionBarTitle(view.getTitle());
        }

    }

    /**
     * The custom WebChomeClient which sets the ProgressBars progress 
     * when a page is loading.
     */
    private class MyWebChromeClient extends android.webkit.WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            mProgressBar.setProgress(progress);
        }

    }

}
