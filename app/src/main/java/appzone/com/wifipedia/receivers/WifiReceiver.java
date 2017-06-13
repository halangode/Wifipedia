package appzone.com.wifipedia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import appzone.com.wifipedia.presenter.contract.HomeContract;

/**
 * Created by Hari on 16/11/16.
 */
public class WifiReceiver extends BroadcastReceiver {

    HomeContract.Event homePresenter;
    String TAG = this.getClass().getSimpleName();


    String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    public WifiReceiver(HomeContract.Event homePresenter) {
        this.homePresenter = homePresenter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            homePresenter.onStateChanged();
            Log.d(TAG, "Wifi state changed");
        }
        else if(action.equals(CONNECTIVITY_CHANGE)){
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

            if(networkInfo != null &&
                    networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                    networkInfo.isConnected()){

                Log.d(TAG, "Connectivity changed");
                homePresenter.onWifiConnected();
            }
        }
    }
}
