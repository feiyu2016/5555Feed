package no.fjeld.feed;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.view.ViewGroup.*;
import android.widget.*;

public class FeedItemTouchListener implements View.OnTouchListener {

    private static final int SWIPE_DURATION = 250;
    private static final int SWIPE_DISTANCE_MIN = 100;
    private static final int SWIPE_VELOCITY_MIN = 100;

    private FeedApplication mApp;
    private FeedItem mFeedItem;

    private ListView mListView;

    private int mSwipeSlop = -1;
    private float mDownX;

    private VelocityTracker mVelocityTracker;

    private boolean mItemPressed = false;
    private boolean mSwiping = false;

    public FeedItemTouchListener(FeedApplication app, FeedItem item) {

        this.mApp = app;
        this.mFeedItem = item;
        this.mListView = app.getFeed().getFeedListView();

    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        if (mSwipeSlop < 0) 
            mSwipeSlop = ViewConfiguration.get(mApp.getFeedActivity())
                .getScaledTouchSlop();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (mVelocityTracker == null)
                    mVelocityTracker = VelocityTracker.obtain();
                else
                    mVelocityTracker.clear();

                mVelocityTracker.addMovement(event);

                mDownX = event.getX();

                if (mItemPressed)
                    return false;

                mItemPressed = true;

                break;

            case MotionEvent.ACTION_CANCEL:

                mVelocityTracker = null;

                v.setAlpha(1);
                v.setTranslationX(0);

                mItemPressed = false;

                break;

            case MotionEvent.ACTION_MOVE: 

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);

                actionMove(v, event);

                break;

            case MotionEvent.ACTION_UP: 

                mItemPressed = false;
                
                if (mSwiping)
                    actionUp(v, event);
                else 
                    new FeedItemPopup(mApp, mFeedItem).initView();

                break;

        }

        return true;

    }

    private void actionMove(View v, MotionEvent event) {

        float x = event.getX() + v.getTranslationX();
        float deltaX = x - mDownX;

        if (!mSwiping) {
            if (Math.abs(deltaX) > mSwipeSlop) {
                mSwiping = true;
                mListView.requestDisallowInterceptTouchEvent(true);
                mApp.getSwipeRefresh().getSwipeLayout().setEnabled(false);
            }
        }

        if (mSwiping) {
            v.setTranslationX(x - mDownX);
            v.setAlpha(1 - Math.abs(deltaX) / v.getWidth());
        }

    }

    private void actionUp(View v, MotionEvent event) {


        float x = event.getX() + v.getTranslationX();
        float deltaX = x - mDownX;
        float velocityX = Math.abs(mVelocityTracker.getXVelocity());

        float fractionCovered, endX, endAlpha;
        final boolean remove;

        if (velocityX > SWIPE_VELOCITY_MIN || Math.abs(deltaX) > v.getWidth() / 3) {

            fractionCovered = Math.abs(deltaX) / v.getWidth();
            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
            endAlpha = 0;
            remove = true;

        } else {

            fractionCovered = 1 - (Math.abs(deltaX) / v.getWidth());
            endX = 0;
            endAlpha = 1;
            remove = false;

        }

        mListView.setEnabled(false);

        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);

        v.animate().setDuration(duration)
            .alpha(endAlpha).translationX(endX)
            .setListener(new SlideOutListener(v, remove, (int) endX));


    }

    private class SlideOutListener implements Animator.AnimatorListener {

        private View mView;
        private boolean mRemove;
        private int mEndX;

        public SlideOutListener(View v, boolean remove, int endX) {

            this.mView = v;
            this.mRemove = remove;
            this.mEndX = endX;

        }

        @Override
        public void onAnimationStart(Animator anim) {}

        @Override
        public void onAnimationRepeat(Animator anim) {}

        @Override
        public void onAnimationEnd(Animator anim) {

            if (mRemove) { 

                FeedAdapter adapter = mApp.getFeed().getFeedAdapter();

                int position = adapter.getFeedList().indexOf(mFeedItem);

                if (position != -1) {
                    adapter.getFeedList().remove(position);
                    adapter.notifyDataSetChanged();
                }

                if (mEndX < 0)
                    mApp.getFeed().readLater(mFeedItem);
                else
                    mApp.getFeed().markAsRead(mFeedItem);

            }

            mView.setAlpha(1);
            mView.setTranslationX(0);

            mSwiping = false;
            mListView.setEnabled(true);
            mApp.getSwipeRefresh().getSwipeLayout().setEnabled(true);

        }

        @Override
        public void onAnimationCancel(Animator anim) {}

    }

}
