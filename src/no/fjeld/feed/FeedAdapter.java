package no.fjeld.feed;

import java.util.*;
import java.io.*;

import android.app.*;
import android.graphics.*;
import android.content.*;
import android.media.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

class FeedAdapter extends ArrayAdapter <FeedItem> {

    private Activity mActivity;
    private ArrayList <FeedItem> mFeedList;

    public FeedAdapter(Activity mActivity, int mResourceView, ArrayList <FeedItem> mFeedList) {

        super(mActivity, mResourceView, mFeedList);
        this.mActivity = mActivity;
        this.mFeedList = mFeedList;    

    }

    private class ViewHolder {

        TextView mTitle;
        TextView mDescription;
        ImageView mImage;

    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (view == null) {

            view = ((LayoutInflater) mActivity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item, null);

            mViewHolder = new ViewHolder();

            mViewHolder.mTitle = (TextView) view.findViewById(R.id.feed_title);
            mViewHolder.mDescription = (TextView) view.findViewById(R.id.feed_description);
            mViewHolder.mImage = (ImageView) view.findViewById(R.id.feed_image);

            view.setTag(mViewHolder);


        } else
            mViewHolder = (ViewHolder) view.getTag();

        FeedItem mFeedItem = mFeedList.get(position);

        if (mFeedItem != null)
            setItemView(mViewHolder, mFeedItem);

        return view;

    }

    /**
     * Sets the text and bitmaps for the FeedItem.
     *
     * If the Bitmap in the item is null, the 
     * ImageView-visibility is set to Gone.
     */
    private void setItemView(ViewHolder mViewHolder, FeedItem mFeedItem) {

        mViewHolder.mTitle.setText(mFeedItem.getTitle());
        mViewHolder.mDescription.setText(mFeedItem.getDescription());

        if (mFeedItem.getImage() != null) {
            mViewHolder.mImage.setVisibility(View.VISIBLE);
            mViewHolder.mImage.setImageBitmap(mFeedItem.getImage());
        } else 
            mViewHolder.mImage.setVisibility(View.GONE);

    }

    public ArrayList <FeedItem> getFeedList() {

        return mFeedList;

    }

}
