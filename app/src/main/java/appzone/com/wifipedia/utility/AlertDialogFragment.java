package appzone.com.wifipedia.utility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.contract.SavedListContract;

public class AlertDialogFragment extends DialogFragment {
    SavedListContract.View savedListView;

    public AlertDialogFragment(SavedListContract.View savedListView){
        this.savedListView = savedListView;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Failed to remove Network");
        builder.setMessage("This network was not added using this application");
        builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                savedListView.launchWifiSettings();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Negative", Toast.LENGTH_SHORT).show();

                    }});
                    // Create the AlertDialog object and return it
         return builder.create();
    }
}