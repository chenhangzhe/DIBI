package cn.suozhi.DiBi.home.model;

/**
 * 查询版本
 */
public class VersionEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {}
     */

    private int code;
    private String msg;
    private DataEntity data;

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

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * createTime : 2019-08-02 11:19:50
         * updateTime : null
         * versionId : 2
         * fromDate : 2019-08-02T00:00:00.000+0800
         * description : tesssst
         * platform : 2
         * version : 1.0.0
         * isMandatoryUpdate : 1
         * downloadUrl : http://imtt.dd.qq.18BB16=com.youba.barcode_3.2.5_37.apk
         */

        private String createTime;
        private Object updateTime;
        private Integer versionId;
        private String fromDate;
        private String description;
        private Integer platform;
        private String version;
        private Integer isMandatoryUpdate;
        private String downloadUrl;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public Integer getVersionId() {
            return versionId;
        }

        public void setVersionId(Integer versionId) {
            this.versionId = versionId;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getPlatform() {
            return platform;
        }

        public void setPlatform(Integer platform) {
            this.platform = platform;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getIsMandatoryUpdate() {
            return isMandatoryUpdate == null ? 0 : isMandatoryUpdate;
        }

        public void setIsMandatoryUpdate(Integer isMandatoryUpdate) {
            this.isMandatoryUpdate = isMandatoryUpdate;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
