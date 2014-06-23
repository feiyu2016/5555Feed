package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

/** 
 * The OnItemLongClickListener for the Navigation Drawers ListView.
 * This will display the options for a DrawerItem which is either
 * to rename the title, or delete it.
 */
public class DrawerLongClick implements OnItemLongClickListener {

    /* Values to determine which action to take. */ 
    private static final int EDIT = 0;  
    private static final int DELETE = 1;
    private static final int CANCEL = 2;
    private int action;

    private Activity mActivity;
    private DrawerAdapter mDrawerAdapter;
    private DrawerItem mDrawerItem;

    private View mView;    // The view of the item clicked.
    private int mPosition; // The position of the item that was clicked.

    private TextView mDrawerText;
    private LinearLayout mDrawerOptions; // The layout with the options.
    private LinearLayout mEditItem;      // The edit-option layout.
    private LinearLayout mDeleteItem;    // The delete-option layout.
    private LinearLayout mCancel;        // The cancel-option layout.

    /* Animations for the view. */
    private Animation mSlideIn;
    private Animation mSlideOut;

    /**
     * Constructor for the class DrawerLongClick.
     *
     * @param activity      The main activity for this app.
     * @param drawerAdapter The adapter for the NavigationDrawer list.
     */
    public DrawerLongClick(Activity activity, DrawerAdapter drawerAdapter) {

        mActivity = activity;
        mDrawerAdapter = drawerAdapter;

    }

    @Override
    public boolean onItemLongClick(AdapterView <?> parent, View view,
            int position, long id) {

        mView = view;
        mPosition = position - 3;

        /* Checks if the position is a RSS-item, and not 'All feeds', or 'Saved items'. */
        if (mPosition > -1 && mPosition < mDrawerAdapter.getDrawerList().size()) {

            /* Means that another item has its options visible. */
            if(mDrawerText != null && mDrawerText.getVisibility() == View.INVISIBLE) 
                closeLast();

            initAnims();
            initViews();

            mDrawerOptions.setVisibility(View.VISIBLE);
            mDrawerText.setVisibility(View.INVISIBLE);
            mDrawerText.startAnimation(mSlideOut);

        }

        return true;

    }

    /**
     * Hides the options for the current item.
     */
    private void close() {

        mDrawerText.startAnimation(mSlideIn);

    }

    /** 
     * Hides the options of the previous clicked item.
     */
    private void closeLast() {

        mDrawerOptions.setVisibility(View.INVISIBLE);
        mDrawerText.setVisibility(View.VISIBLE);
        mDrawerText.startAnimation(AnimationUtils.loadAnimation(mActivity.
                    getBaseContext(), R.anim.slide_in_left));

    }

    /**
     * Initializes the animations to be used for the DrawerItem-view.
     */
    private void initAnims() {

        mSlideIn = AnimationUtils.loadAnimation(mActivity.
                getBaseContext(), R.anim.slide_in_left);
        mSlideOut = AnimationUtils.loadAnimation(mActivity.
                getBaseContext(), R.anim.slide_out_left);

        mSlideIn.setAnimationListener(new SlideInListener());

    }

    /**
     * Initializes the option-views and gives them OnClickListeners.
     */
    private void initViews() {

        mDrawerText = (TextView) mView.findViewById(
                R.id.drawer_item_text); 
        mDrawerOptions = (LinearLayout) mView.findViewById(
                R.id.drawer_item_options);

        mEditItem = (LinearLayout) mDrawerOptions
            .findViewById(R.id.drawer_option_edit);
        mDeleteItem = (LinearLayout) mDrawerOptions
            .findViewById(R.id.drawer_option_delete);
        mCancel = (LinearLayout) mDrawerOptions
            .findViewById(R.id.drawer_option_cancel);

        mEditItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View editView) {
                action = EDIT;
                close();
            }   
        }); 

        mDeleteItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View deleteView) {
                action = DELETE;
                close(); 
            }   
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View cancelView) {
                action = CANCEL;
                close(); 
            }
        }); 

    }

    /**
     * AnimationListener that takes care of what to do when the user
     * has made a choice, and the animation on the options is done. 
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

            mDrawerItem = mDrawerAdapter.getDrawerList().get(mPosition);

            if (action == EDIT) {

                changeName();

            } else if (action == DELETE) { 

                mDrawerAdapter.getDrawerList().remove(mPosition);
                mDrawerAdapter.notifyDataSetChanged(); 

                /* Deletes the DrawerItem from the database. */
                ((FeedApplication)mActivity.getApplication())
                        .getDatabase().delete("drawerItems", mDrawerItem.getUrl()); 

            }

        }

        /**
         * Displays an AlertDialog to the user which takes text as input,
         * which is then used to set the new title for a DrawerItem.
         */
        private void changeName() {

            /* Some 'final'-fields necessary for inner class access. */
            final EditText input = new EditText(mActivity);
            input.setHint(mDrawerItem.getFeedName());

            /* Shows the keyboard */
            input.postDelayed(new Runnable() {

                @Override
                public void run() {
                    ((InputMethodManager) mActivity.getSystemService(
                        Context.INPUT_METHOD_SERVICE)).showSoftInput(input, 0);
                }

            }, 50); 

            /* The dialog */
            AlertDialog.Builder dialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(mActivity, R.style.DefaultTheme));
            dialog.setTitle(R.string.drawer_item_change_title);
            dialog.setView(input);

            /* The "OK"-button */
            dialog.setPositiveButton(R.string.new_feed_ok,
                    new DialogInterface.OnClickListener() {

                        @Override 
                        public void onClick(DialogInterface dialog, int button) {

                            /* Updates the title for the DrawerItem. */
                            mDrawerItem.setFeedName(input.getText().toString());
                            mDrawerAdapter.notifyDataSetChanged();

                            /* Updates the title for the DrawerItem. */
                            ((FeedApplication)mActivity.getApplication())
                                     .getDatabase().update(mDrawerItem);

                        }

                    });

            /* The "Cancel"-button */
            dialog.setNegativeButton(R.string.new_feed_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {}
                    });

            dialog.show();

        }

    }

}
