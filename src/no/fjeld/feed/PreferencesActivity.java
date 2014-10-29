package no.fjeld.feed;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;
import android.widget.*;

public class PreferencesActivity extends PreferenceActivity {

    private FeedApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);     

        mApp = (FeedApplication) getApplication();
        initActionBar();

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new PreferenceFrag()).commit();

    }

    /**
     * Initializes the custom ActionBar.
     */
    private void initActionBar() {

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        getActionBar().setCustomView(inflater.inflate(
                    R.layout.action_bar, null));

        int abResId = getResources().getIdentifier(
                "action_bar_container", "id", "android");

        TextView mActionBarTitle = (android.widget.TextView) 
            getWindow().getDecorView().findViewById(abResId)
            .findViewById(R.id.ab_title);

        ImageView mABIndicator = (android.widget.ImageView) 
            getWindow().getDecorView().findViewById(abResId)
            .findViewById(R.id.drawer_indicator);

        mABIndicator.setImageResource(R.drawable.ic_action_navigation_arrow_back);
        mActionBarTitle.setText((getResources().getString(R.string.preference_title)));

        mABIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);

    }

    @SuppressLint("ValidFragment")
    private class PreferenceFrag extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);         
            addPreferencesFromResource(R.layout.preferences);

            initPrefs();

        }

        /**
         * Provides an OnPreferenceClickListener for the 'Clear read'-option.
         */
        private void initPrefs() {

            Preference clearRead = (Preference) findPreference(
                    "preference_clear_read");

            clearRead.setOnPreferenceClickListener(
                    new OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            mApp.getDatabase().delete("readItems", null);
                            return true;
                        }
                    });

        }

    }

}
