package no.fjeld.feed;

import android.app.Activity;
import android.view.*;
import android.view.View.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class SlidingPopup {

    private final static int ANIMATION_TIME = 300;

    private static SlidingPopup sInstance;

    private Activity mActivity;
    
    private ViewGroup mParentView;
    private View mFadingView;
    private View mChildView;

    private TransitionDrawable mTransition;

    /**
     * Public constructor for SlidingPopup.
     *
     * @param activity   An Activity-pointer.
     * @param parentView The ViewGroup which will show the sliding View.
     * @param childView  The View that will slide down.
     */
    public SlidingPopup(Activity activity, ViewGroup parentView, 
            View childView) {

        mActivity = activity;

        mParentView = parentView;
        mChildView = childView;

        mTransition = getTransitionDrawable();

        mFadingView = new View(mActivity); 
        mFadingView.setBackground(mTransition);

        mParentView.addView(mFadingView);
        mParentView.addView(mChildView);

        sInstance = this;

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
