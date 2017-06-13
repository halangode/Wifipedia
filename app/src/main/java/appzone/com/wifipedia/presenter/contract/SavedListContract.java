package appzone.com.wifipedia.presenter.contract;

/**
 * Created by Harikumar Alangode on 23-Apr-17.
 */

public class SavedListContract {

    public interface View{
        void onRefreshNetworkList();

        void showAlertDialog();

        void launchWifiSettings();
    }

    public interface UserAction{
        boolean forgetNetwork(int id);

        void showAlertDialog();
    }
}
