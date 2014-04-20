package no.fjeld.feed;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;

/**
 * The DragListener that handles the events on a longclick
 * on a FeedItem.
 */
public class FeedDragListener implements View.OnDragListener {

    private View mView;
    private FeedApplication mApp;

    private FeedItem mFeedItem;

    private LinearLayout mPopupView;

    private TextView mReadNow;
    private TextView mReadLater;

    private Rect mReadNowRect;
    private Rect mReadLaterRect;

    private TransitionDrawable mTrans;

    private int x;
    private int y;

    public FeedDragListener(View mView, FeedApplication mApp) {
    
        this.mView = mView;   
        this.mApp = mApp;

    }

    @Override
    public boolean onDrag(View view, DragEvent event) {

        init(event);

        x = (int) event.getX();
        y = (int) event.getY();

        switch(event.getAction()) {

            /** The user starts the drag. */
            case DragEvent.ACTION_DRAG_STARTED:

                setView(View.VISIBLE, R.anim.slide_in_left);
                mTrans.startTransition(100);

                return true;

                /** The user drags across the screen. */
            case DragEvent.ACTION_DRAG_LOCATION:

                /* Read now */
                if(mReadNowRect.contains(x, y))
                    mReadNow.setBackgroundResource(R.color.accent_dark);
                else if(!mReadNowRect.contains(x, y))
                    mReadNow.setBackgroundResource(R.color.accent_light);

                /* Read later */
                if(mReadLaterRect.contains(x, y))
                    mReadLater.setBackgroundResource(R.color.accent_dark);
                else if(!mReadLaterRect.contains(x, y))
                    mReadLater.setBackgroundResource(R.color.accent_light);

                return true;

                /** The user releases the hold. */
            case DragEvent.ACTION_DROP:

                int position = mApp.getFeed().getFeedAdapter().
                    getFeedList().indexOf(mFeedItem) + 1;

                if (mReadNowRect.contains(x, y))
                    //readNow(position);
                if (mReadLaterRect.contains(x, y))
                    //readLater(position);
                
                return true;

                /** The DragEvent has ended. */
            case DragEvent.ACTION_DRAG_ENDED:

                setView(View.GONE, R.anim.slide_out_left);
                mTrans.reverseTransition(100);

                return true;

        }

        return false;

    }

    /**
     * Initializes the values and layouts for the 
     * DragListener.
     *
     * @param event The DragEvent needed to get the FeedItem-object.
     */
    private void init(DragEvent event) {

        mFeedItem = (FeedItem) event.getLocalState();
        mPopupView = (LinearLayout) mView.findViewById(R.id.popup_view);

        ImageView mImage = (ImageView) mView.findViewById(R.id.popup_image);

        if (mFeedItem.getImage() == null)
            mImage.setVisibility(View.GONE);
        else {
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageBitmap(mFeedItem.getImage());
        }

        ((TextView) mView.findViewById(R.id.popup_feed_title))
            .setText(mFeedItem.getTitle());
        ((TextView) mView.findViewById(R.id.popup_feed_description))
            .setText(mFeedItem.getDescription());

        mReadNow = (TextView) mView.findViewById(R.id.popup_read_now);
        mReadLater = (TextView) mView.findViewById(R.id.popup_read_later);

        mReadNowRect = new Rect(
                mReadNow.getLeft(), mReadNow.getTop(),
                mReadNow.getLeft() + mReadNow.getWidth(), 
                mReadNow.getTop() + mReadNow.getHeight());
        mReadLaterRect = new Rect(
                mReadLater.getLeft(), mReadLater.getTop(), 
                mReadLater.getLeft() + mReadLater.getWidth(), 
                mReadLater.getTop() + mReadLater.getHeight());

        mTrans = (TransitionDrawable) ((LinearLayout) 
                mView.findViewById(R.id.fading_background)).getBackground();

    }

    /**
     * Sets the PopupView-visibility.
     * An animation is also loaded for the View.
     *
     * @param visibility The visibility-value for the view.
     * @param anim       The anim-resource for the view.
     */
    private void setView(int visibility, int anim) {

        mPopupView.setVisibility(visibility);
        mPopupView.startAnimation(AnimationUtils.loadAnimation(
                    mApp.getFeedActivity().getBaseContext(), anim));

    }

}
