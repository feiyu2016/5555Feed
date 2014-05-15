package no.fjeld.feed;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.apache.http.HttpResponse;
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
    private String mEncoding;
    private ArrayList <FeedItem> mFeedList;

    private int mNodeListLength;

    /**
     * Constructor for the class "GetFeed".
     *
     * @param app        The application-object for this app.
     * @param drawerItem The DrawerItem-object clicked.
     */
    public GetFeed(FeedApplication app, DrawerItem drawerItem) {

        this.mApp = app;
        this.mFeedName = drawerItem.getFeedName();
        this.mEncoding = drawerItem.getEncoding();
        this.mFeedList = drawerItem.getFeedList();

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String ... args) {

        StringBuilder stringBuilder = new StringBuilder();

        try {

            HttpResponse httpResponse = new DefaultHttpClient()
                .execute(new HttpGet(args[0]));

            InputStream inputStream = httpResponse.getEntity()
                .getContent();

            BufferedReader buffReader = new BufferedReader(
                    new InputStreamReader(inputStream, mEncoding));

            String line = "";

            while((line = buffReader.readLine()) != null)
                stringBuilder.append(line);

            inputStream.close();

        } catch (Exception e) {
            return null;
        }

        return stringBuilder.toString();

    }

    @Override
    protected void onPostExecute(String feed) {

        parseFeed(feed);

    }

    /**
     * Parses the feed and calls the "GetImage"-class to download 
     * images/insert the items in the FeedList.
     */
    private void parseFeed(String feed) {

        String title;
        String description;
        String link;
        String pubDate;
        String imgUrl;

        SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(mApp.getFeedActivity()
                    .getBaseContext());

        try {

            DocumentBuilderFactory docBuilder = DocumentBuilderFactory
                .newInstance();

            Document document = docBuilder.newDocumentBuilder().parse(
                    new InputSource(new StringReader(feed)));

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");
            mNodeListLength = nodeList.getLength();

            /* Iterates through the Document */
            for (int i = 0; i < nodeList.getLength(); i++) {

                title = getValue(nodeList.item(i), "title"); 
                description = getValue(nodeList.item(i), "description");
                link = getValue(nodeList.item(i), "link");

                /* Gets the publication date of the article */
                pubDate = (getValue(nodeList.item(i), "pubDate") != null)
                    ? getValue(nodeList.item(i), "pubDate")
                    : getValue(nodeList.item(i), "dc:date"); 

                /* Gets the imageurl if any. */
                imgUrl = (getEnclosure((Element) nodeList.item(i)) != null) 
                    ? getEnclosure((Element) nodeList.item(i))
                    : getUrl(description); 

                /* Checks if the user has chosen to download images */
                if (sharedPrefs.getBoolean("preference_images", true))
                    new GetImage(i).execute(imgUrl, title, description, link,
                            pubDate);
                else
                    new GetImage(i).execute(null, title, description, link,
                            pubDate);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } 

    } 

    /** 
     * Gets the text-value from an element with the given tag. 
     */
    private String getValue(Node node, String tag) {

        try {
            return ((Element) node).getElementsByTagName(tag).item(0).getTextContent();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * If there is an enclosure-url in the item, this function will return
     * the the value of that tag.
     */ 
    private String getEnclosure(Element element) {

        NodeList nodeList = element.getElementsByTagName("enclosure");

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getAttributes().getLength() > 0) {

                return nodeList.item(i).getAttributes().getNamedItem("url")
                    .getTextContent();

            }
        }

        return null;

    } 

    /**
     * Looks for an <img> tag in the description. 
     * If found, the value will be returned as the url for the
     * image to download.
     */
    private String getUrl(String description) {

        Pattern p = Pattern.compile("src\\s*=\\s*([\"'])?([^ \"']*)");
        Matcher m = p.matcher(description);

        if (m.find())
            return m.group(2);

        return null;

    }

    /**
     * Is called from "onPostExecute()" in GetFeed, to retrieve
     * a Bitmap-object from a provided url.
     */
    private class GetImage extends AsyncTask <String, Integer, Bitmap> {

        private String mTitle;
        private String mDescription;
        private String mUrl;
        private Date mPubDate;

        private int itemNumber;

        /**
         * Constructor for the class GetImage.
         *
         * @param itemNumber The position of current item.
         */
        GetImage(int itemNumber) {

            this.itemNumber = itemNumber;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Bitmap doInBackground(String ... args) {

            mTitle = stripHtml(args[1]);
            mDescription = stripHtml(args[2]);
            mUrl = args[3];
            mPubDate = stringToDate(args[4]);

            try {

                HttpURLConnection connection = (HttpURLConnection) 
                    new URL(args[0]).openConnection();

                if (connection.getResponseCode() != 200)
                    throw new Exception("Error in connection.");

                InputStream inputStream = connection.getInputStream();

                return BitmapFactory.decodeStream(inputStream);

            }

            catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap image) {

            /* Just in case of an empty item. */
            if (mTitle.length() > 5) { 
    
                FeedItem newItem = new FeedItem(mTitle, mDescription,
                    mUrl, mPubDate, image, mFeedName);    

                /* Add the new FeedItem to the DrawerItems mFeedList, and to the
                 * FeedAdapters mFeedList. */
                mFeedList.add(newItem);
                mApp.getFeed().getFeedAdapter().getFeedList().add(newItem);
                
                mApp.getFeed().getFeedAdapter().notifyDataSetChanged();

            }

            /* If this is the last item from the feed, 
             * set the refresh-state of the progressbar to false. */
            if (itemNumber == mNodeListLength - 1)
                mApp.getSwipeRefresh().getSwipeLayout().setRefreshing(false);

        }

        /**
         * Strips the "html"-string of remaining
         * debris from the parsing.
         */
        public String stripHtml(String html) {

            try {

                return Html.fromHtml(html).toString()
                    .replace('\n', (char) 32)
                    .replace((char) 160, (char) 32)
                    .replace((char) 8211, (char) 32)
                    .replace((char) 65532, (char) 32)
                    .trim();

            } catch (Exception e) {
                return null;
            }

        }

        /**
         * Converts a string containing a date to a Date-object.
         */
        private Date stringToDate(String pubDate) {

            try {

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd MMM yyyy HH mm ss", Locale.US);

                pubDate = pubDate.substring(5, 25).trim();

                byte [] bytes = pubDate.getBytes("ISO-8859-1");

                pubDate = new String(bytes, "UTF-8").replaceAll(
                        "\\W", " ");

                return dateFormat.parse(pubDate);

            } catch (Exception e) {
                return null;
            }    

        }

    }

}
