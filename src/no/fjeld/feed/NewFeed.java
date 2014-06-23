package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.os.*;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

public class NewFeed extends AsyncTask <String, Integer, String> {

    FeedApplication mApp;

    DrawerItem newItem;
    String mUrl;

    NewFeed(Activity activity, DrawerItem newItem) {

        this.mApp = (FeedApplication)activity.getApplication();
        this.newItem = newItem;
        
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

        StringBuilder stringBuilder = new StringBuilder();

        try {

            HttpResponse httpResponse = new DefaultHttpClient()
                .execute(new HttpGet(mUrl));

            InputStream inputStream = httpResponse.getEntity()
                .getContent();

            BufferedReader buffReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            String line = null;

            while ((line = buffReader.readLine()) != null)
                stringBuilder.append(line);

            inputStream.close();

        } catch(Exception e) {

            e.printStackTrace();
            return null;

        }

        return stringBuilder.toString();

    }

    @Override
    protected void onPostExecute(String feed) {

        if (feed == null)
            invalidFeed();

        else {

            String feedName = getFeedName(feed);
            String encoding = getEncoding(feed);

            if (feedName == null)
                invalidFeed();
            else
                addFeed(feedName, encoding);

        }

    }

    /**
     * Gets the Feed-title from the RSS-feed.
     */
    private String getFeedName(String feed) {

        try {

            DocumentBuilderFactory docBuilder = DocumentBuilderFactory.newInstance();

            Document document = docBuilder.newDocumentBuilder().parse(
                    new InputSource(new StringReader(feed)));

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("channel");

            for (int i = 0; i < nodeList.getLength(); i++)
                return ((Element) nodeList.item(i)).getElementsByTagName(
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

        String encoding = feed.split("\"")[3].toUpperCase();   

        if (!encoding.toUpperCase().equals("UTF-8")
                && !encoding.toUpperCase().equals("ISO-8859-1")
                && !encoding.toUpperCase().equals("ISO-8859-15")) 
            return "UTF-8";
        else
            return encoding;

    }

    /**
     * Adds the new Feed to the Drawer-list.
     */
    public void addFeed(String feedName, String encoding) {

        newItem.setFeedName(feedName);
        newItem.setUrl(mUrl);
        newItem.setEncoding(encoding); 
        
        mApp.getNavDrawer().getDrawerAdapter().notifyDataSetChanged();
        mApp.getDatabase().add(newItem);

    }

    /**
     * Shows a dialog if mFeedName is null.
     * This could mean that the url is invalid, or that 
     * the RSS-feed is malformed.
     */
    private void invalidFeed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(mApp.getFeedActivity());
        dialog.setTitle(R.string.invalid_feed_message);

        dialog.setNeutralButton(R.string.invalid_feed_ok, new DialogInterface.
                OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        dialog.show();

        ArrayList <DrawerItem> drawerItems = mApp.getNavDrawer().
                getDrawerAdapter().getDrawerList();
       
        /* Removes the 'Loading feed..'-iten from the DrawerList. */ 
        drawerItems.remove(drawerItems.indexOf(newItem)); 
        mApp.getNavDrawer().getDrawerAdapter().notifyDataSetChanged();

    }

}
