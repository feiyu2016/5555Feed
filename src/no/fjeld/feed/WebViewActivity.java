package no.fjeld.feed;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class WebViewActivity extends Activity {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) findViewById (R.id.webview_progress);

        initWebView();

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
            getActionBar().setTitle(view.getTitle());
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
