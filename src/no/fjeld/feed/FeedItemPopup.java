package no.fjeld.feed;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
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

    private static boolean sVisible;
    private static FeedItemPopup sInstance;

    private FeedApplication mApp;
    private FeedItem mFeedItem;

    private LinearLayout mPopupView;
    private TransitionDrawable mBackground;

    private ImageView mImage;
    private TextView mTitle;
    private TextView mDescription;

    public FeedItemPopup(FeedApplication app, FeedItem item) {

        this.mApp = app;
        this.mFeedItem = item;

        sInstance = this;

    }

    public void initView() {

        mPopupView = (LinearLayout) mApp.getFeedActivity().findViewById(
                R.id.popup_view); 
        mBackground = (TransitionDrawable) mApp.getFeedActivity().findViewById(
                R.id.fading_background).getBackground();

        mImage = (ImageView) mPopupView.findViewById(R.id.popup_image);
        mTitle = (TextView) mPopupView.findViewById(R.id.popup_title);
        mDescription = (TextView) mPopupView.findViewById(R.id.popup_description);     

        setClickListeners();
        setContent();

        mPopupView.setVisibility(View.VISIBLE);
        mPopupView.startAnimation(AnimationUtils.loadAnimation(
                    mApp.getFeedActivity(), R.anim.slide_in_top));

        mBackground.startTransition(150); 
       
        mApp.getFeed().getFeedListView().setEnabled(false); 
        sVisible = true;

    }

    private void setClickListeners() {

        mPopupView.findViewById(R.id.popup_read).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        read();
                    }
                });

        mPopupView.findViewById(R.id.popup_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        done();
                    }
                });

        mPopupView.findViewById(R.id.dummy).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        done();
                    }
                });
                    

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

    private void read() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                mApp.getFeedActivity());

        if (prefs.getBoolean("preference_open_in", true)) {

            Intent appIntent = new Intent(mApp.getFeedActivity(), 
                    WebViewActivity.class).putExtra("url", mFeedItem.getUrl());
            mApp.getFeedActivity().startActivity(appIntent);

        } else {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse(mFeedItem.getUrl()));
            mApp.getFeedActivity().startActivity(browserIntent);

        }
    }

    public void done() {

        mPopupView.setVisibility(View.GONE);
        mPopupView.startAnimation(AnimationUtils.loadAnimation(
                    mApp.getFeedActivity(), R.anim.slide_out_top));

        mBackground.reverseTransition(150);

        mApp.getFeed().getFeedListView().setEnabled(true); 
        sVisible = false;

    }

    public static boolean isVisible() {

        return sVisible;

    }

    public static FeedItemPopup getInstance() {
            
        return sInstance;

    }

}
