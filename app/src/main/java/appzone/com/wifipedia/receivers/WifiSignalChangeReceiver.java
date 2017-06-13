package appzone.com.wifipedia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import appzone.com.wifipedia.presenter.contract.HomeContract;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Hari on 16/11/16.
 */
public class WifiSignalChangeReceiver extends BroadcastReceiver {
    HomeContract.Event homePresenter;

    String TAG = this.getClass().getSimpleName();

    public WifiSignalChangeReceiver(HomeContract.Event event){
        homePresenter = event;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)){
            Log.d(TAG, "onReceive: " + WifiFactory.getScanResult().toString());
            homePresenter.onNewResults();
        }
        else if(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0) != 0){
            int signalChange = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
            homePresenter.onSignalStrengthChanged();
            Toast.makeText(context, "Signal strength changed: " + signalChange, Toast.LENGTH_SHORT).show();
        }
    }
}
