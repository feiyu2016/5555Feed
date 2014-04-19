package no.fjeld.feed; 

import android.graphics.Bitmap;
import java.util.*;
import java.text.*;

public class FeedItem implements Comparable <FeedItem> {

    private String mTitle;
    private String mDescription;
    private String mUrl;
    private Date mPubDate;
    private Bitmap mImage;
    private String mFeed;

    public FeedItem(String mTitle, String mDescription, String mUrl, Date mPubDate, Bitmap mImage, String mFeed) {

        this.mTitle = mTitle.trim();
        this.mDescription = mDescription;
        this.mUrl = mUrl;
        this.mPubDate = mPubDate;
        this.mImage = mImage;
        this.mFeed = mFeed;

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
