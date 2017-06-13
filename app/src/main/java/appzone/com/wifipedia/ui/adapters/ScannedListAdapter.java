package appzone.com.wifipedia.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import appzone.com.wifipedia.R;
import appzone.com.wifipedia.ui.beans.ScannedBean;

/**
 * Created by Hari on 17/11/16.
 */

public class ScannedListAdapter extends RecyclerView.Adapter<ScannedListAdapter.MyViewHolder> {

    Context context;

    ArrayList<ScannedBean> wifiList = new ArrayList<>();



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleVisibilityOfHiddenLayout(view);
        }
    };

    public ScannedListAdapter(Context context, ArrayList<ScannedBean> wifiList) {
        this.wifiList = wifiList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scanned_list_row, parent, false);

        itemView.setOnClickListener(onClickListener);
        return new ScannedListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ScannedBean scannedBean = wifiList.get(position);
        holder.wifiIpTV.setText(scannedBean.getIpAddress());
        holder.macAddTV.setText(scannedBean.getMacAddress().replace('-', ':').toUpperCase());
        holder.vendorTV.setText(scannedBean.getVendor());
        holder.hostName.setText(scannedBean.getHostName());
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView wifiIpTV;
        TextView macAddTV;
        TextView vendorTV;
        TextView hostName;
        TextView osTV;


        public MyViewHolder(View itemView) {
            super(itemView);

            wifiIpTV = (TextView) itemView.findViewById(R.id.wifi_ip_tv);
            macAddTV = (TextView) itemView.findViewById(R.id.madAddress_tv);
            vendorTV = (TextView) itemView.findViewById(R.id.vendor_tv);
            hostName = (TextView) itemView.findViewById(R.id.device_tv);
            osTV = (TextView) itemView.findViewById(R.id.os_tv);
        }
    }

    private void toggleVisibilityOfHiddenLayout(View view){
        LinearLayout hiddenLayout = (LinearLayout) view.findViewById(R.id.LLayoutHidden);

        if(hiddenLayout.getVisibility() == View.GONE || hiddenLayout.getVisibility() == View.INVISIBLE){
            hiddenLayout.setVisibility(View.VISIBLE);
        } else if(hiddenLayout.getVisibility() == View.VISIBLE){
            hiddenLayout.setVisibility(View.GONE);
        }
    }

}
