package appzone.com.wifipedia.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.ui.activities.WifiHomeActivity;
import appzone.com.wifipedia.utility.Constants;


/**
 * Created by Harikumar Alangode on 02-May-17.
 */

public class SettingsPrefFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fragment_preference_settings);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getResources().getString(R.string.wifi_interval_key))){
            Constants.DELAY = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.wifi_interval_key), "10"));
            ((WifiHomeActivity)getActivity()).removeCallback();
            ((WifiHomeActivity)getActivity()).startScanningScheduler();
        }
        else if(key.equals(getResources().getString(R.string.device_interval_key))){
            String deviceInterval = sharedPreferences.getString(getResources().getString(R.string.device_interval_key), "Medium");
            Constants.DEVICE_SCAN_INTERVAL = ((WifiHomeActivity)getActivity()).getDeviceScanningValue(deviceInterval);
        }
        else if(key.equals(getResources().getString(R.string.hidden_ssid_key))){
            Constants.isHiddenSSIDEnabled = sharedPreferences.getBoolean(getResources().getString(R.string.hidden_ssid_key), false);
        }
    }
}
