package no.fjeld.feed;

import java.util.*;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class DrawerAdapter extends ArrayAdapter <DrawerItem> {

    private Activity mActivity;
    private ArrayList <DrawerItem> mDrawerList;

    /**
     * Constructor for the class DrawerAdapter.
     *
     * @param activity     FeedActivity-pointer
     * @param resourceView The resource id for the DrawerItem's layout
     * @param drawerList   The list which contains DrawerItems
     */
    public DrawerAdapter(Activity activity, int resourceView, 
            ArrayList <DrawerItem> drawerList) {

        super(activity, resourceView, drawerList);
        mActivity = activity;
        mDrawerList = drawerList; 

    }

    private class ViewHolder {

        TextView mFeedName;

    }

    /**
     * Returns the View for the current DrawerItem.
     *
     * @return view The DrawerItem-view.
     */
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {

            /* Inflates the layout for the DrawerItem. */
            view = ((LayoutInflater) mActivity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.drawer_item, null);

            viewHolder = new ViewHolder();

            viewHolder.mFeedName = (TextView) view.findViewById(
                    R.id.drawer_item_text);

            view.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) view.getTag();

        DrawerItem drawerItem = mDrawerList.get(position);

        /* Sets the title for the TextView in the DrawerItem-layout. */ 
        if (drawerItem != null) 
            viewHolder.mFeedName.setText(drawerItem.getFeedName());

        /* If the DrawerItem is a newly added item that has not loaded
         * all it's values yet, it displays the title 'Loading feed..'
         * and sets the ProgressBar as visible. */
        LinearLayout progressLayout = (LinearLayout) view
                .findViewById(R.id.drawer_item_progress);

        if (drawerItem.getFeedName().equals(mActivity
                .getString(R.string.drawer_item_loading))) { 
            progressLayout.setVisibility(View.VISIBLE);
        } else {    
            progressLayout.setVisibility(View.GONE);
        }

        return view;

    }

    /** 
     * notifyDataSetChanged() is overridden
     * to sort the list when something has changed.
     */
    @Override
    public void notifyDataSetChanged() {

        sortList();
        super.notifyDataSetChanged();

    }

    /**
     * Sorts mDrawerList (the list with the DrawerItem-objects) 
     * alphabetically based on their titles. 
     */
    private void sortList() {

        Collections.sort(mDrawerList, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo( 
                    second.getFeedName().toLowerCase());
            }

        });

    }

    /**
     * Returns the adapters ArrayList.
     *
     * @return mDrawerList The ArrayList with the DrawerItems.
     */
    public ArrayList <DrawerItem> getDrawerList() {

        return mDrawerList;

    }

}
