package no.fjeld.feed;

import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;

public class PreferencesActivity extends PreferenceActivity {

    FeedApplication mApp;

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

    public static class PreferenceFrag extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);         
            addPreferencesFromResource(R.layout.preferences);

            initPrefs();

        }

        public void initPrefs() {

            Preference clearRead = (Preference) findPreference("preference_clear_read");
            Preference clearSaved = (Preference) findPreference("preference_clear_saved");

            clearRead.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return true;
                }

            });

            clearSaved.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return true;
                }

            });

        }

    }

}
