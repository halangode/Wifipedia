package appzone.com.wifipedia.presenter.contract;

import java.net.InetAddress;

import appzone.com.wifipedia.ui.beans.ScannedBean;

/**
 * Created by Harikumar Alangode on 22-Apr-17.
 */

public class ScannedListContract {

    public interface View{
        void showProgressDialogue();
        void dismissProgressDialogue();
        void setProgress(int value);
        void foundNewDevice(ScannedBean scannedBean);

    }
    public interface UserActions{
        void onScanWifiDevices(String inetAddress, int lastVal);
        void onCancelTask();
    }
}
