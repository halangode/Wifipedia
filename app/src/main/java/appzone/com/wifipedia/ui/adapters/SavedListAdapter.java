package appzone.com.wifipedia.ui.adapters;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.presenter.contract.SavedListContract;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Hari on 17/11/16.
 */

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.MyViewHolder> {

    Context context;
    List<WifiConfiguration> wifiConfigurations = new ArrayList<>();
    WifiEnterpriseConfig wifiEnterpriseConfig = new WifiEnterpriseConfig();
    SavedListContract.UserAction savedListPresenter;


    public SavedListAdapter(Context context, List<WifiConfiguration> wifiConfigurations, SavedListContract.UserAction savedListPresenter) {
        this.wifiConfigurations = wifiConfigurations;
        this.context = context;
        this.savedListPresenter = savedListPresenter;
    }

    public void setWifiConfigurations(List<WifiConfiguration> wifiList){
        this.wifiConfigurations = wifiList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_wifi_row, parent, false);

        return new SavedListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final WifiConfiguration wifiConfiguration = wifiConfigurations.get(position);
        wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;

        holder.ssid.setText(wifiConfiguration.SSID.replace("\"", ""));

        if((Integer)wifiConfiguration.priority != null)
            holder.priority.setText(wifiConfiguration.priority + "");

        if((Integer)wifiConfiguration.networkId != null)
            holder.netId.setText(String.valueOf(wifiConfiguration.networkId));

        holder.forgetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = savedListPresenter.forgetNetwork(wifiConfiguration.networkId);
                if(!flag){
                    Toast.makeText(context, "This network was not added using this app", Toast.LENGTH_SHORT).show();
                    savedListPresenter.showAlertDialog();
                }

                Toast.makeText(context, "Network removed successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(wifiConfigurations != null)
            return wifiConfigurations.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ssid;
        TextView netId;
        TextView priority;
        TextView forgetTV;


        public MyViewHolder(View itemView) {
            super(itemView);

            ssid = (TextView) itemView.findViewById(R.id.wifissid);
            priority = (TextView) itemView.findViewById(R.id.priority_tv);
            netId = (TextView) itemView.findViewById(R.id.netID_tv);
            forgetTV = (TextView) itemView.findViewById(R.id.forgetTV);

        }
    }
}
