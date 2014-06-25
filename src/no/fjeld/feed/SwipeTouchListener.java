package no.fjeld.feed;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * An extended OnTouchListener that allows swiping of a View.
 *
 * onClick(), onSwipeLeft() and onSwipeRight() must be overridden
 * in order provide desired functionality after a swipe or touch.
 */
public class SwipeTouchListener implements OnTouchListener {

    private static final int SWIPE_DURATION = 300;
    private static final int SWIPE_VELOCITY_MIN = 75;

    private Activity mActivity;
    private ViewGroup mViewParent;
    private View mView;

    private VelocityTracker mVelocityTracker;

    private boolean mViewPressed = false;
    private boolean mSwiping = false;

    private float mDownX;
    private int mSwipeSlop;

    /**
     * Public constructor for the class SwipeTouchListener.
     *
     * @param activity   An Activity-pointer. 
     * @param viewParent The ViewGroup that contains the View. 
     */
    public SwipeTouchListener(Activity activity, ViewGroup 
            viewParent) {  

        mActivity = activity;
        mViewParent = viewParent;

    }

    /**
     * Is called when the View has recieved a touch event. 
     * Contains a switch() that controls what should happen 
     * when the user performs certain actions.
     *
     * @param view  The View that recieved the touch event
     * @param event A MotionEvent-object containing info about
     *              the event 
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        mSwipeSlop = ViewConfiguration
                .get(mActivity).getScaledTouchSlop();

        switch (event.getAction()) {

            /* The View has received its first TouchEvent. */ 
            case MotionEvent.ACTION_DOWN:
                if (mViewPressed)
                    return false;
                mViewPressed = true;

                actionDown(view, event);
                break;

            /* The View has been dragged to a location. */
            case MotionEvent.ACTION_MOVE: 
                actionMove(event);
                break;

            /* The hold on the View has been released. */ 
            case MotionEvent.ACTION_UP: 
                mViewPressed = false;

                if (mSwiping)
                    onSwipe(event);
                else 
                    return onClick(); 
                break;

            /* The hold on the View has been cancelled. */ 
            case MotionEvent.ACTION_CANCEL:
                mViewPressed = false;
                mVelocityTracker = null;

                view.setAlpha(1);
                view.setTranslationX(0);
                break;

        }

        return true;

    }

    private void actionDown(View view, MotionEvent event) {

        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        else
            mVelocityTracker.clear();

        mVelocityTracker.addMovement(event);

        mView = view;
        mDownX = event.getX();

    }

    /**
     * Is called from 'onTouch()' when the View has been dragged 
     * to a location. 
     * 
     * @param event The MotionEvent-object from onTouch()
     */
    private void actionMove(MotionEvent event) {

        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(100);

        float currentX = event.getX() + mView.getTranslationX();
        float deltaX   = currentX - mDownX;

        if (!mSwiping) {
            if (Math.abs(deltaX) > mSwipeSlop) {
                mSwiping = true;
                mViewParent.requestDisallowInterceptTouchEvent(true);
            }
        }

        if (mSwiping) {
            mView.setTranslationX(currentX - mDownX);
            mView.setAlpha(1 - Math.abs(deltaX) / mView.getWidth());
        }

    }

    /**
     * Is called from 'onTouch()' when the View has been released
     * and a swipe-gesture has been performed on the View. 
     * 
     * @param event The MotionEvent-object from onTouch() 
     */
    private void onSwipe(MotionEvent event) {

        float currentX  = event.getX() + mView.getTranslationX();
        float deltaX    = currentX - mDownX;
        float velocityX = Math.abs(mVelocityTracker.getXVelocity());

        float outOfView, endX, endAlpha;
        boolean dismiss = false;

        /* The user has performed a swipe-gesture at the specified 
         * velocity, or over a third of the Views width has been 
         * dragged out of its initial position. */
        if (velocityX > SWIPE_VELOCITY_MIN || Math.abs(deltaX) 
                > mView.getWidth() / 3) {

            outOfView = Math.abs(deltaX) / mView.getWidth();
            endX      = (deltaX < 0)
                        ? -mView.getWidth() 
                        : mView.getWidth();
            endAlpha  = 0;
            dismiss   = true;

        /* The user has released the hold on the View before it 
         * should be dismissed. */
        } else {

            outOfView = 1 - (Math.abs(deltaX) / mView.getWidth());
            endX      = 0;
            endAlpha  = 1;

        }

        mViewParent.setEnabled(false);

        long duration = (int) ((1 - outOfView) * SWIPE_DURATION);
        endSwipe(duration, endAlpha, endX, dismiss);

    }

    private void endSwipe(long duration, float endAlpha, final float 
            fEndX, final boolean fDismiss) {

        mView.animate().setDuration(duration)
                .alpha(endAlpha).translationX(fEndX)
                .setListener(new AnimatorListener() {

                @Override
                public void onAnimationEnd(Animator anim) {

                    mView.setAlpha(1);
                    mView.setTranslationX(0);
                    mSwiping = false;
                    mViewParent.setEnabled(true);

                    if (fDismiss) {
                        if (fEndX < 0) onSwipeLeft();
                        else           onSwipeRight();
                    }
                
                }
            
                @Override
                public void onAnimationCancel(Animator anim) {}

                @Override
                public void onAnimationRepeat(Animator anim) {}

                @Override
                public void onAnimationStart(Animator anim) {}

            });

    }

    /** 
     * Is called when the user has performed a single click on the View. 
     */
    public boolean onClick() { return true; }

    /** 
     * Is called when the user has performed a left swipe on the View. 
     */
    public void onSwipeLeft() {}

    /** 
     * Is called when the user has performed a right swipe on the View. 
     */
    public void onSwipeRight() {}

}
