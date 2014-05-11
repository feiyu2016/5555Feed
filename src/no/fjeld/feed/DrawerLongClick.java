package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.view.animation.*;
import android.view.GestureDetector.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

/** 
 * The LongClickListener for the Navigation Drawers ListView.
 */
public class DrawerLongClick implements OnItemLongClickListener {

    private static final int EDIT = 0;
    private static final int DELETE = 1;

    private FeedApplication mApp;
    private DrawerAdapter mDrawerAdapter;

    private View view;
    private int position;

    private TextView mDrawerText;
    private LinearLayout mDrawerOptions;
    private LinearLayout editItem;
    private LinearLayout deleteItem;

    private Animation slideIn;
    private Animation slideOut;

    private int action;

    public DrawerLongClick(FeedApplication mApp, DrawerAdapter mDrawerAdapter) {

        this.mApp = mApp;
        this.mDrawerAdapter = mDrawerAdapter;

    }

    @Override
    public boolean onItemLongClick(AdapterView <?> parent, View v,
            int pos, long id) {

        this.view = v;
        this.position = pos - 3;

        if (position > -1 && position < mDrawerAdapter.getDrawerList().size()) {

            if(mDrawerText != null && mDrawerText.getVisibility() == View.INVISIBLE) 
                closeLast();

            initAnims();
            initViews();

            mDrawerOptions.setVisibility(View.VISIBLE);
            mDrawerText.setVisibility(View.INVISIBLE);
            mDrawerText.startAnimation(slideOut);

        }

        return true;

    }

    public void closeLast() {

        mDrawerOptions.setVisibility(View.INVISIBLE);
        mDrawerText.setVisibility(View.VISIBLE);
        mDrawerText.startAnimation(AnimationUtils.loadAnimation(mApp.getFeedActivity().
                    getBaseContext(), R.anim.slide_in_left));

    }

    public void initAnims() {

        slideIn = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_in_left);
        slideOut = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_out_left);

        slideIn.setAnimationListener(new SlideInListener());

    }

    public void initViews() {

        mDrawerText = (TextView) view.findViewById(
                R.id.drawer_item_text); 
        mDrawerOptions = (LinearLayout) view.findViewById(
                R.id.drawer_item_options);

        editItem = (LinearLayout) mDrawerOptions
            .findViewById(R.id.drawer_option_edit);
        deleteItem = (LinearLayout) mDrawerOptions
            .findViewById(R.id.drawer_option_delete);

        editItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View editView) {
                action = EDIT;
                editItem();
            }   
        }); 

        deleteItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View deleteView) {
                action = DELETE;
                deleteItem();
            }   
        }); 

    }

    public void editItem() {

        mDrawerText.startAnimation(slideIn);

    }

    public void deleteItem() {

        mDrawerText.startAnimation(slideIn);

    }

    /**
     * When the item slides back in to the drawer.
     */
    private class SlideInListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {

            mDrawerText.setVisibility(View.VISIBLE);
            mDrawerOptions.setVisibility(View.INVISIBLE);

            if (action == EDIT) {

                DrawerItem item = mDrawerAdapter.getDrawerList().get(position);
                item.setFeedName("New name");
                mDrawerAdapter.notifyDataSetChanged();

                mApp.getDatabase().update(item);

            } else if (action == DELETE) { 

                mDrawerAdapter.getDrawerList().remove(position);
                mDrawerAdapter.notifyDataSetChanged(); 

                //mApp.getDatabase().delete("drawerItems", 
                //        mDrawerAdapter.getDrawerList().get(position).getUrl());

            }

        }

    }

}
