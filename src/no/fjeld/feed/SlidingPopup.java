package no.fjeld.feed;

import android.app.Activity;
import android.view.*;
import android.view.View.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

/**
 * Applies a background that fades into a darker color while
 * sliding a View down from the top of the screen.
 * 
 * A click on the background will slide the View out of the
 * screen again. If the backbutton should be used for the 
 * same purpose, put the following in the activitiy that 
 * uses the View:
 *
 * @Override
 * public void onBackPressed() {
 *     if (SlidingPopup.getInstance() != null)
 *         SlidingPopup.getInstance().slideOut();
 *     else
 *         super.onBackPressed();
 * }
 *
 * If any functionality is desired when the View has slided
 * out of the screen, override 'onSlideOutFinished()'.
 */
public class SlidingPopup {

    private final static int ANIMATION_TIME = 300;

    private static SlidingPopup sInstance;
    private TransitionDrawable mTransition;

    private Activity mActivity;
    
    private ViewGroup mParentView;
    private View mChildView;
    private View mFadingView;

    /**
     * Public constructor for SlidingPopup.
     *
     * @param activity   An Activity-pointer.
     * @param parentView The ViewGroup which will show the sliding View.
     * @param childView  The View that will slide down.
     */
    public SlidingPopup(Activity activity, ViewGroup parentView, 
            View childView) {

        sInstance = this;
        mTransition = getTransitionDrawable();

        mActivity = activity;

        mParentView = parentView;
        mChildView = childView;
        mFadingView = new View(mActivity); 
        mFadingView.setBackground(mTransition);

        mParentView.addView(mFadingView);
        mParentView.addView(mChildView);

        setCancelListener();
        slideIn();

    } 

    /**
     * Starts the fading (transition) of the background
     * and animates mChildView (sliding in).
     */
    private void slideIn() {

        Animation anim = AnimationUtils.loadAnimation(
                mActivity, R.anim.slide_in_top); 

        mChildView.startAnimation(anim);
        mTransition.startTransition(ANIMATION_TIME);

    }

    /**
     * Reverse the fading (transition) of the background
     * and animates mChildView (sliding out).
     *
     * Has a public modifier in case it needs to be accessed
     * from other classes.
     */
    public void slideOut() {

        Animation anim = AnimationUtils.loadAnimation(
                mActivity, R.anim.slide_out_top); 
        anim.setAnimationListener(new SlideOutListener());

        mChildView.startAnimation(anim);
        mTransition.reverseTransition(ANIMATION_TIME);

    }

    /**
     * Creates a new TransitionDrawable for use as a fading 
     * background when a View slides down from the top of 
     * the screen.
     *
     * @return A new TransitionDrawable.
     */
    private TransitionDrawable getTransitionDrawable() {

        ColorDrawable layerOne = new ColorDrawable(
                android.R.color.transparent);
        ColorDrawable layerTwo = new ColorDrawable(
                Color.parseColor("#99000000"));

        return new TransitionDrawable(new Drawable [] {
                layerOne, layerTwo
        });

    }

    /**
     * Sets an OnTouchListener on mFadingView that cancels
     * the View when a TouchEvent has occured on the background.
     */
    private void setCancelListener() {

        mFadingView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                slideOut();
            }
        });

    }

    /** 
     * Returns the current instance of the visible View.
     */
    public static SlidingPopup getInstance() {
        return sInstance;
    }

    /**
     * Is ment to be overridden if any desired functionality
     * is wanted when the View has slided out of the screen.
     */
    public void onSlideOutFinished() {}

    /**
     * AnimationListener for the animation that slides the
     * View out of the screen.
     */
    private class SlideOutListener implements AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

            mParentView.removeView(mFadingView);
            mParentView.removeView(mChildView);
            sInstance = null;

            onSlideOutFinished();

        }

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationStart(Animation animation) {}

    }

}
