package appzone.com.wifipedia.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import appzone.com.wifipedia.ui.fragments.SavedWifiFragment;
import appzone.com.wifipedia.ui.fragments.SettingsPrefFragment;
import appzone.com.wifipedia.ui.fragments.WifiListFragment;
import appzone.com.wifipedia.ui.fragments.WifiSwitchedOffFragment;
import appzone.com.wifipedia.utility.WifiFactory;

/**
 * Created by Hari on 17/11/16.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    public final int pageCount = 3;

    public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                if(WifiFactory.getWifiManager().isWifiEnabled())
                    return new SavedWifiFragment();
                else
                    return new WifiSwitchedOffFragment();

            case 1:
                if(WifiFactory.getWifiManager().isWifiEnabled())
                    return new WifiListFragment();
                else
                    return new WifiSwitchedOffFragment();

            case 2: return new SettingsPrefFragment();


            default: break;

        }

        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof WifiListFragment){
            WifiListFragment wifiListFragment = (WifiListFragment) object;
            wifiListFragment.onRefreshList();
        }
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Saved";
            case 1: return "Live";
            case 2: return "Settings";
            default: return "";
        }
    }
}
