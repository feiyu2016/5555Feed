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
    private ShareAdapter mShareAdapter;

    private ActivityInfo mActivityInfo;
    private boolean mShare = false;

    public ShareView(Activity activity, ViewGroup parentView, FeedItem 
            feedItem) {

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

        mShareAdapter = new ShareAdapter(pacMan, apps);
        mShareView.setAdapter(mShareAdapter);

        mShareView.setOnItemClickListener(getOnItemClickListener());

    }

    private OnItemClickListener getOnItemClickListener() {

        return new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView <?> parent, View view, 
                    int position, long id) {

                mActivityInfo = mShareAdapter.getItem(position)
                        .activityInfo;
                mShare = true;

                SlidingView.getInstance().slideOut();

            }

        };

    }

    private void showView() {

        new SlidingView(mActivity, mParentView, mShareView) {
            @Override
            public void onSlideOutFinished() {

                if (!mShare) return;

                ComponentName app = new ComponentName(mActivityInfo
                        .applicationInfo.packageName, mActivityInfo.name);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setComponent(app).setType("text/plain");

                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mFeedItem.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, 
                        mFeedItem.getDescription() + "\n\n" +
                        mFeedItem.getUrl());

                mActivity.startActivity(shareIntent);

            }
        }; 

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

            imageView.setImageDrawable(getItem(position).loadIcon(
                        mPacMan));

            return imageView;

        }

    }

}
