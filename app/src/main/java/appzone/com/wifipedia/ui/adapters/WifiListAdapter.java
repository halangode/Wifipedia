package appzone.com.wifipedia.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.contract.WifiListContract;
import appzone.com.wifipedia.ui.activities.WifiHomeActivity;
import appzone.com.wifipedia.ui.beans.WifiCapabilityBean;
import appzone.com.wifipedia.ui.fragments.MoreInfoFragment;
import appzone.com.wifipedia.utility.Utility;
import appzone.com.wifipedia.utility.WifiFactory;
import appzone.com.wifipedia.utility.WifiHelper;

/**
 * Created by Hari on 06/08/16.
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.MyViewHolder>{

    List<ScanResult> wifiBeans;
    Context context;
    WifiCapabilityBean wifiCapabilityBean = new WifiCapabilityBean();
    private int signal;
    WifiListContract.UserActions wifiListPresenter;


    public WifiListAdapter(List<ScanResult> wifiBeans, Context context, WifiListContract.UserActions wifiListPresenter) {
        this.wifiBeans = wifiBeans;
        this.context = context;
        this.wifiListPresenter = wifiListPresenter;
    }

    public void setWifiBeans(List<ScanResult> wifiBeans) {
        this.wifiBeans = wifiBeans;
    }

    public void clearWifiList(){
        this.wifiBeans.clear();
    }

    @Override
    public WifiListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WifiListAdapter.MyViewHolder holder, int position) {

        final ScanResult wifiBean = wifiBeans.get(position);
        holder.ssidTextView.setText(wifiBean.SSID);
        signal = WifiManager.calculateSignalLevel(wifiBean.level, 100);
        holder.rssiTextView.setText(Integer.toString(signal) + "%");
        holder.rssiTextView.setTextColor(setColorRange(signal));
        holder.bssidTextView.setText(wifiBean.BSSID.toString());

        holder.channelTextView.setText(Integer.toString(Utility.convertFrequencyToChannel(wifiBean.frequency)));
        wifiCapabilityBean = WifiHelper.convertScanResult(wifiBean);
        holder.securityTextView.setText(wifiCapabilityBean.getSecurity());
        holder.authenticationTextView.setText(wifiCapabilityBean.getAuthentication());
        holder.encryptionTextView.setText(wifiCapabilityBean.getEncryption());
        holder.keyManagementTextView.setText(wifiCapabilityBean.getArchitecture());

        if(WifiFactory.getWifiManager().getConnectionInfo().getSSID().replace("\"", "").equals(wifiBean.SSID.toString()) &&
                WifiFactory.getWifiManager().getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED){
            Collections.swap(wifiBeans, position, 0);
            holder.connectButton.setText("Disconnect");
            holder.connectButton.setBackgroundColor(context.getResources().getColor(R.color.colorConnected));
            holder.ssidTextView.setTextColor(context.getResources().getColor(R.color.colorConnected));
            holder.moreInfoButton.setVisibility(Button.VISIBLE);

        }
        else {
            holder.connectButton.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.ssidTextView.setTextColor(Color.BLACK);
            holder.moreInfoButton.setVisibility(Button.GONE);
            holder.connectButton.setText("Connect");
        }

        holder.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.connectButton.getText().toString().equals("Connect")){
                    wifiListPresenter.onConnectClicked(wifiBean);
                }
                else {
                    wifiListPresenter.onDisconnectClicked();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return wifiBeans.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ssidTextView;
        TextView rssiTextView;
        TextView bssidTextView;
        TextView channelTextView;
        TextView securityTextView;
        TextView authenticationTextView;
        TextView encryptionTextView;
        TextView keyManagementTextView;
        Button connectButton;
        Button moreInfoButton;
        LinearLayout outerLayout;
        LinearLayout hiddenLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            ssidTextView = (TextView) itemView.findViewById(R.id.wifissid);
            rssiTextView = (TextView) itemView.findViewById(R.id.wifirssi);
            bssidTextView = (TextView) itemView.findViewById(R.id.bssidTV);
            channelTextView = (TextView) itemView.findViewById(R.id.channelTV);
            securityTextView = (TextView) itemView.findViewById(R.id.securityTV);
            authenticationTextView = (TextView) itemView.findViewById(R.id.authenticationTV);
            encryptionTextView = (TextView) itemView.findViewById(R.id.encryptionTV);
            keyManagementTextView = (TextView) itemView.findViewById(R.id.typeTV);
            connectButton = (Button) itemView.findViewById(R.id.connectButton);
            moreInfoButton = (Button) itemView.findViewById(R.id.moreInfoButton);
            outerLayout = (LinearLayout) itemView.findViewById(R.id.outerLayout);
            hiddenLayout = (LinearLayout) itemView.findViewById(R.id.LLayoutHidden);

            outerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleVisibilityOfHiddenLayout(hiddenLayout);
                }
            });
            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wifiListPresenter.onMoreInfoClicked();
                }
            });
        }
    }

    private void toggleVisibilityOfHiddenLayout(LinearLayout hiddenLayout){
        if(hiddenLayout.getVisibility() == View.GONE || hiddenLayout.getVisibility() == View.INVISIBLE){
            hiddenLayout.setVisibility(View.VISIBLE);
        } else if(hiddenLayout.getVisibility() == View.VISIBLE){
            hiddenLayout.setVisibility(View.GONE);
        }
    }

    private int setColorRange(int strength){

        if(strength > 75 && strength < 100){
            return context.getResources().getColor(R.color.colorHighRange);
        }
        else if(strength > 50 && strength < 75){
            return context.getResources().getColor(R.color.colorMidRange);
        }
        else if(strength > 25 && strength < 50){
            return context.getResources().getColor(R.color.colorAvgRange);
        }
        else if(strength > 0 && strength < 25){
            return context.getResources().getColor(R.color.colorLowRange);
        }

        return Color.BLACK;
    }
}
