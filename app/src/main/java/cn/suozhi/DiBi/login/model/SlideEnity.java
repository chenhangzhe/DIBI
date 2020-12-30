package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

public class SlideEnity implements Serializable {
    /**
     * nc_token : FFFF0N0N0000000057D2
     * sessionid : 01xFg-uWO10FRE471N5_NKpbt5DZKy643ZZJ_Y2_Nx-xpfQo0HCbsui-WnGN38_-rgmwpZitPdb80o-vzDqhTsr-QDv-MYU043O9UGf1GYkcgwAUyPcau_jHQEWBD3uCNDRq1RuZbTh5_hwRDMEHP7ED1tGbs_6viOrz44azvKZp4
     * sig : 05XBa8EtGVqtX0rzvVYJMIN--4ZM26LzWAV0R9ZeriNv0xm-jver1UCDzrU7p9bG8qVHpxxKeYZOYxZCETJjQj0V5phKli79vE2R_IXhpmFNuqomxZsADqzp3nZPDv_DwKB3ipoZN-aJ9oUFAOsWMAXEN_3MtTUKlnDipOFBsjZr65KAWKawxj7Puo9fHG7-dja4ve9RfRWGth_CU4GCBUcES-M0STAmeOLeqxbhzEPlwuQa1-xV5pHi6PSDUzL8lf_TJyVdHYcQ27HsW6jdleA2_FRjsjULHIFLkAx_Lijz5zaA9SEvddO8sVyH0Gga16LZoqkBdM5xRVZk0U_hl9wBB18JdAfomJJ6wd1_3JZb8UiT6Qw3jMczWin9vzpPTC
     */

    private String nc_token;
    private String sessionid;
    private String sig;

    public String getNc_token() {
        return nc_token;
    }

    public void setNc_token(String nc_token) {
        this.nc_token = nc_token;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
