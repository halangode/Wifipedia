package appzone.com.wifipedia.ui.beans;

/**
 * Created by Hari on 13/08/16.
 */
public class WifiCapabilityBean {
    String security;
    String authentication;
    String encryption;
    String architecture;


    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getSecurity() {
        if(security != null){
            return security;
        }
        else {
            return "None";
        }
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getEncryption() {
        if(encryption != null){
            return encryption;
        }
        else {
            return "None";
        }
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getArchitecture() {
        if(architecture != null){
            return architecture;
        }
        else {
            return "None";
        }
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }
}
