package cn.suozhi.DiBi.home.model;

/**
 * 中国国籍C2认证人脸识别
 */
public class FaceTokenEntity {

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
         * verifyToken : 58054e
         * verifyPageUrl : null
         * bizId : 10101841
         */

        private String verifyToken;
        private String verifyPageUrl;
        private String bizId;

        public String getVerifyToken() {
            return verifyToken;
        }

        public void setVerifyToken(String verifyToken) {
            this.verifyToken = verifyToken;
        }

        public String getVerifyPageUrl() {
            return verifyPageUrl;
        }

        public void setVerifyPageUrl(String verifyPageUrl) {
            this.verifyPageUrl = verifyPageUrl;
        }

        public String getBizId() {
            return bizId;
        }

        public void setBizId(String bizId) {
            this.bizId = bizId;
        }
    }
}
