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
    private String mEncoding;
    private int position;

    /**
     * Constructor for the class "GetFeed".
     *
     * @param mApp      The application-object for this app.
     * @param mFeedName The feedname for the feed to get.
     * @param mEncoding The encoding for the feed.
     * @param position  The position in the drawerlist.
     */
    public GetFeed(FeedApplication mApp, String mFeedName, String mEncoding, 
            int position) {

        this.mApp = mApp;
        this.mFeedName = mFeedName;
        this.mEncoding = mEncoding;
        this.position = position;

    }

    @Override
    protected void onPreExecute() {
        System.out.println("Loads feed");
    }

    @Override
    protected String doInBackground(String ... args) {

        StringBuilder mStringBuilder = new StringBuilder();

        try {

            HttpResponse mHttpResponse = new DefaultHttpClient()
                .execute(new HttpGet(args[0]));

            InputStream mInputStream = mHttpResponse.getEntity()
                .getContent();

            BufferedReader mBuffReader = new BufferedReader(
                    new InputStreamReader(mInputStream, mEncoding));

            String line = "";

            while((line = mBuffReader.readLine()) != null)
                mStringBuilder.append(line);

            mInputStream.close();

        } catch (Exception e) {
            return null;
        }

        return mStringBuilder.toString();

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

        String mTitle;
        String mDescription;
        String mLink;
        String mPubDate;
        String mImgUrl;

        SharedPreferences mSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(mApp.getFeedActivity()
                    .getBaseContext());

        try {

            DocumentBuilderFactory mDocBuilder = DocumentBuilderFactory
                .newInstance();

            Document mDocument = mDocBuilder.newDocumentBuilder().parse(
                    new InputSource(new StringReader(feed)));

            mDocument.getDocumentElement().normalize();

            NodeList mNodeList = mDocument.getElementsByTagName("item");

            /* Iterates through the Document */
            for (int i = 0; i < mNodeList.getLength(); i++) {

                mTitle = getValue(mNodeList.item(i), "title"); 
                mDescription = getValue(mNodeList.item(i), "description");
                mLink = getValue(mNodeList.item(i), "link");

                mPubDate = (getValue(mNodeList.item(i), "pubDate") != null)
                    ? getValue(mNodeList.item(i), "pubDate")
                    : getValue(mNodeList.item(i), "dc:date"); 

                mImgUrl = (getEnclosure((Element) mNodeList.item(i)) != null) 
                    ? getEnclosure((Element) mNodeList.item(i))
                    : getUrl(mDescription); 

                /* Checks if the user has chosen to download images */
                if (mSharedPrefs.getBoolean("preference_images", true))
                    new GetImage().execute(mImgUrl, mTitle, mDescription, mLink,
                            mPubDate);
                else
                    new GetImage().execute(null, mTitle, mDescription, mLink,
                            mPubDate);

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

        NodeList list = element.getElementsByTagName("enclosure");

        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getAttributes().getLength() > 0) {

                return list.item(i).getAttributes().getNamedItem("url")
                    .getTextContent();

            }
        }

        return null;

    } 

    /**
     * Looks for an <img> tag in the description. 
     *
     * If found, the value will be returned as the url for the
     * image to download.
     */
    private String getUrl(String mDescription) {

        Pattern p = Pattern.compile("src\\s*=\\s*([\"'])?([^ \"']*)");
        Matcher m = p.matcher(mDescription);

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

                HttpURLConnection mConnection = (HttpURLConnection) 
                    new URL(args[0]).openConnection();

                if (mConnection.getResponseCode() != 200)
                    throw new Exception("Error in connection.");

                InputStream mInputStream = mConnection.getInputStream();

                return BitmapFactory.decodeStream(mInputStream);

            }

            catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap mImage) {

            mApp.getFeed().getFeedList().get(position)
                .add(new FeedItem(mTitle, mDescription, 
                            mUrl, mPubDate, mImage, mFeedName));

            mApp.getFeed().getFeedAdapter().getFeedList()
                .add(new FeedItem(mTitle, mDescription, 
                            mUrl, mPubDate, mImage, mFeedName));

            mApp.getFeed().getFeedAdapter().notifyDataSetChanged();

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
        private Date stringToDate(String mPubDate) {

            try {

                SimpleDateFormat format = new SimpleDateFormat(
                        "dd MMM yyyy HH mm ss", Locale.US);

                mPubDate = mPubDate.substring(5, 25).trim();

                byte [] bytes = mPubDate.getBytes("ISO-8859-1");

                mPubDate = new String(bytes, "UTF-8").replaceAll(
                        "\\W", " ");

                return format.parse(mPubDate);

            } catch (Exception e) {
                return null;
            }    

        }

    }

}