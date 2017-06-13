package appzone.com.wifipedia.presenter;

import appzone.com.wifipedia.presenter.contract.SavedListContract;
import appzone.com.wifipedia.utility.AlertDialogFragment;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Harikumar Alangode on 23-Apr-17.
 */

public class SavedListPresenter implements SavedListContract.UserAction{
    SavedListContract.View savedListView;
    public SavedListPresenter(SavedListContract.View view) {
        savedListView = view;
    }

    @Override
    public boolean forgetNetwork(int id) {
        boolean isRemoved = WifiFactory.getWifiManager().removeNetwork(id);
        if(isRemoved){
            savedListView.onRefreshNetworkList();
            return true;
        }
        return false;
    }

    @Override
    public void showAlertDialog() {
        savedListView.showAlertDialog();
    }
}
