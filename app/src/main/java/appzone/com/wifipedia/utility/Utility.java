package appzone.com.wifipedia.utility;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Hari on 13/08/16.
 */
public class Utility {


    public static void showWifiAlertDialog(Context context, String title, String message, String positive, String negative){

            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

    }



    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    public static ProgressDialog showProgressDialog(Context context, String message, int style){
        ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.setProgressStyle(style);
        progress.setIndeterminate(true);
        progress.show();
        return progress;
    }

    public static void toast(String message, Context context){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
