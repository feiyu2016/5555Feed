package no.fjeld.feed;

import java.util.*;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class DrawerAdapter extends ArrayAdapter <DrawerItem> {

    private Activity mActivity;
    private ArrayList <DrawerItem> mDrawerItems;

    public DrawerAdapter(Activity activity, int resourceView, 
            ArrayList <DrawerItem> drawerList) {

        super(activity, resourceView, drawerList);
        this.mActivity = activity;
        this.mDrawerItems = drawerList; 

    }

    private class ViewHolder {

        TextView mFeedName;

    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {

            view = ((LayoutInflater) mActivity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.drawer_item, null);

            viewHolder = new ViewHolder();
            viewHolder.mFeedName = (TextView) view.findViewById(
                    R.id.drawer_item_text);

            view.setTag(viewHolder);


        } else
            viewHolder = (ViewHolder) view.getTag();

        DrawerItem drawerItem = mDrawerItems.get(position);

        if (drawerItem != null) 
            viewHolder.mFeedName.setText(drawerItem.getFeedName());

        if (drawerItem.getFeedName().equals(mActivity.getString(
                        R.string.drawer_item_loading))) 
            ((LinearLayout) view.findViewById(R.id.drawer_item_progress))
                .setVisibility(View.VISIBLE);
        else    
            ((LinearLayout) view.findViewById(R.id.drawer_item_progress))
                .setVisibility(View.GONE);


        return view;

    }

    /**
     * Returns the adapters ArrayList.
     */
    public ArrayList <DrawerItem> getDrawerList() {

        return mDrawerItems;

    }

    @Override
    public void notifyDataSetChanged() {

        sortList();
        super.notifyDataSetChanged();

    }

    /**
     * Sorts the items alphabetically based on their titles. 
     */
    private void sortList() {

        Collections.sort(mDrawerItems, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo( 
                    second.getFeedName().toLowerCase());
            }

        });

    }

}
