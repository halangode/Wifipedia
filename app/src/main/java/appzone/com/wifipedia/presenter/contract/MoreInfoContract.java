package appzone.com.wifipedia.presenter.contract;

import appzone.com.wifipedia.presenter.BaseView;

/**
 * Created by Harikumar Alangode on 22-Apr-17.
 */

public class MoreInfoContract {

    public interface View extends BaseView<WifiListContract.UserActions>{
        void showScannedListFragment(String hostAddr);
    }

    public interface UserAction{
        void onScanNetworkClicked(String hostAddr);
    }
}
