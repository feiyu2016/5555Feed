package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

class FeedAdapter extends ArrayAdapter <FeedItem> {

    private Activity mActivity;
    private FeedList mFeedList;

    /**
     * Constructor for the class FeedAdapter.
     *
     * @param activity     FeedActivity-pointer.
     * @param resourceView The layout-id for the FeedItem.
     * @param drawerList   The list which contains FeedItems.
     */
    public FeedAdapter(Activity activity, int resourceView, FeedList feedList) {

        super(activity, resourceView, feedList);
        this.mActivity = activity;
        this.mFeedList = feedList;    

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

        /* Sets the values for the views in the FeedItem-layout. */
        if (feedItem != null)
            setItemView(viewHolder, feedItem);

        setSwipeListener(view, parent, feedItem);

        return view;

    }

    /**
     * Sets the text and bitmaps for the FeedItem.
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

    public void setSwipeListener(View view, ViewGroup parent, final FeedItem item) {

        view.setOnTouchListener(new SwipeTouchListener(mActivity, parent) {

            @Override
            public boolean onClick() {
                click(item);
                return super.onClick();
            }

            @Override
            public void onSwipeLeft() {
                leftSwipe(item); 
            }

            @Override
            public void onSwipeRight() {
                rightSwipe(item);
            }  

        }); 

    }

    private void click(FeedItem item) {

        FeedApplication app = (FeedApplication) mActivity.getApplication();

        if (!FeedItemPopup.isVisible())
            new FeedItemPopup(app, item).initView();

    }

    public void leftSwipe(FeedItem item) {

        mFeedList.remove(mFeedList.indexOf(item));
        notifyDataSetChanged();

        ((FeedApplication) mActivity.getApplication()).getFeed().readLater(item);

    }

    public void rightSwipe(FeedItem item) {

        mFeedList.remove(mFeedList.indexOf(item));
        notifyDataSetChanged();

        ((FeedApplication) mActivity.getApplication()).getFeed().markAsRead(item);

    }

    /**
     * Returns the adapters ArrayList.
     *
     * @return mFeedList The ArrayList with the FeedItems.
     */
    public FeedList getFeedList() {

        return mFeedList;

    }

}
