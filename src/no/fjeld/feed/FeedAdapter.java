package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

class FeedAdapter extends ArrayAdapter <FeedItem> {

    private Activity mActivity;
    private FeedList mFeedList;

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

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {

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

        if (feedItem != null)
            setItemView(viewHolder, feedItem);

        return view;

    }

    /**
     * Sets the text and bitmaps for the FeedItem.
     *
     * If the Bitmap in the item is null, the 
     * ImageView-visibility is set to Gone.
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

    public FeedList getFeedList() {

        return mFeedList;

    }

}
