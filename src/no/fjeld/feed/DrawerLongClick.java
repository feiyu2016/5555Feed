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
 */
public class DrawerLongClick implements OnItemLongClickListener {

    private static final int EDIT = 0;
    private static final int DELETE = 1;
    private static final int CANCEL = 2;

    private FeedApplication mApp;
    private DrawerAdapter mDrawerAdapter;
    private DrawerItem mDrawerItem;

    private View mView;
    private int mPosition;

    private TextView mDrawerText;
    private LinearLayout mDrawerOptions;
    private LinearLayout mEditItem;
    private LinearLayout mDeleteItem;
    private LinearLayout mCancel;

    private Animation mSlideIn;
    private Animation mSlideOut;

    private int action;

    public DrawerLongClick(FeedApplication app, DrawerAdapter drawerAdapter) {

        this.mApp = app;
        this.mDrawerAdapter = drawerAdapter;

    }

    @Override
    public boolean onItemLongClick(AdapterView <?> parent, View view,
            int position, long id) {

        this.mView = view;
        this.mPosition = position - 3;

        if (mPosition > -1 && mPosition < mDrawerAdapter.getDrawerList().size()) {

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

    public void closeLast() {

        mDrawerOptions.setVisibility(View.INVISIBLE);
        mDrawerText.setVisibility(View.VISIBLE);
        mDrawerText.startAnimation(AnimationUtils.loadAnimation(mApp.getFeedActivity().
                    getBaseContext(), R.anim.slide_in_left));

    }

    public void initAnims() {

        mSlideIn = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_in_left);
        mSlideOut = AnimationUtils.loadAnimation(mApp.getFeedActivity().
                getBaseContext(), R.anim.slide_out_left);

        mSlideIn.setAnimationListener(new SlideInListener());

    }

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
                editItem();
            }   
        }); 

        mDeleteItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View deleteView) {
                action = DELETE;
                deleteItem();
            }   
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View cancelView) {
                action = CANCEL;
                cancel();
            }
        }); 

    }

    public void editItem() {

        mDrawerText.startAnimation(mSlideIn);

    }

    public void deleteItem() {

        mDrawerText.startAnimation(mSlideIn);

    }

    public void cancel() {

        mDrawerText.startAnimation(mSlideIn);

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

            mDrawerItem = mDrawerAdapter.getDrawerList().get(mPosition);

            if (action == EDIT) {

                changeName();

            } else if (action == DELETE) { 

                mDrawerAdapter.getDrawerList().remove(mPosition);
                mDrawerAdapter.notifyDataSetChanged(); 

                mApp.getDatabase().delete("drawerItems", mDrawerItem.getUrl()); 

            }

        }


        private void changeName() {

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

                            mDrawerItem.setFeedName(input.getText().toString());
                            mDrawerAdapter.notifyDataSetChanged();

                            mApp.getDatabase().update(mDrawerItem);

                        }

                    });

            /* The "Cancel"-button */
            dialog.setNegativeButton(R.string.new_feed_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                        }
                    });

            dialog.show();

        }

    }

}
