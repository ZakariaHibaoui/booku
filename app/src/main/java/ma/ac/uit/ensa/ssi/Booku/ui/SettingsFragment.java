package ma.ac.uit.ensa.ssi.Booku.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.utils.SettingsUtil;

public class SettingsFragment extends PreferenceFragmentCompat {
    Activity act;

    public SettingsFragment(Activity act) {
        this.act = act;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        findPreference("language_preference")
                .setOnPreferenceChangeListener((p, n) -> {
                    SettingsUtil.restart_on_change(act);
                    return true;
                });

        findPreference("dark_mode")
                .setOnPreferenceChangeListener((p, n) -> {
                    SettingsUtil.restart_on_change(act);
                    return true;
                });
    }
}