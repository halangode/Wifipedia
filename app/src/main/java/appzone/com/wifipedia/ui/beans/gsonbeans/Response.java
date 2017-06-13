package appzone.com.wifipedia.ui.beans.gsonbeans;

import java.util.ArrayList;

/**
 * Created by Hari on 22/11/16.
 */

public class Response {

    ArrayList<VendorDetails> vendorDetails;

    public Response(ArrayList<VendorDetails> vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public ArrayList<VendorDetails> getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(ArrayList<VendorDetails> vendorDetails) {
        this.vendorDetails = vendorDetails;
    }
}
