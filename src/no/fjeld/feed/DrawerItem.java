package no.fjeld.feed; 

public class DrawerItem {

    private String mFeedName;
    private String mUrl;
    private String mEncoding;

    public DrawerItem(String mFeedName, String mUrl, String mEncoding) {
        
        this.mFeedName = mFeedName;
        this.mUrl = mUrl;
        this.mEncoding = mEncoding;

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

}
