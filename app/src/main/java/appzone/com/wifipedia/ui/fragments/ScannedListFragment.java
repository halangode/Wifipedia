package appzone.com.wifipedia.ui.fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.ScannedListPresenter;
import appzone.com.wifipedia.presenter.contract.ScannedListContract;
import appzone.com.wifipedia.ui.adapters.ScannedListAdapter;
import appzone.com.wifipedia.ui.beans.ScannedBean;

/**
 * Created by Hari on 22/11/16.
 */

public class ScannedListFragment extends Fragment implements ScannedListContract.View {

    private RecyclerView scannedListRV;
    private ArrayList<ScannedBean> wifiList;
    private ScannedListAdapter scannedListAdapter;
    String ip = "";
    private String inputIp;
    private int lastValue;
    public ProgressDialog progressDialog;

    ScannedListContract.UserActions scannedListPresenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannedListPresenter = new ScannedListPresenter(this);

        wifiList = new ArrayList<>();

        Bundle bundle = getArguments();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Scanning");

        if(bundle != null)
            ip = bundle.getString("ipValue").toString();

        scannedListAdapter = new ScannedListAdapter(getActivity(), wifiList);

        inputIp = ip.substring(0, ip.lastIndexOf('.') + 1);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scanned_list_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialogue();
        scannedListPresenter.onCancelTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgressDialogue();
        scannedListPresenter.onCancelTask();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scannedListRV = (RecyclerView) view.findViewById(R.id.scannedListRV);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        scannedListRV.setLayoutManager(layoutManager);
        scannedListRV.setAdapter(scannedListAdapter);

        lastValue = Integer.valueOf(ip.substring(ip.lastIndexOf('.') + 1));

        scannedListPresenter.onScanWifiDevices(inputIp, lastValue);
    }

    @Override
    public void showProgressDialogue() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialogue() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void setProgress(int value) {
        progressDialog.setProgress(value);
    }

    @Override
    public void foundNewDevice(ScannedBean scannedBean) {
        wifiList.add(scannedBean);
        scannedListAdapter.notifyDataSetChanged();
    }
}
