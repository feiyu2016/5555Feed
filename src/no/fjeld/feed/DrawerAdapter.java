package no.fjeld.feed;

import java.util.*;

import com.google.gson.*;

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
     * Overrides the notifyDataSetChanged to save the DrawerList 
     * to SharedPreferences when a new feed is added or removed.
     */
    @Override
    public void notifyDataSetChanged() {

        Set <String> mDrawerSet = new LinkedHashSet <String> ();

        Gson mGson = new GsonBuilder().setPrettyPrinting().create();

        SharedPreferences mSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(mActivity.getBaseContext());

        for (int i = 0; i < mDrawerList.size(); i++)
            mDrawerSet.add(mGson.toJson(mDrawerList.get(i))); 

        mSharedPrefs.edit().putStringSet(
                "drawer_items", mDrawerSet).commit();

        mDrawerList = sorted(mDrawerList);

        super.notifyDataSetChanged();
    
    }

    /**
     * Sorts the items alphabetically based on their titles. 
     *
     * @param  mTempList The list to be sorted.
     * @return mTempList The sorted list.
     */
    private ArrayList <DrawerItem> sorted(ArrayList <DrawerItem> mTempList) {

        Collections.sort(mTempList, new Comparator <DrawerItem> () {

            @Override
            public int compare(DrawerItem first, DrawerItem second) {
                return first.getFeedName().toLowerCase().compareTo( 
                    second.getFeedName().toLowerCase());
            }

        });

        return mTempList;

    }

}
