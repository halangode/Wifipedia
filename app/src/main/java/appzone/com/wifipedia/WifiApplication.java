package appzone.com.wifipedia;

import android.app.Application;
import android.support.v7.preference.Preference;

import appzone.com.wifipedia.utility.WifiFactory;
import appzone.com.wifipedia.utility.WifiHelper;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class WifiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize WifiFactory
        WifiFactory wifiFactory = WifiFactory.getInstance(this);

    }
}
