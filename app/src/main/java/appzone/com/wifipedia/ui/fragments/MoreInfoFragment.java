package appzone.com.wifipedia.ui.fragments;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.MoreInfoPresenter;
import appzone.com.wifipedia.presenter.contract.MoreInfoContract;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Hari on 16/11/16.
 */

public class MoreInfoFragment extends Fragment implements MoreInfoContract.View {


    //UI
    TextView connectedSSIDTV;
    TextView bssidTV;
    TextView frequencyTV;
    TextView ipAddressTV;
    TextView linkSpeedTV;
    TextView networkIDTV;

    //network

    WifiInfo wifiInfo;
    private InetAddress myaddr;
    private LinkProperties linkProperties;
    private TextView interfaceTV;
    private NetworkCapabilities networkCapability;
    private TextView uploadBandwidthTV;
    private TextView downloadBandwidthTV;
    private Button scanNetworkButton;
    private ConnectivityManager connectivityManager;

    private MoreInfoContract.UserAction moreInfoPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.more_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moreInfoPresenter = new MoreInfoPresenter(this);
        initializeViews(view);

        wifiInfo = WifiFactory.getWifiManager().getConnectionInfo();
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);


        linkProperties = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork());
        networkCapability = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        int ip = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ?
                Integer.reverseBytes(wifiInfo.getIpAddress()) : wifiInfo.getIpAddress();


        try {
            byte[] ipAddress = BigInteger.valueOf(ip).toByteArray();

            myaddr = InetAddress.getByAddress(ipAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        connectedSSIDTV.setText(wifiInfo.getSSID().replace("\"", ""));
        bssidTV.setText(wifiInfo.getBSSID());
        frequencyTV.setText(wifiInfo.getFrequency() + "MHz");
        ipAddressTV.setText(myaddr.getHostAddress() + "");
        linkSpeedTV.setText(wifiInfo.getLinkSpeed() + "Mbps");
        networkIDTV.setText(wifiInfo.getNetworkId() + "");
        interfaceTV.setText(linkProperties.getInterfaceName());
        uploadBandwidthTV.setText(getSpeedMeasure(networkCapability.getLinkUpstreamBandwidthKbps()));
        downloadBandwidthTV.setText(getSpeedMeasure(networkCapability.getLinkDownstreamBandwidthKbps()));

        scanNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScannedListFragment scannedListFragment = new ScannedListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("ipValue", myaddr.getHostAddress());
                scannedListFragment.setArguments(bundle);

                moreInfoPresenter.onScanNetworkClicked(myaddr.getHostAddress());
            }
        });



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initializeViews(View view){
        connectedSSIDTV = (TextView) view.findViewById(R.id.connectedToTV);
        bssidTV = (TextView) view.findViewById(R.id.bssid_tv);
        frequencyTV = (TextView) view.findViewById(R.id.freqTV);
        ipAddressTV = (TextView) view.findViewById(R.id.ipTV);
        linkSpeedTV = (TextView) view.findViewById(R.id.linkSpeedTV);
        networkIDTV = (TextView) view.findViewById(R.id.networkIDTV);
        interfaceTV = (TextView) view.findViewById(R.id.interfaceTV);
        uploadBandwidthTV = (TextView) view.findViewById(R.id.upBandwithTV);
        downloadBandwidthTV = (TextView) view.findViewById(R.id.downBandwidthTV);
        scanNetworkButton = (Button) view.findViewById(R.id.scanNetworkButton);
    }

    public String getSpeedMeasure(int speed){

        String unit = "";
        int convertedSpeed = 0;

        if(speed >= 1024 && speed < 1024*1024){
            unit = "Kbps";
            convertedSpeed = speed;
        }

        else if(speed >= 1024*1024 && speed < 1024*1024*1024){
            unit = "Mbps";
            convertedSpeed = (speed)/1024;
        }
        else if(speed >= 1024*1024*1024 && speed < 1024*1024*1024*1024){
            unit = "Gbps";
            convertedSpeed = (speed)/(1024*1024);
        }

        return String.valueOf(convertedSpeed) + unit;
    }

    @Override
    public void showScannedListFragment(String hostAddr) {
        ScannedListFragment scannedListFragment = new ScannedListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ipValue", hostAddr);
        scannedListFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scannedListFragment)
                .addToBackStack("More")
                .commit();
    }
}
