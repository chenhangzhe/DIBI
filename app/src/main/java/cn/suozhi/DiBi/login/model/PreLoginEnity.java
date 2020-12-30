package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

/**
 * Created by DELL on 2019/7/10.
 */

public class PreLoginEnity {
    /**
     * code : 0
     * msg : 成功
     * data : {"preToken":"5aebaa46-3fbc-4add-95e3-7f37ac662be6","bindStatus":{"phoneEnabled":1,"emailEnabled":0,"gaEnabled":0}}
     */

    private long code;
    private String msg;
    private DataBean data;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * preToken : 5aebaa46-3fbc-4add-95e3-7f37ac662be6
         * bindStatus : {"phoneEnabled":1,"emailEnabled":0,"gaEnabled":0}
         */

        private String preToken;
        private BindStatusBean bindStatus;

        public String getPreToken() {
            return preToken;
        }

        public void setPreToken(String preToken) {
            this.preToken = preToken;
        }

        public BindStatusBean getBindStatus() {
            return bindStatus;
        }

        public void setBindStatus(BindStatusBean bindStatus) {
            this.bindStatus = bindStatus;
        }

        public static class BindStatusBean implements Serializable {
            /**
             * phoneEnabled : 1
             * emailEnabled : 0
             * gaEnabled : 0
             */

            private int phoneEnabled;
            private int emailEnabled;
            private int gaEnabled;

            public int getPhoneEnabled() {
                return phoneEnabled;
            }

            public void setPhoneEnabled(int phoneEnabled) {
                this.phoneEnabled = phoneEnabled;
            }

            public int getEmailEnabled() {
                return emailEnabled;
            }

            public void setEmailEnabled(int emailEnabled) {
                this.emailEnabled = emailEnabled;
            }

            public int getGaEnabled() {
                return gaEnabled;
            }

            public void setGaEnabled(int gaEnabled) {
                this.gaEnabled = gaEnabled;
            }
        }
    }
}
