package appzone.com.wifipedia.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.stealthcopter.networktools.ARPInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import appzone.com.wifipedia.presenter.contract.ScannedListContract;
import appzone.com.wifipedia.ui.beans.ScannedBean;
import appzone.com.wifipedia.utility.Constants;
import appzone.com.wifipedia.utility.WifiHelper;

/**
 * Created by Harikumar Alangode on 22-Apr-17.
 */

public class ScannedListPresenter implements ScannedListContract.UserActions {
    ScannedListContract.View scannedListView;

    public ScannedListPresenter(ScannedListContract.View scannedListView){
        this.scannedListView = scannedListView;
    }

    private AsyncTask upAsyncTask = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {

            InetAddress inetAddress = null;
            int progress = 0;


            for(int i = 1; i < 256; i++){

                if(i%2 == 0){

                    progress = Math.round(((i * 100)/255));
                    publishProgress(null, progress);
                }


                if (isCancelled())
                    return null;

                Log.d("test", "Trying: " + objects[0].toString() + String.valueOf(i));
                try {
                    inetAddress = InetAddress.getByName(objects[0].toString() + String.valueOf(i));

                    if(inetAddress.isReachable(Constants.DEVICE_SCAN_INTERVAL)){

                        publishProgress(doScanWifiDevice(inetAddress), progress);

                    }
                    else {
                        if(ARPInfo.getMACFromIPAddress(inetAddress.getHostName()) != null && !ARPInfo.getMACFromIPAddress(inetAddress.getHostName()).equals("00:00:00:00:00:00")){

                            publishProgress(doScanWifiDevice(inetAddress), progress);
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            scannedListView.setProgress((Integer)values[1]);

            if(values[0] != null){
                ScannedBean scannedBean = (ScannedBean) values[0];

                if(scannedBean != null){
                    scannedListView.foundNewDevice(scannedBean);
                }
            }

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            scannedListView.dismissProgressDialogue();
        }
    };

    public ScannedBean doScanWifiDevice(InetAddress inetAddress){
        Log.d("Found", inetAddress.getHostName());

        //get mac address
        String macAddress = WifiHelper.getMacAddress(inetAddress);

        if(macAddress == null){
            macAddress = WifiHelper.getMacFromArpCache(inetAddress.getHostName());
        }


        if(macAddress == null){
            macAddress = "Unknown";
        }

        ScannedBean scannedBean= new ScannedBean(inetAddress.getHostName(), macAddress);
        scannedBean.setHostName(inetAddress.getCanonicalHostName());
        scannedBean.setOs("unknown");

        String vendorName = WifiHelper.httpConnection(Constants.VENDOR_LOOKUP_URL, scannedBean.getMacAddress());

        if(vendorName != null && vendorName != ""){
            scannedBean.setVendor(vendorName);
        }
        else {
            scannedBean.setVendor("Unknown");
        }

        return scannedBean;
    }

    @Override
    public void onScanWifiDevices(String inetAddress, int lastVal) {
        scannedListView.showProgressDialogue();
        upAsyncTask.execute(inetAddress, lastVal);
    }

    @Override
    public void onCancelTask() {
        upAsyncTask.cancel(true);
    }
}
