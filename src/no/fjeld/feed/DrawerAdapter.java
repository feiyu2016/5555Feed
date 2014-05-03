package no.fjeld.feed;

import java.util.*;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class DrawerAdapter extends ArrayAdapter <DrawerItem> {

    private Activity mActivity;
    private ArrayList <DrawerItem> mDrawerList;

    public DrawerAdapter(Activity mActivity, int mResourceView, 
            ArrayList <DrawerItem> mDrawerList) {

        super(mActivity, mResourceView, mDrawerList);
        this.mActivity = mActivity;
        this.mDrawerList = mDrawerList; 

    }

    private class ViewHolder {

        TextView mFeedName;

    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (view == null) {

            view = ((LayoutInflater) mActivity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.drawer_item, null);

            mViewHolder = new ViewHolder();
            mViewHolder.mFeedName = (TextView) view.findViewById(
                    R.id.drawer_item_text);

            view.setTag(mViewHolder);


        } else
            mViewHolder = (ViewHolder) view.getTag();

        DrawerItem mDrawerItem = mDrawerList.get(position);

        if (mDrawerItem != null) 
            mViewHolder.mFeedName.setText(mDrawerItem.getFeedName());

        return view;

    }

    /**
     * Returns the adapters ArrayList.
     */
    public ArrayList <DrawerItem> getDrawerList() {

        return mDrawerList;

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

        Collections.sort(mDrawerList, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo( 
                    second.getFeedName().toLowerCase());
            }

        });

    }

}
