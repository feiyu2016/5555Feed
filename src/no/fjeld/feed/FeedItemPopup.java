package no.fjeld.feed;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.preference.PreferenceManager;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.view.ViewGroup.*;
import android.widget.*;

public class FeedItemPopup {

    private final static int SHARE = 0;
    private final static int READ = 1;
    private int mAction;

    private static boolean sVisible;
    private static FeedItemPopup sInstance;

    private Activity mActivity;
    private FeedItem mFeedItem;

    private LinearLayout mPopupView;
    private TransitionDrawable mBackground;

    private ImageView mImage;
    private TextView mTitle;
    private TextView mDescription;

    public FeedItemPopup(Activity activity, FeedItem item) {

        mActivity = activity;
        mFeedItem = item;

        sInstance = this;

    }

    public static boolean isVisible() {

        return sVisible;

    }

    public static FeedItemPopup getInstance() {

        return sInstance;

    }

    public void initView() {

        mPopupView = (LinearLayout) mActivity.findViewById(
                R.id.popup_view); 
        mBackground = (TransitionDrawable) mActivity.findViewById(
                R.id.fading_background).getBackground();

        mImage = (ImageView) mPopupView.findViewById(R.id.popup_image);
        mTitle = (TextView) mPopupView.findViewById(R.id.popup_title);
        mDescription = (TextView) mPopupView.findViewById(R.id.popup_description);     

        setClickListeners();
        setContent();

        showView();


    }

    private void setContent() {

        mImage.setVisibility(View.VISIBLE);

        if (mFeedItem.getImage() != null)
            mImage.setImageBitmap(mFeedItem.getImage());
        else
            mImage.setVisibility(View.GONE);

        mTitle.setText(mFeedItem.getTitle());
        mDescription.setText(mFeedItem.getDescription());

        mDescription.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setClickListeners() {

        mPopupView.findViewById(R.id.popup_share).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAction = 0;
                        hideView();
                    }
                });

        mPopupView.findViewById(R.id.popup_read).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAction = 1;
                        hideView();
                    }
                });

        mPopupView.findViewById(R.id.dummy).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isVisible()) hideView(); 
                    }
                });

    }

    private void showView() {

        Animation anim = AnimationUtils.loadAnimation(
                mActivity, R.anim.slide_in_top);

        mPopupView.startAnimation(anim);
        mBackground.startTransition(300);

        mPopupView.setVisibility(View.VISIBLE);
        sVisible = true;

    }

    public void hideView() {

        Animation anim = AnimationUtils.loadAnimation(
                mActivity, R.anim.slide_out_top);
        anim.setAnimationListener(new SlideOutListener());

        mPopupView.startAnimation(anim);
        mBackground.reverseTransition(300);

        mPopupView.setVisibility(View.GONE);
        sVisible = false;

    }

    private void share() {

    }

    private void read() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
               mActivity); 

        if (prefs.getBoolean("preference_open_in", true)) {

            Intent appIntent = new Intent(mActivity,
                    WebViewActivity.class).putExtra("url", mFeedItem.getUrl());
            mActivity.startActivity(appIntent);

        } else {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse(mFeedItem.getUrl()));
            mActivity.startActivity(browserIntent);

        }

    }

    private class SlideOutListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {

            if (mAction == SHARE) 
                share();
            else if (mAction == READ) 
                read();

        }

    }

}
