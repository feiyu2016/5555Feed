package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.text.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

public class GetFeed extends AsyncTask <String, Integer, String> {

    private FeedApplication mApp;
    private String mFeedName;
    private String mUrl;
    private String mEncoding;
    private int position;

    GetFeed(FeedApplication mApp, String mFeedName, String mUrl, 
            String mEncoding, int position) {

        this.mApp = mApp;
        this.mFeedName = mFeedName;
        this.mUrl = mUrl;
        this.mEncoding = mEncoding;
        this.position = position;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground() {
    }

    @Override
    protected void onPostExecute(String feed) {
    }

}
