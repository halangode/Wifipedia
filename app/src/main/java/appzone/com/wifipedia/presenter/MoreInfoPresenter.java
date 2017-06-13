package appzone.com.wifipedia.presenter;

import appzone.com.wifipedia.presenter.contract.MoreInfoContract;
import appzone.com.wifipedia.ui.fragments.MoreInfoFragment;

/**
 * Created by Harikumar Alangode on 22-Apr-17.
 */

public class MoreInfoPresenter implements MoreInfoContract.UserAction {

    MoreInfoContract.View moreInfoView;

    public MoreInfoPresenter(MoreInfoContract.View view) {
        moreInfoView = view;
    }

    @Override
    public void onScanNetworkClicked(String hostAddr) {
        moreInfoView.showScannedListFragment(hostAddr);
    }
}
