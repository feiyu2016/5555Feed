package no.fjeld.feed;

import android.annotation.SuppressLint;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;

public class PreferencesActivity extends PreferenceActivity {

    private FeedApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);     

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mApp = (FeedApplication) getApplication();

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new PreferenceFrag()).commit();

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
