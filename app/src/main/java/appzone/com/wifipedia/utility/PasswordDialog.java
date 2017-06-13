package appzone.com.wifipedia.utility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.contract.WifiListContract;

/**
 * Created by Hari on 13/11/16.
 */

public class PasswordDialog extends AppCompatDialogFragment {

    ScanResult scanResult;
    String password;
    WifiListContract.UserActions wifiPresenter;

    public PasswordDialog(ScanResult scanResult, WifiListContract.UserActions presenter) {
        this.scanResult = scanResult;
        wifiPresenter = presenter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.wifi_connect_alert_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        builder.setMessage("Connect to " + scanResult.SSID);

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        password = ((EditText)((Dialog)dialog).findViewById(R.id.passwordET)).getText().toString();
                        wifiPresenter.onConnectRequest(scanResult, password, getActivity());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PasswordDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
