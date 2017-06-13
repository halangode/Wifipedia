package appzone.com.wifipedia.ui.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.SavedListPresenter;
import appzone.com.wifipedia.presenter.contract.SavedListContract;
import appzone.com.wifipedia.ui.activities.WifiHomeActivity;
import appzone.com.wifipedia.ui.adapters.SavedListAdapter;
import appzone.com.wifipedia.utility.AlertDialogFragment;
import appzone.com.wifipedia.utility.WifiFactory;

import static android.provider.Settings.ACTION_WIFI_IP_SETTINGS;
import static android.provider.Settings.ACTION_WIFI_SETTINGS;

/**
 * Created by Hari on 17/11/16.
 */

public class SavedWifiFragment extends Fragment implements SavedListContract.View{

    private RecyclerView savedRecyclerView;
    private SavedListContract.UserAction savedListPresenter;

    public SavedListAdapter savedListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saved_wifi_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savedListPresenter = new SavedListPresenter(this);
        savedRecyclerView = (RecyclerView) view.findViewById(R.id.savedWifi_rv);
        if(WifiFactory.getWifiManager().isWifiEnabled()){
            RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity());
            savedRecyclerView.setLayoutManager(layoutManager);
            savedListAdapter = new SavedListAdapter(getActivity(), WifiFactory.getWifiManager().getConfiguredNetworks(), savedListPresenter);
            savedRecyclerView.setAdapter(savedListAdapter);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onRefreshNetworkList() {
        savedListAdapter.setWifiConfigurations(WifiFactory.getWifiManager().getConfiguredNetworks());
        savedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlertDialog() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment(this);
        alertDialogFragment.show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void launchWifiSettings() {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        getActivity().startActivityFromFragment(this, intent, 0);
    }
}
