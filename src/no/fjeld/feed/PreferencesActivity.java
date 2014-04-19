package no.fjeld.feed;

import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);     

        getActionBar().setDisplayHomeAsUpEnabled(true);

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

        }

    }

}
