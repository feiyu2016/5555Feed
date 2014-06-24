package no.fjeld.feed;

import android.app.Activity;
import android.content.*;
import android.net.*;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;

public class SlidingFeedItem {

    private final static int SHARE = 0;
    private final static int READ = 1;
    private int mAction;

    private Activity mActivity;
    private ViewGroup mParentView;
    private FeedItem mFeedItem;

    public SlidingFeedItem(Activity activity, ViewGroup parentView, 
            FeedItem feedItem) {

        mActivity = activity;
        mParentView = parentView;
        mFeedItem = feedItem;

        initChildView();

    }

    private void initChildView() {

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

        image.setImageBitmap(mFeedItem.getImage());
        title.setText(mFeedItem.getTitle());
        description.setText(mFeedItem.getDescription());

        setOnClickListeners(mainView);

        new SlidingView(mActivity, mParentView, mainView) {
            @Override
            public void onSlideOutFinished() {
                if      (mAction == SHARE) share();
                else if (mAction == READ)  read(); 
            }   
        };

    } 

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

    private void share() {

    }

    private void read() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                mActivity);

        if (prefs.getBoolean("preference_open_in", true)) {

            Intent appIntent = new Intent(mActivity,
                    WebViewActivity.class).putExtra("url", mFeedItem.getUrl());
            mActivity.startActivity(appIntent);

        } else {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mFeedItem.getUrl()));
            mActivity.startActivity(browserIntent);

        }
    
    }

}
