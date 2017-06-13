package appzone.com.wifipedia.presenter;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.List;

import appzone.com.wifipedia.presenter.contract.WifiListContract;
import appzone.com.wifipedia.ui.activities.WifiHomeActivity;
import appzone.com.wifipedia.ui.beans.WifiCapabilityBean;
import appzone.com.wifipedia.utility.Constants;
import appzone.com.wifipedia.utility.WifiFactory;
import appzone.com.wifipedia.utility.WifiHelper;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class WifiListPresenter implements WifiListContract.UserActions {
    WifiListContract.View wifiView;
    private Context context;


    public WifiListPresenter(WifiListContract.View view, Context context){
        wifiView = view;
        this.context = context;
    }

    @Override
    public void onConnectClicked(ScanResult scanResult) {
        WifiCapabilityBean capabilityBean = WifiHelper.convertScanResult(scanResult);
        if(capabilityBean.getSecurity().equals("OPEN")){
            onConnectRequest(scanResult, "", context);
        }
        else{
            wifiView.showPasswordDialog(scanResult);
        }
    }
    @Override
    public void onDisconnectClicked() {
        WifiFactory.getWifiManager().disconnect();
        wifiView.showProgressDialog("Disconnecting...");
    }

    @Override
    public List<ScanResult> filterScanResults() {
        List<ScanResult> scanResults = WifiFactory.getScanResult();
        if(!Constants.isHiddenSSIDEnabled){
            for(ScanResult s : scanResults){
                if(s.SSID.equals("")){
                    scanResults.remove(scanResults.indexOf(s));
                }
            }
        }
        return scanResults;
    }

    @Override
    public void onConnected() {
        wifiView.onWifiConnected();
        wifiView.onRefreshList();
    }

    @Override
    public void onStateChanged() {
        ((WifiHomeActivity)context).onNewResultsAvailable();
    }

    @Override
    public void onConnectRequest(ScanResult scanResult, String password, Context context) {
        wifiView.onConnecting();
        WifiHelper.connectWiFi(scanResult, password);
    }

    @Override
    public void onMoreInfoClicked() {
        wifiView.launchMoreInfoActivity();
    }
}
