package appzone.com.wifipedia.utility;

import android.content.Context;
import android.net.sip.SipAudioCall;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class WifiFactory {

    static Context context;
    private static WifiFactory wifiFactory;
    private static WifiManager wifiManager;

    private WifiFactory(Context context){
        this.context = context;
    }

    public static WifiFactory getInstance(Context context){
        if(wifiFactory == null){
            wifiFactory = new WifiFactory(context);
        }

        return wifiFactory;
    }

    public static WifiManager getWifiManager(){
        if(wifiManager == null && context != null){
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        return wifiManager;
    }

    public static List<ScanResult> getScanResult(){
        return getWifiManager().getScanResults();
    }


}
