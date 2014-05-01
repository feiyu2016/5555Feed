package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.os.*;

import java.io.*;
import java.util.*;
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

public class NewFeed extends AsyncTask <String, Integer, String> {

    FeedApplication mApp;
    String mUrl;

    NewFeed(FeedApplication mApp) {

        this.mApp = mApp;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String ... args) {

        if (!args[0].startsWith("http://") && !args[0].startsWith("https://"))
            mUrl = "http://" + args[0];
        else
            mUrl = args[0];

        StringBuilder mStringBuilder = new StringBuilder();

        try {

            HttpResponse mHttpResponse = new DefaultHttpClient()
                .execute(new HttpGet(mUrl));

            InputStream mInputStream = mHttpResponse.getEntity()
                .getContent();

            BufferedReader mBuffReader = new BufferedReader(
                    new InputStreamReader(mInputStream));

            String line = null;

            while ((line = mBuffReader.readLine()) != null)
                mStringBuilder.append(line);

            mInputStream.close();

        } catch(Exception e) {

            e.printStackTrace();
            return null;

        }

        return mStringBuilder.toString();

    }

    @Override
    protected void onPostExecute(String feed) {

        if (feed == null)
            invalidFeed();

        else {

            String mFeedName = getFeedName(feed);
            String mEncoding = getEncoding(feed);

            if (mFeedName == null)
                invalidFeed();
            else
                addFeed(mFeedName, mEncoding);

        }

    }

    /**
     * Gets the Feed-title from the RSS-feed.
     */
    private String getFeedName(String feed) {

        try {

            DocumentBuilderFactory mDocBuilder = DocumentBuilderFactory.newInstance();

            Document mDocument = mDocBuilder.newDocumentBuilder().parse(
                    new InputSource(new StringReader(feed)));

            mDocument.getDocumentElement().normalize();

            NodeList mNodeList = mDocument.getElementsByTagName("channel");

            for (int i = 0; i < mNodeList.getLength(); i++)
                return ((Element) mNodeList.item(i)).getElementsByTagName(
                        "title").item(0).getTextContent();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }   

        return null;

    } 

    /**
     * Gets the XML-encoding from the RSS-feed.
     */
    private String getEncoding(String feed) {

        String mEncoding = feed.split("\"")[3].toUpperCase();   

        if (!mEncoding.toUpperCase().equals("UTF-8")
                && !mEncoding.toUpperCase().equals("ISO-8859-1")
                && !mEncoding.toUpperCase().equals("ISO-8859-15")) 
            return "UTF-8";
        else
            return mEncoding;

    }

    /**
     * Adds the new Feed to the Drawer-list.
     */
    public void addFeed(String mFeedName, String mEncoding) {

        DrawerItem mDrawerItem = new DrawerItem(mFeedName, mUrl, mEncoding, new ArrayList <FeedItem> ());
        
        DrawerDB db = new DrawerDB(mApp.getFeedActivity());
        db.addItem(mDrawerItem);
        
        mApp.getNavDrawer().getDrawerAdapter().getDrawerList().add(mDrawerItem);
        mApp.getNavDrawer().getDrawerAdapter().notifyDataSetChanged();

    }

    /**
     * Shows a dialog if mFeedName is null.

     * This could mean that the url is invalid, or that 
     * the RSS-feed is malformed.
     */
    private void invalidFeed() {

        AlertDialog.Builder mDialog = new AlertDialog.Builder(mApp.getFeedActivity());
        mDialog.setTitle(R.string.invalid_feed_message);

        mDialog.setNeutralButton(R.string.invalid_feed_ok, new DialogInterface.
                OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        mDialog.show();

    }

}
