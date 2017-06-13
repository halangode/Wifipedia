package appzone.com.wifipedia.presenter.contract;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.List;

import appzone.com.wifipedia.presenter.BaseView;
import appzone.com.wifipedia.ui.beans.WifiCapabilityBean;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class WifiListContract {
    public interface View extends BaseView<UserActions>{
        void showPasswordDialog(ScanResult scanResult);
        void showProgressDialog(String message);
        void onConnecting();
        void onWifiConnected();
        void dismissProgressDialog();
        void onRefreshList();
        void launchMoreInfoActivity();
    }
    public interface UserActions{
        void onConnectClicked(ScanResult scanResult);
        void onDisconnectClicked();
        List<ScanResult> filterScanResults();
        void onConnected();
        void onStateChanged();
        void onConnectRequest(ScanResult scanResult, String password, Context context);
        void onMoreInfoClicked();
    }
}
