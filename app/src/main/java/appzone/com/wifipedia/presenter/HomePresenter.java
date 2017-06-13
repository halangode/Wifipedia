package appzone.com.wifipedia.presenter;

import appzone.com.wifipedia.presenter.contract.HomeContract;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class HomePresenter implements HomeContract.Event {
    private HomeContract.View homeView;

    public HomePresenter(HomeContract.View view){
        homeView = view;
    }

    @Override
    public void onStateChanged() {
        homeView.onStateChanged();
    }

    @Override
    public void onNewResults() {
        homeView.onNewResultsAvailable();
    }

    @Override
    public void onWifiConnected() {
        homeView.onWifiConnected();
    }

    @Override
    public void onSignalStrengthChanged() {
        homeView.onSignalStrengthChanged();
    }

    @Override
    public void onDisconnected() {
        homeView.dismissProgressDialog();
        homeView.onDisconnected();
    }
}
