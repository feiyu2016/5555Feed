package no.fjeld.feed; 

import java.util.*;

public class DrawerItem {

    private String mFeedName;
    private String mUrl;
    private String mEncoding;
    private ArrayList <FeedItem> mFeedList;

    public DrawerItem(String feedName, String url, String encoding, 
            ArrayList <FeedItem> feedList) {

        mFeedName = feedName;
        mUrl = url;
        mEncoding = encoding;
        mFeedList = feedList;

    }

    public String getFeedName() {
        return mFeedName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getEncoding() {
        return mEncoding;
    }

    public ArrayList <FeedItem> getFeedList() {
        return mFeedList;
    }

    public void setFeedName(String name) {
        this.mFeedName = name;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setEncoding(String encoding) {
        this.mEncoding = encoding;
    }

    public void setFeedList(ArrayList <FeedItem> feedList) {
        this.mFeedList = feedList;
    }

}
