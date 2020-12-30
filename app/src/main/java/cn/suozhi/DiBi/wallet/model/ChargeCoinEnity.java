package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;

/**
 * 充币
 */
public class ChargeCoinEnity {
    /**
     * code : 0
     * msg : 成功
     * data : {"address":"fasfsfsafasfsdaf","tag":null}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
         * address : fasfsfsafasfsdaf
         * tag : null
         */

        private String address;
        private String tag;
        private String tagDescribe;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTagDescribe() {
            return tagDescribe;
        }

        public void setTagDescribe(String tagDescribe) {
            this.tagDescribe = tagDescribe;
        }
    }
}
