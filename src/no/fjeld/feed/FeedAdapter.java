package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

class FeedAdapter extends ArrayAdapter <FeedItem> {

    private Activity mActivity;
    private FeedList mFeedList;

    /**
     * Constructor for the class FeedAdapter.
     *
     * @param activity     FeedActivity-pointer
     * @param resourceView The resource id for the FeedItem's layout
     * @param feedList     The FeedList which contains FeedItems
     */
    public FeedAdapter(Activity activity, int resourceView, FeedList feedList) {

        super(activity, resourceView, feedList);
        mActivity = activity;
        mFeedList = feedList;    

    }

    private class ViewHolder {

        TextView mTitle;
        TextView mDescription;
        ImageView mImage;

    }

    /**
     * Returns the view for the current FeedItem.
     *
     * @return view The FeedItem-view.
     */
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {

            /* Inflates the layout for the FeedItem. */
            view = ((LayoutInflater) mActivity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.mTitle = (TextView) view.findViewById(R.id.feed_title);
            viewHolder.mDescription = (TextView) view.findViewById(R.id.feed_description);
            viewHolder.mImage = (ImageView) view.findViewById(R.id.feed_image);

            view.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) view.getTag();

        FeedItem feedItem = mFeedList.get(position);

        /* Sets the values for the View */
        if (feedItem != null)
            setItemView(viewHolder, feedItem);

        /* Provides a SwipeTouchListener for the View. */
        view.setOnTouchListener(getSwipeListener(parent, feedItem));

        return view;

    }

    /**
     * Sets the TextViews and BitMap for the FeedItem.
     *
     * If the Bitmap in the FeedItem is null, the 
     * ImageView-visibility is set to View.GONE.
     */
    private void setItemView(ViewHolder viewHolder, FeedItem feedItem) {

        viewHolder.mTitle.setText(feedItem.getTitle());
        viewHolder.mDescription.setText(feedItem.getDescription());

        if (feedItem.getImage() != null) {
            viewHolder.mImage.setVisibility(View.VISIBLE);
            viewHolder.mImage.setImageBitmap(feedItem.getImage());
        } else 
            viewHolder.mImage.setVisibility(View.GONE);

    }

    /**
     * Creates a SwipeTouchListener for the View.
     *
     * @param  parent The ListView that contains the View
     * @param  item   The FeedItem
     * @return        A new SwipeTouchListener
     */
    private SwipeTouchListener getSwipeListener(ViewGroup parent, final FeedItem feedItem) {

        return new SwipeTouchListener(mActivity, parent) {

            @Override
            public boolean onClick() {
                click(feedItem);
                return super.onClick();
            }

            @Override
            public void onSwipeLeft() {
                leftSwipe(feedItem); 
            }

            @Override
            public void onSwipeRight() {
                rightSwipe(feedItem);
            }  

        }; 

    }

    /**
     * Is called when the View is clicked.
     *
     * @param item The FeedItem clicked
     */
    private void click(FeedItem item) {

        FeedApplication app = (FeedApplication) mActivity.getApplication();

        if (!FeedItemPopup.isVisible())
            new FeedItemPopup(app, item).initView();

    }

    /**
     * Is called when the View is swiped to the left.
     *
     * @param item The FeedItem swiped
     */
    private void leftSwipe(FeedItem item) {

        mFeedList.remove(mFeedList.indexOf(item));
        notifyDataSetChanged();

        ((FeedApplication) mActivity.getApplication()).getFeed().readLater(item);

    }

    /**
     * Is called when the View is swiped to the right.
     *
     * @param item The FeedItem swiped
     */
    private void rightSwipe(FeedItem item) {

        mFeedList.remove(mFeedList.indexOf(item));
        notifyDataSetChanged();

        ((FeedApplication) mActivity.getApplication()).getFeed().markAsRead(item);

    }

    /**
     * Returns the adapters FeedList.
     *
     * @return mFeedList The FeedList with the FeedItems.
     */
    public FeedList getFeedList() {

        return mFeedList;

    }

}
