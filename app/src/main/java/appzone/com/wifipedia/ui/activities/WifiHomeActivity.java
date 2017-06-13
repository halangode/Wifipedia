package appzone.com.wifipedia.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.util.List;


import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.HomePresenter;
import appzone.com.wifipedia.presenter.contract.HomeContract;
import appzone.com.wifipedia.ui.adapters.ScreenSlidePagerAdapter;
import appzone.com.wifipedia.receivers.WifiReceiver;
import appzone.com.wifipedia.receivers.WifiSignalChangeReceiver;
import appzone.com.wifipedia.ui.fragments.SavedWifiFragment;
import appzone.com.wifipedia.ui.fragments.WifiListFragment;
import appzone.com.wifipedia.utility.Constants;
import appzone.com.wifipedia.utility.WifiFactory;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class WifiHomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, HomeContract.View{

    //Constants
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;
    private final int REQUEST_CHECK_SETTINGS = 0x1;
    String TAG = this.getClass().getSimpleName().toString();

    //Receivers
    WifiReceiver wifiReceiver;
    WifiSignalChangeReceiver wifiSignalChangeReceiver;

    List<Fragment> fragmentList;
    WifiListFragment wifiListFragment;
    SavedWifiFragment savedWifiFragment;


    //Fragment stuffs

    ProgressDialog progressDialog;
    //Fragments

    TabLayout tabLayout;

    //Viewpager stuff

    private ViewPager viewPager;
    public PagerAdapter pagerAdapter;

    FloatingActionButton wifiButton;

    HomeContract.Event homePresenter;

    private Handler handler;
    private Runnable runnableCode;

    GoogleApiClient mGoogleApiClient;
    private boolean isResultsAvailable = true;

    //Manager
    public void startScanningScheduler(){

        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                if(isResultsAvailable){
                    WifiFactory.getWifiManager().startScan();
                    handler.postDelayed(runnableCode, Constants.DELAY * 1000);
                    Log.d(TAG, "Wifi scanning delay" + Constants.DELAY);
                    isResultsAvailable = false;
                }
            }
        };
        handler.post(runnableCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Wifipedia");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        setPreferenceDefaults();


        wifiButton = (FloatingActionButton) findViewById(R.id.wifi_fab);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(WifiFactory.getWifiManager().isWifiEnabled()){
                    WifiFactory.getWifiManager().setWifiEnabled(false);
                } else {
                    WifiFactory.getWifiManager().setWifiEnabled(true);
                }
            }
        });


        requestPermission();

        homePresenter = new HomePresenter(this);

        progressDialog = new ProgressDialog(this);

        setupViewPager();
    }

    private void setPreferenceDefaults() {
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference_settings, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.isHiddenSSIDEnabled = sharedPref.getBoolean(getResources().getString(R.string.hidden_ssid_key), false);
        Constants.DELAY = Integer.parseInt(sharedPref.getString(getResources().getString(R.string.wifi_interval_key), "10"));

        String deviceScanningInterval = sharedPref.getString(getResources().getString(R.string.device_interval_key), getResources().getString(R.string.device_interval_medium));

        Constants.DEVICE_SCAN_INTERVAL = getDeviceScanningValue(deviceScanningInterval);

    }


    public int getDeviceScanningValue(String str){
        switch (str){
            case "Fast": return 60;
            case "Medium": return 100;
            case "Slow": return 200;
            case "Deep": return 300;
            default: return 100;
        }
    }


    private void requestPermission() {
        //Marshmellow check permission for location
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        else {
            requestEnableLocation();
        }
    }

    public void setupViewPager(){

        if(viewPager == null){
            viewPager = (ViewPager) findViewById(R.id.pager);
        }
        if(pagerAdapter == null){
            pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        }
        if(viewPager != null && pagerAdapter != null && viewPager.isAttachedToWindow()){
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(1);
        }
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    public void initRecieversStartScan() {

        wifiReceiver = new WifiReceiver(homePresenter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver, intentFilter);

        wifiSignalChangeReceiver = new WifiSignalChangeReceiver(homePresenter);
        IntentFilter intentFilterSignal = new IntentFilter();
        intentFilterSignal.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilterSignal.addAction(WifiManager.RSSI_CHANGED_ACTION);
        this.registerReceiver(wifiSignalChangeReceiver, intentFilterSignal);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecieversStartScan();
        startScanningScheduler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(wifiSignalChangeReceiver);
        removeCallback();
    }

    public void removeCallback() {
        if(handler != null){
            handler.removeCallbacks(runnableCode);
            Log.d(TAG, "removeCallback: removedCallback");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestEnableLocation();
        } else {
            //ask to grant permission again
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //TODO Investigate Nexus location issue

    public void requestEnableLocation()
    {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(100000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        initRecieversStartScan();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(WifiHomeActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        initRecieversStartScan();
                        break;

                    case Activity.RESULT_CANCELED:
                        requestEnableLocation();//keep asking if imp or do whatever
                        break;

                }

                break;
        }
    }

    public boolean isTopActivity(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String className = taskInfo.get(0).topActivity.getClassName();
        if(!className.equals(this.getComponentName().getClassName())){
            return false;
        }
        return true;
    }

    @Override
    public void onStateChanged() {
        if (WifiFactory.getWifiManager().isWifiEnabled()) {
            startScanningScheduler();
            showProgressDialog("Scanning");
            wifiButton.setImageDrawable(getDrawable(R.drawable.wifi_off));
            setupViewPager();
        } else {
            dismissProgressDialog();
            if(!isTopActivity()){
                Intent i = new Intent(this, WifiHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
            else {
                setupViewPager();
            }

            wifiButton.setImageDrawable(getDrawable(R.drawable.wifi_on));
            removeCallback();

        }
    }

    @Override
    public void showProgressDialog(String message) {
        if(!progressDialog.isShowing()){
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    @Override
    public void onNewResultsAvailable() {
        isResultsAvailable = true;
        fragmentList = getSupportFragmentManager().getFragments();
        if(wifiListFragment == null && fragmentList != null){
            for(Fragment fragment: fragmentList){
                if(fragment instanceof WifiListFragment){
                    wifiListFragment = ((WifiListFragment) fragment);
                }
            }
        }

        if(wifiListFragment != null){
            wifiListFragment.onRefreshList();
        }
        pagerAdapter.notifyDataSetChanged();
        dismissProgressDialog();
    }

    @Override
    public void onSignalStrengthChanged() {
        if(wifiListFragment != null){
            wifiListFragment.onRefreshList();
        }
    }

    @Override
    public void onWifiConnected() {

        fragmentList = getSupportFragmentManager().getFragments();
        if(savedWifiFragment == null && fragmentList != null    ){
            for(Fragment fragment: fragmentList){
                if(fragment instanceof SavedWifiFragment){
                    savedWifiFragment = ((SavedWifiFragment) fragment);
                }
            }
        }

        if(savedWifiFragment != null){
            savedWifiFragment.onRefreshNetworkList();
        }

        pagerAdapter.notifyDataSetChanged();
        dismissProgressDialog();
    }

    @Override
    public void onDisconnected() {
        if(wifiListFragment != null){
            wifiListFragment.onRefreshList();
        }
    }
}
