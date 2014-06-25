package no.fjeld.feed;

import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

public class ShareView {

    private Activity mActivity;
    private ViewGroup mParentView;

    public ShareView(Activity activity, ViewGroup parentView) {
        
        mActivity = activity;
        mParentView = parentView;
        
        showView();
   
    }

    private void showView() {

        LayoutInflater inflater = (LayoutInflater) mActivity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GridView shareView = (GridView) inflater.inflate(R.layout.share_view, null); 

        PackageManager pacMan = mActivity.getPackageManager();
        List <ResolveInfo> apps = pacMan.queryIntentActivities(
                new Intent(Intent.ACTION_SEND).setType("text/plain"), 0);

        ShareAdapter shareAdapter = new ShareAdapter(pacMan, apps);
        shareView.setAdapter(shareAdapter);

        shareView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

            }
        });

        new SlidingView(mActivity, mParentView, shareView);    

    }

    private class ShareAdapter extends ArrayAdapter <ResolveInfo> {

        private PackageManager mPacMan;

        ShareAdapter(PackageManager pacMan, List <ResolveInfo> apps) {

            super(mActivity, 0, apps);
            mPacMan = pacMan;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            ImageView imageView;
            LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (view == null) 
                imageView = (ImageView) inflater.inflate(R.layout.share_view_item, null);
            else 
                imageView = (ImageView) view;

            imageView.setImageDrawable(getItem(position).loadIcon(mPacMan));

            return imageView;

        }

    }

}
