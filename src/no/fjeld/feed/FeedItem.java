package no.fjeld.feed; 

import android.graphics.Bitmap;
import java.util.*;

public class FeedItem implements Comparable <FeedItem> {

    private String mTitle;
    private String mDescription;
    private String mUrl;
    private Date mPubDate;
    private Bitmap mImage;
    private String mFeed;

    public FeedItem(String title, String description, String url, 
            Date pubDate, Bitmap image, String feed) {

        mTitle = title.trim();
        mDescription = description;
        mUrl = url;
        mPubDate = pubDate;
        mImage = image;
        mFeed = feed;

    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUrl() {
        return mUrl;
    }

    public Date getPubDate() {
        return mPubDate;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getFeed() {
        return mFeed;
    }

    @Override
    public int compareTo(FeedItem item) {

        try {
            return getPubDate().compareTo(item.getPubDate());
        } catch (Exception e) {
            return 0;
        }

    }

}
