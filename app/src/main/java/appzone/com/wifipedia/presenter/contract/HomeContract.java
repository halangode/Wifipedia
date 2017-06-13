package appzone.com.wifipedia.presenter.contract;

import appzone.com.wifipedia.presenter.BaseView;

/**
 * Created by Harikumar Alangode on 20-Apr-17.
 */

public class HomeContract {
    public interface View extends BaseView<Event> {
        void onStateChanged();
        void showProgressDialog(String message);
        void dismissProgressDialog();
        void onNewResultsAvailable();
        void onSignalStrengthChanged();
        void onWifiConnected();

        void onDisconnected();
    }

    public interface Event{
        void onStateChanged();
        void onNewResults();
        void onWifiConnected();
        void onSignalStrengthChanged();

        void onDisconnected();
    }
}
