package no.fjeld.feed;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class WebViewActivity extends Activity {

    private FeedApplication mApp;

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        mApp = FeedApplication.getInstance();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) findViewById (R.id.webview_progress);

        initWebView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview, menu);

        return true;

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

        mWebView.loadUrl(getIntent().getExtras().getString("url"));

    }

    /**
     * The custom WebViewClient which sets the ProgressBars visibility
     * when a page starts to load, and when it has finished loading.
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
            getActionBar().setSubtitle(view.getTitle());
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
