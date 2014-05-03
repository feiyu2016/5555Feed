package no.fjeld.feed;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;

import java.util.*;

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

    private class PreferenceFrag extends PreferenceFragment {

        private SharedPreferences mSharedPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);         
            addPreferencesFromResource(R.layout.preferences);

            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                    getBaseContext());

            initPrefs();

        }

        public void initPrefs() {

            Preference clearRead = (Preference) findPreference(
                    "preference_clear_read");
            Preference clearSaved = (Preference) findPreference(
                    "preference_clear_saved");

            clearRead.setOnPreferenceClickListener(
                    new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            mApp.getDatabase().delete("readItems", null);
                            return true;

                        }

                    });

            clearSaved.setOnPreferenceClickListener(
                    new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            mApp.getDatabase().delete("savedItems", null);
                            return true;

                        }

                    });


        }

    }

}
