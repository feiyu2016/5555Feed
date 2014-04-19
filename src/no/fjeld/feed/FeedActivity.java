package no.fjeld.feed;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class FeedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        mApplication.mNavigationDrawer.getDrawerToggle().syncState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mApplication.mNavigationDrawer.getDrawerToggle().onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);

    }

}
