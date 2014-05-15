package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

/** 
 * The LongClickListener for the Navigation Drawers ListView.
 * This will display the options for a DrawerItem which is either
 * to rename the title, or delete it.
 */
public class DrawerLongClick implements OnItemLongClickListener {

    /* Values to determine which action to take. */ 
    private static final int EDIT = 0;  
    private static final int DELETE = 1;
    private int action;

    private FeedApplication mApp;
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
     * @param app           The app-pointer used to access global pointers.
     * @param drawerAdapter The adapter for the NavigationDrawer list.
     */
    public DrawerLongClick(FeedApplication app, DrawerAdapter drawerAdapter) {

        this.mApp = app;
        this.mDrawerAdapter = drawerAdapter;

    }

    @Override
    public boolean onItemLongClick(AdapterView <?> parent, View view,
            int position, long id) {

        this.mView = view;
        this.mPosition = position - 3;

        /* Checks if the position is a RSS-item, and not just 'All feeds', or 'Saved items'. */
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
    public void closeLast() {

        mDrawerOptions.setVisibility(View.INVISIBLE);
        mDrawerText.setVisibility(View.VISIBLE);
        mDrawerText.startAnimation(AnimationUtils.loadAnimation(mApp.getFeedActivity().
                    getBaseContext(), R.anim.slide_in_left));

    }

    /**
     * Initializes the animations to be used for the DrawerItem-view.
     */
    public void initAnims() {

        mSlideIn = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_in_left);
        mSlideOut = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_out_left);

        mSlideIn.setAnimationListener(new SlideInListener());

    }

    /**
     * Initializes the option-views and gives them OnClickListeners.
     */
    public void initViews() {

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
                close(); 
            }
        }); 

    }

    /**
     * Inner class that takes care of the actions when the options
     * for the item is hidden.
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
                mApp.getDatabase().delete("drawerItems", mDrawerItem.getUrl()); 

            }

        }

        /**
         * Displays an AlertDialog to the user which takes text as input,
         * which is then used to set the new title for a DrawerItem.
         */
        private void changeName() {

            /* Some 'final'-fields necessary for inner class access. */
            final Activity activity = mApp.getFeedActivity();
            final EditText input = new EditText(activity);
            input.setHint(mDrawerItem.getFeedName());

            /* Shows the keyboard */
            input.postDelayed(new Runnable() {

                @Override
                public void run() {
                    ((InputMethodManager) activity.getSystemService(
                        Context.INPUT_METHOD_SERVICE)).showSoftInput(input, 0);
                }

            }, 50); 

            /* The dialog */
            AlertDialog.Builder dialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(activity, R.style.DefaultTheme));
            dialog.setTitle(R.string.drawer_item_change_title);
            dialog.setView(input);

            /* The "OK"-button */
            dialog.setPositiveButton(R.string.new_feed_ok,
                    new DialogInterface.OnClickListener() {

                        @Override 
                        public void onClick(DialogInterface dialog, int button) {

                            /* Updates the title for the DrawerItem. *//
                            mDrawerItem.setFeedName(input.getText().toString());
                            mDrawerAdapter.notifyDataSetChanged();

                            /* Updates the title for the DrawerItem. *//
                            mApp.getDatabase().update(mDrawerItem);

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
