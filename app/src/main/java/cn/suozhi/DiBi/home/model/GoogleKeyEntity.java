package cn.suozhi.DiBi.home.model;

/**
 * 获取谷歌安全绑定Key
 */
public class GoogleKeyEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {}
     */

    private long code;
    private String msg;
    private DataEntity data;

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

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * gaKey : 4JFARHZVRRIEZOEH
         * qrCodeUrl : otpauth://totp/1226428HZVRRIEZOEH&issuer=VVBTC
         */

        private String gaKey;
        private String qrCodeUrl;

        public String getGaKey() {
            return gaKey;
        }

        public void setGaKey(String gaKey) {
            this.gaKey = gaKey;
        }

        public String getQrCodeUrl() {
            return qrCodeUrl;
        }

        public void setQrCodeUrl(String qrCodeUrl) {
            this.qrCodeUrl = qrCodeUrl;
        }
    }
}
