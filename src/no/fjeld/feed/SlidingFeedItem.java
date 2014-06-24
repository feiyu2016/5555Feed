package no.fjeld.feed;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.*;

public class SlidingFeedItem {

    private Activity mActivity;
    private ViewGroup mParentView;
    private FeedItem mFeedItem;

    private SlidingView mSlidingView;
    
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

        LinearLayout mainView = (LinearLayout) inflater.inflate(R.layout.popup_view, null);

        ImageView image = (ImageView) mainView.findViewById(R.id.popup_image);
        TextView title = (TextView) mainView.findViewById(R.id.popup_title);
        TextView description = (TextView) mainView.findViewById(R.id.popup_description);

        image.setImageBitmap(mFeedItem.getImage());
        title.setText(mFeedItem.getTitle());
        description.setText(mFeedItem.getDescription());

        new SlidingView(mActivity, mParentView, mainView);

    } 

}
