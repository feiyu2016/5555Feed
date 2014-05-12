package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class NavigationDrawer {

    private View mView;
    private FeedApplication mApp;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private DrawerAdapter mDrawerAdapter;

    /**
     * Constructor for the class NavgigationDrawer.
     *
     * @param view  The FeedActivity content view.
     * @param app  The Application-object for this app.
     */
    public NavigationDrawer(View view, FeedApplication app) {

        this.mView = view;
        this.mApp = app;

        initDrawerLayout();
        initDrawerToggle();
        initDrawerAdapter();
        initDrawerListView();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerListView.setAdapter(mDrawerAdapter);

        app.getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Initializes the DrawerLayout.
     */
    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) mView.findViewById(R.id.drawer_layout);

    }

    /**
     * Initializes the DrawerLayouts DrawerToggle.
     */
    private void initDrawerToggle() {

        mDrawerToggle = new ActionBarDrawerToggle(mApp.getFeedActivity(), mDrawerLayout, 
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_closed) {

            public void onDrawerClosed(View mainView) {

                DrawerItem drawerItem = mApp.getFeed().lastDrawerItem;

                if (drawerItem != null)
                    mApp.getActionBar().setSubtitle(drawerItem.getFeedName());

                mApp.getFeedActivity().invalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {

                mApp.getActionBar().setSubtitle(R.string.drawer_open);
                mApp.getFeedActivity().invalidateOptionsMenu();

            }

        };

    }

    /**
     * Initializes the DrawerLayouts ListView.
     */ 
    private void initDrawerListView() {

        mDrawerListView = (ListView) mView.findViewById(R.id.drawer_list);
        addViews(); 

        mDrawerListView.setOnItemClickListener(new DrawerClickListener());
        mDrawerListView.setOnItemLongClickListener(new DrawerLongClick(mApp, mDrawerAdapter));

    }

    /**
     * Adds header- and footer-views to the ListView in the
     * NavigationDrawer.
     */
    private void addViews() {

        /* Headers */
        mDrawerListView.addHeaderView(getItemView(
                    R.id.drawer_item_text,
                    R.string.drawer_header_all_feeds));

        mDrawerListView.addHeaderView(getItemView(
                    R.id.drawer_item_text,
                    R.string.drawer_header_saved_items));

        mDrawerListView.addHeaderView(getView(R.layout.drawer_group,
                    R.id.drawer_group_text,
                    R.string.drawer_group_subfeeds, 0));

        /* Footers */
        mDrawerListView.addFooterView(((LayoutInflater) mApp.getFeedActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.drawer_footer_divider, null));

        mDrawerListView.addFooterView(getView(R.layout.drawer_footer,
                    R.id.drawer_footer_text,
                    R.string.drawer_footer_preferences,
                    R.drawable.ic_icon_settings));

        mDrawerListView.addFooterView(getView(R.layout.drawer_footer,
                    R.id.drawer_footer_text,
                    R.string.drawer_footer_about,
                    R.drawable.ic_icon_about));

    } 


    private FrameLayout getItemView(int textResId, int stringResId) {

        LayoutInflater inflater = (LayoutInflater) mApp.getFeedActivity()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout item = (FrameLayout) inflater.inflate(
                R.layout.drawer_item, null);

        TextView itemText = (TextView) item.findViewById(textResId);
        itemText.setText(mApp.getFeedActivity().getResources()
                .getString(stringResId));

        return item;

    }

    /**
     * Returns an inflated LinearLayout with the correct content
     * according to the parameters.
     *
     * @param  layoutResId The layout-xml to be inflated.
     * @param  textResId   The TextView to be set.
     * @param  stringResId The String-resource for the TextView.
     * @param  imgResId    The Drawable-resource for the layout.
     *
     * @return mListItem    The new LinearLayout.
     */
    private LinearLayout getView(int layoutResId, int textResId, 
            int stringResId, int imgResId) {

        LayoutInflater inflater = (LayoutInflater) mApp.getFeedActivity()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout item = (LinearLayout) inflater.inflate(
                layoutResId, null);

        TextView itemText = (TextView) item.findViewById(textResId);
        itemText.setText(mApp.getFeedActivity().getResources()
                .getString(stringResId));

        if (imgResId != 0) {

            ImageView itemImage = (ImageView) item.findViewById(
                    R.id.drawer_footer_image);
            itemImage.setImageResource(imgResId);

        }

        /* This is the case where we add the "Add Feed"-button */
        if (stringResId == R.string.drawer_group_subfeeds) {

            ((LinearLayout) item.findViewById(R.id.drawer_group_new_feed))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newFeed();
                    }
                });

        }

        return item;

    }

    /**
     * Initializes the ListViews adapter.
     */
    private void initDrawerAdapter() {

        mDrawerAdapter = new DrawerAdapter(
                mApp.getFeedActivity(), R.layout.drawer_item, 
                        mApp.getDatabase().getDrawerItems());

    }

    /**
     * Returns the DrawerLayout for the NavDrawer.
     */
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    /**
     * Returns the DrawerLayouts DrawerToggle.
     */
    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    /**
     * Returns the ListViews adapter.
     */
    public DrawerAdapter getDrawerAdapter() {
        return mDrawerAdapter;
    }

    /**
     * Gets called from the OnItemClickListener when the user has 
     * clicked one of the items in the drawer.
     */
    private void drawerItemClicked(int position) {

        int mDrawerSize = mDrawerListView.getChildCount();

        if (position == 0) {                        // All feeds

            mDrawerLayout.closeDrawer(Gravity.LEFT);
            mApp.getFeed().allFeeds();

        } else if (position == 1) {                 // Saved items 

            mDrawerLayout.closeDrawer(Gravity.LEFT);
            mApp.getFeed().savedFeeds();

        } else if (position == mDrawerSize - 2) {   // Preferences

            mApp.getFeedActivity().startActivity(new Intent(
                        mApp.getFeedActivity(), PreferencesActivity.class));

        } else if (position == mDrawerSize - 1) {   // About


        } else if (position > 2 && position < mDrawerSize - 2) { // Feed

            mDrawerLayout.closeDrawer(Gravity.LEFT);

            DrawerItem drawerItem = mDrawerAdapter
                .getDrawerList().get(position - 3);

            mApp.getFeed().loadFeed(drawerItem, false); 

        }

    }

    /**
     * Displays a dialog which prompts the user for an
     * url. 
     * The url will be sent to the NewFeed-class, and from
     * there a new DrawerItem will be called.
     */ 
    public void newFeed() {

        final Activity activity = mApp.getFeedActivity();

        final EditText input = new EditText(activity);
        input.setHint(R.string.new_feed_hint);

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
        dialog.setTitle(R.string.new_feed_title);
        dialog.setView(input);

        /* The "OK"-button */
        dialog.setPositiveButton(R.string.new_feed_ok,
                new DialogInterface.OnClickListener() {

                    @Override 
                    public void onClick(DialogInterface dialog, int button) {
                        NewFeed newFeed = new NewFeed(mApp);
                        newFeed.execute(input.getText().toString());
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

    /**
     * The ClickListener for the Navigation Drawers ListView.
     */
    private class DrawerClickListener implements OnItemClickListener {

        private View mLastItemView;

        @Override
        public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

            setItemView(view, position);
            drawerItemClicked(position);

        }

        /**
         * Sets the clicked views fontstyle to bold on click,
         * and reset the last clicked.
         */
        public void setItemView(View view, int position) {

            int listSize = mDrawerListView.getCount();

            if (position != 2 && position <= listSize - 3) {

                if (mLastItemView != null) {

                    TextView textView = (TextView) mLastItemView.findViewById(
                            R.id.drawer_item_text);
                    textView.setTypeface(((TextView) view.findViewById(
                                    R.id.drawer_item_text)).getTypeface(), 
                            Typeface.NORMAL);

                }

                TextView textView = (TextView) view.findViewById(
                        R.id.drawer_item_text);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

                mLastItemView = view;

            }

        }

    }

}
