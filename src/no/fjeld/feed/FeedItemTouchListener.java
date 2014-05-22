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
    private static final int MOVE_DURATION = 150;

    private FeedApplication mApp;
    private FeedItem mFeedItem;

    private ListView mListView;

    private int mSwipeSlop = -1;
    private float mDownX;

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

                mDownX = event.getX();

                if (mItemPressed)
                    return false;

                mItemPressed = true;

                break;

            case MotionEvent.ACTION_CANCEL:

                v.setAlpha(1);
                v.setTranslationX(0);

                mItemPressed = false;

                break;

            case MotionEvent.ACTION_MOVE: 

                actionMove(v, event);
                break;

            case MotionEvent.ACTION_UP: 

                actionUp(v, event);
                break;

        }

        return true;

    }

    private void actionMove(View v, MotionEvent event) {

        float x = event.getX() + v.getTranslationX();
        float deltaX = x - mDownX;
        float deltaXAbs = Math.abs(deltaX); 

        if (!mSwiping) {
            if (deltaXAbs > mSwipeSlop) {
                mSwiping = true;
                mListView.requestDisallowInterceptTouchEvent(true);
            }
        }

        if (mSwiping) {
            v.setTranslationX(x - mDownX);
            v.setAlpha(1 - deltaXAbs / v.getWidth());
        }

    }

    private void actionUp(View v, MotionEvent event) {

        if (mSwiping) {

            float x = event.getX() + v.getTranslationX();
            float deltaX = x - mDownX;
            float deltaXAbs = Math.abs(deltaX);

            float fractionCovered, endX, endAlpha;
            final boolean remove;

            if (deltaXAbs > v.getWidth() / 4) {

                fractionCovered = deltaXAbs / v.getWidth();
                endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                endAlpha = 0;
                remove = true;

            } else {

                fractionCovered = 1 - (deltaXAbs / v.getWidth());
                endX = 0;
                endAlpha = 1;
                remove = false;

            }

            long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
            mListView.setEnabled(false);

            v.animate().setDuration(duration)
                .alpha(endAlpha).translationX(endX)
                .setListener(new SlideOutListener(v, remove, (int) endX));

        }

        mItemPressed = false;

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
                adapter.getFeedList().remove(adapter.getFeedList().indexOf(mFeedItem));
                adapter.notifyDataSetChanged();

                if (mEndX < 0)
                    mApp.getFeed().readLater(mFeedItem);
                else
                    mApp.getFeed().markAsRead(mFeedItem);

            }

            mView.setAlpha(1);
            mView.setTranslationX(0);

            mSwiping = false;
            mListView.setEnabled(true);

        }

        @Override
        public void onAnimationCancel(Animator anim) {}

    }

}
