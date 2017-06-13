package appzone.com.wifipedia.ui.fragments;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.WifiListPresenter;
import appzone.com.wifipedia.presenter.contract.WifiListContract;
import appzone.com.wifipedia.ui.activities.WifiConnectedActivity;
import appzone.com.wifipedia.ui.activities.WifiHomeActivity;
import appzone.com.wifipedia.ui.adapters.WifiListAdapter;
import appzone.com.wifipedia.utility.PasswordDialog;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Hari on 16/11/16.
 */

public class WifiListFragment extends Fragment implements WifiListContract.View{

    //UI

    WifiListAdapter wifiListAdapter;
    RecyclerView wifiRecyclerView;
    WifiListContract.UserActions wifiPresenter;

    Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult o1, ScanResult o2) {
            return o2.level - o1.level;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiPresenter = new WifiListPresenter(this, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wifi_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wifiRecyclerView = (RecyclerView) view.findViewById(R.id.wifiList_rv);

        //Set UI
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity());
        wifiRecyclerView.setLayoutManager(layoutManager);
        wifiListAdapter = new WifiListAdapter(wifiPresenter.filterScanResults(), getActivity(), wifiPresenter);
        wifiRecyclerView.setAdapter(wifiListAdapter);
    }

    @Override
    public void showPasswordDialog(ScanResult scanResult) {
        PasswordDialog passwordDialog = new PasswordDialog(scanResult, wifiPresenter);
        passwordDialog.show(getActivity().getSupportFragmentManager(), "WifiHomeActivity");
    }

    @Override
    public void showProgressDialog(String message) {
        ((WifiHomeActivity) getActivity()).showProgressDialog(message);
    }

    @Override
    public void onConnecting() {
        showProgressDialog("Connecting...");
    }

    @Override
    public void onWifiConnected() {
        dismissProgressDialog();
        onRefreshList();
    }

    @Override
    public void dismissProgressDialog() {
        ((WifiHomeActivity) getActivity()).dismissProgressDialog();
    }

    @Override
    public void onRefreshList() {
        refreshRecyclerView();
    }

    @Override
    public void launchMoreInfoActivity() {
        Intent intent = new Intent(getActivity(), WifiConnectedActivity.class);
        startActivity(intent);
    }

    public void refreshRecyclerView() {
        List<ScanResult> scanResults = wifiPresenter.filterScanResults();
        Collections.sort(scanResults, comparator);
        wifiListAdapter.setWifiBeans(scanResults);
        wifiListAdapter.notifyDataSetChanged();
    }
}
