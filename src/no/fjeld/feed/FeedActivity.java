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

    private FeedApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mApplication = (FeedApplication) getApplication();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

}
