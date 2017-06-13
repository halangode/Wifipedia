package appzone.com.wifipedia.ui.beans;

/**
 * Created by Hari on 22/11/16.
 */

public class ScannedBean {

    String ipAddress;
    String macAddress;
    String vendor;
    String hostName;
    String os;

    public ScannedBean(String ipAddress, String macAddress) {
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getVendor() {
        return vendor;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
