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
    private FeedItem mFeedItem;
    private GridView mShareView;

    public ShareView(Activity activity, ViewGroup parentView, FeedItem feedItem) {

        mActivity = activity;
        mParentView = parentView;
        mFeedItem = feedItem;

        initView();
        showView();

    }

    private void initView() {

        LayoutInflater inflater = (LayoutInflater) mActivity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mShareView = (GridView) inflater.inflate(R.layout.share_view, null); 

        PackageManager pacMan = mActivity.getPackageManager();
        List <ResolveInfo> apps = pacMan.queryIntentActivities(
                new Intent(Intent.ACTION_SEND).setType("text/plain"), 0);

        final ShareAdapter shareAdapter = new ShareAdapter(pacMan, apps);
        mShareView.setAdapter(shareAdapter);

        mShareView.setOnItemClickListener(getOnItemClickListener());

    }

    private OnItemClickListener getOnItemClickListener(final ShareAdapter 
            shareAdapter) {

        return new OnItemClickListener() {
       
            @Override
            public void onItemClick(AdapterView <?> parent, View view, 
                int position, long id) {

                ActivityInfo activity = shareAdapter.getItem(position)
                        .activityInfo;
                ComponentName app = new ComponentName(activity
                        .applicationInfo.packageName, activity.name);

                Intent share_intent = new Intent(Intent.ACTION_SEND);
                share_intent.setComponent(app).setType("text/plain");

                share_intent.putExtra(Intent.EXTRA_TEXT, 
                        mFeedItem.getTitle() + "\n\n" + 
                        mFeedItem.getDescription());
                
                mActivity.startActivity(share_intent);

            }
        
        };

    }

    private void showView() {

        new SlidingView(mActivity, mParentView, mShareView);    

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
                imageView = (ImageView) inflater.inflate(
                        R.layout.share_view_item, null);
            else 
                imageView = (ImageView) view;

            imageView.setImageDrawable(getItem(position)
                    .loadIcon(mPacMan));

            return imageView;

        }

    }

}
