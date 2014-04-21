package no.fjeld.feed; 

import java.util.*;

public class DrawerItem {

    private String mFeedName;
    private String mUrl;
    private String mEncoding;
    private ArrayList <FeedItem> mFeedList;

    public DrawerItem(String mFeedName, String mUrl, String mEncoding, 
            ArrayList <FeedItem> mFeedList) {
        
        this.mFeedName = mFeedName;
        this.mUrl = mUrl;
        this.mEncoding = mEncoding;
        this.mFeedList = mFeedList;

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

}
