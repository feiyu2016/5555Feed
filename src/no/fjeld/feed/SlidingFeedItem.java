package no.fjeld.feed;

import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class SlidingFeedItem {

    private final static int SHARE = 0;
    private final static int READ = 1;
    private int mAction = -1;

    private Activity mActivity;
    private ViewGroup mParentView;
    private FeedItem mFeedItem;

    /**
     * Public constructor for SlidingFeedItem.
     *
     * @param activity   An activitypointer. 
     * @param parentView The ViewGroup that this View should be displayed in.
     * @param feedItem   The FeedItem that contains the values for the View.
     */
    public SlidingFeedItem(Activity activity, ViewGroup parentView, 
            FeedItem feedItem) {

        mActivity = activity;
        mParentView = parentView;
        mFeedItem = feedItem;

        initView();

    }

    /**
     * Calls the functions that initializes the necessary values
     * and objects for the sliding view.
     */
    private void initView() {

        LinearLayout mainView = getView();
        setOnClickListeners(mainView);
        initSlidingView(mainView);

    }

    /**
     * Creates the View that displays the FeedItem's image, title,
     * and description.
     *
     * @return mainView The new View with the FeedItem's values.
     */    
    private LinearLayout getView() {

        LayoutInflater inflater = (LayoutInflater) mActivity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mainView = (LinearLayout) inflater.inflate(
                R.layout.popup_view, null);

        ImageView image = (ImageView) mainView.findViewById(
                R.id.popup_image);
        TextView title = (TextView) mainView.findViewById(
                R.id.popup_title);
        TextView description = (TextView) mainView.findViewById(
                R.id.popup_description);

        if (mFeedItem.getImage() == null)
            image.setVisibility(View.GONE);
        else 
            image.setImageBitmap(mFeedItem.getImage());

        title.setText(mFeedItem.getTitle());
        description.setText(mFeedItem.getDescription());

        return mainView;

    } 

    /**
     * Sets OnClickListeners for the buttons (Share and Read) in
     * the View.
     */
    private void setOnClickListeners(LinearLayout mainView) {

        mainView.findViewById(R.id.popup_share).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAction = 0;
                        SlidingView.getInstance().slideOut();                 
                    }
                });

        mainView.findViewById(R.id.popup_read).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAction = 1;
                        SlidingView.getInstance().slideOut();
                    }
                });

    }

    /**
     * Creates a new instance of the SlidingView class.
     * It also overrides 'onSlideOutFinished()' in SlidingView
     * in order to execute the correct function when a button
     * has been clicked.
     */
    private void initSlidingView(LinearLayout mainView) {

        new SlidingView(mActivity, mParentView, mainView) {
            @Override
            public void onSlideOutFinished() {
                if      (mAction == SHARE) share();
                else if (mAction == READ)  read(); 
            }   
        };

    }

    /**
     * Creates a new instance of the ShareView class.
     * It will display a number of icons representing apps
     * that can share the data from the FeedItem-pointer
     * passed to the SlidingFeedItem-constructor.
     */ 
    private void share() {

        new ShareView(mActivity, mParentView, mFeedItem);

    }

    /**
     * Opens either a WebView or the default browser based on
     * the users preferences.
     * It displays the article from the url that the FeedItem
     * contains.
     */
    private void read() {

        SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(mActivity);

        if (prefs.getBoolean("preference_open_in", true)) {

            Intent appIntent = new Intent(mActivity,
                    WebViewActivity.class).putExtra(
                        "url", mFeedItem.getUrl());
            mActivity.startActivity(appIntent);

        } else {

            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW, Uri.parse(mFeedItem.getUrl()));
            mActivity.startActivity(browserIntent);

        }

    }

}
