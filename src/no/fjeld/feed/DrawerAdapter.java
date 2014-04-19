package no.fjeld.feed;

import java.util.*;

import com.google.gson.*;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

class DrawerAdapter extends ArrayAdapter <DrawerItem> {

    private Activity mActivity;
    private ArrayList <DrawerItem> mDrawerList;

    public DrawerAdapter(Activity mActivity, int mResourceView, 
            ArrayList <DrawerItem> mDrawerList) {

        super(activity, mResourceView, mDrawerList);
        this.mActivity = mActivity;
        this.mDrawerList = mDrawerList; 

    }

    private class ViewHolder {

        TextView mFeedName;

    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (view == null) {

            view = ((LayoutInflater) activity.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.drawer_item, null);

            mViewHolder = new ViewHolder();
            mViewHolder.mFeedName = (TextView) view.findViewById(
                    R.id.feed_name);

            view.setTag(mViewHolder);


        } else
            mViewHolder = (ViewHolder) view.getTag();

        DrawerItem mDrawerItem = mDrawerList.get(position);

        if (mDrawerItem != null) 
            mViewHolder.mFeedName.setText(mDrawerItem.getFeedName());

        return view;

    }

    public ArrayList <DrawerItem> getDrawerList() {

        return mDrawerList;

    }

    /**
     * Overrides the notifyDataSetChanged to save
     * the DrawerList to SharedPreferences when a new 
     * item is added or removed.
     */
    @Override
    public void notifyDataSetChanged() {

        Set <String> mDrawerItemSet = new LinkedHashSet <String> ();

        Gson mGson = new GsonBuilder().setPrettyPrinting().create();

        SharedPreferences mSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(mActivity.getBaseContext());

        for (int i = 0; i < list.size(); i++)
            mDrawerItemSet.add(mGson.toJson(mDrawerItems.get(i))); 

        mSharedPrefs.edit().putStringSet(
                "drawer_items", mDrawerItemSet).commit();

        mDrawerItems = sorted(mDrawerItems);

        super.notifyDataSetChanged();
    
    }

    /**
     * Sorts the items alphabetically based on their names. 
     *
     * @param  mDrawerItems The list to be sorted.
     * @return mDrawerItems The sorted list.
     */
    public ArrayList <DrawerItem> sorted(ArrayList <DrawerItem> mTempItems) {

        Collections.sort(mTempItems, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo( 
                    second.getFeedName().toLowerCase());
            }

        });

        return mTempItems;

    }

}
