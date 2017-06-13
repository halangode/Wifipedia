package appzone.com.wifipedia.utility;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import appzone.com.wifipedia.ui.beans.WifiCapabilityBean;


/**
 * Created by Hari on 13/08/16.
 */
public class WifiHelper{

    private static final String TAG = "WifiLocationStatus";
    private static int URL_TIMEOUT = 5000;

    public static WifiCapabilityBean convertScanResult(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        WifiCapabilityBean wifiCapabilityBean = new WifiCapabilityBean();
        String filtered = capabilities.replaceAll("\\[", "").replaceAll("\\]", "-");
        String[] capabilityArray = filtered.split("-");

        ArrayList<String> capabilityList = new ArrayList<>(Arrays.asList(capabilityArray));

        wifiCapabilityBean.setSecurity(getScanResultSecurity(capabilityList));
        wifiCapabilityBean.setAuthentication(getScanResultAuthentication(capabilityList));
        wifiCapabilityBean.setEncryption(getScanResultAEncryption(capabilityList));
        wifiCapabilityBean.setArchitecture(getScanResultArchitecture(capabilityList));
        return wifiCapabilityBean;
    }

    public static boolean connectWiFi(ScanResult scanResult, String networkPasskey) {
        try {
            WifiManager wifiManager = WifiFactory.getWifiManager();
            Log.v("rht", "Item clicked, SSID " + scanResult.SSID + " Security : " + scanResult.capabilities);

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + scanResult.SSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPasskey.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPasskey;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPasskey).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPasskey + "\"";

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + scanResult.SSID + "\"")) {
                    Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = wifiManager.disconnect();
                    Log.v("rht", "isDisconnected : " + isDisconnected);

                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.v("rht", "isEnabled : " + isEnabled);

                    boolean isReconnected = wifiManager.reconnect();
                    Log.v("rht", "isReconnected : " + isReconnected);

                    if(isEnabled || isReconnected){
                        return true;
                    }

                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;

    }

    public static ArrayList<ScanResult> filterResult(ArrayList<ScanResult> scanResults, String filter) {

        for (int i = 0; i < scanResults.size(); i++) {
            if (!scanResults.get(i).SSID.toLowerCase().contains(filter.toLowerCase()))
                scanResults.remove(i);
        }

        return scanResults;
    }

    public static String getMacAddress(InetAddress inetAddress) {


        StringBuilder sb = new StringBuilder();
        try {

            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);

            if (network != null) {
                byte[] mac = new byte[0];
                mac = network.getHardwareAddress();
                sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
            } else return null;
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    public static String getMacFromArpCache(String ip) {
        if (ip == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String httpConnection(String connectUrl, String macAddress) {
        try {
            String macEncoded = URLEncoder.encode(macAddress, "utf-8");
            URL url = new URL(connectUrl + macEncoded);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(URL_TIMEOUT);

            return readInputStreamToString(urlConnection);
        } catch (SocketTimeoutException s) {
            Log.e("ERROR", s.getMessage(), s);
            return null;

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }

    }

    public static String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            Log.i("Result", sb.toString());

            result = sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "Error reading InputStream");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }

    public static String getHostName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    /**
     * @return The security of a given {@link ScanResult}.
     * @param scanResult
     */
    public static String getScanResultSecurity(ArrayList<String> scanResult) {
        ArrayList<String> securityCurrent = new ArrayList<>();
        final String[] securityModes = { "WEP", "WPA", "WPA2", "WPA-EAP", "IEEE8021X" };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (scanResult.contains(securityModes[i])) {
                securityCurrent.add(securityModes[i]);
            }
        }

        if(securityCurrent.size() == 0)
            return "OPEN";
        else
        {
            String security = "";
            for(int i = 0; i<securityCurrent.size(); i++){
                security += securityCurrent.get(i) + "/";
            }
            return security.substring(0, security.length() - 1);
        }
    }

    public static String getScanResultAuthentication(ArrayList<String> scanResult) {
        final String[] securityModes = {"PSK"};
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (scanResult.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return "Unknown";
    }

    public static String getScanResultAEncryption(ArrayList<String> scanResult) {
        ArrayList<String> encryptionList = new ArrayList<>();
        final String[] encryptionModes = {"CCMP", "TKIP", "AES", "CCMP+TKIP"};
        for (int i = encryptionModes.length - 1; i >= 0; i--) {
            if (scanResult.contains(encryptionModes[i])) {
                encryptionList.add(encryptionModes[i]);
            }
        }

        if(encryptionList.size() == 0)
            return "None";
        else
        {
            String encryption = "";
            for(int i = 0; i<encryptionList.size(); i++){
                encryption += encryptionList.get(i) + "/";
            }
            return encryption.substring(0, encryption.length() - 1);
        }

    }

    public static String getScanResultArchitecture(ArrayList<String> scanResult) {
        final String[] securityModes = {"IBSS", "ESS", "BSS"};
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (scanResult.contains(securityModes[i])) {
                if(securityModes[i].equals("IBSS"))
                return "Ad-Hoc";
                else
                    return securityModes[i];
            }
        }

        return "Unknown";
    }

    /**
     * @return Whether the given ScanResult represents an adhoc network.
     */
    public static boolean isAdhoc(ScanResult scanResult) {
        return scanResult.capabilities.contains("ADHOC_CAPABILITY");
    }


}

