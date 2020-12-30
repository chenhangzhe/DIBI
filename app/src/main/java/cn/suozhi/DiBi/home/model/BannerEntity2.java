package cn.suozhi.DiBi.home.model;

import java.util.List;

public class BannerEntity2 {
    /**
     * code : 0
     * msg : 成功
     * data : [{"createTime":"2020-04-02 19:00:42","updateTime":"2020-04-02 19:06:07","bannerId":70,"title":"标题","content":"描述","bannerImgUrl":"https://www.dibic.net/img/images/banner/2020-04-02/158582523156732324.jpg","bannerImgHref":"","effectiveStartTime":"2019-06-11T00:00:00.000+0800","effectiveEndTime":"2020-06-04T00:00:00.000+0800","bannerImgHrefType":2,"qrPosition":null}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * createTime : 2020-04-02 19:00:42
         * updateTime : 2020-04-02 19:06:07
         * bannerId : 70
         * title : 标题
         * content : 描述
         * bannerImgUrl : https://www.dibic.net/img/images/banner/2020-04-02/158582523156732324.jpg
         * bannerImgHref :
         * effectiveStartTime : 2019-06-11T00:00:00.000+0800
         * effectiveEndTime : 2020-06-04T00:00:00.000+0800
         * bannerImgHrefType : 2
         * qrPosition : null
         */

        private String createTime;
        private String updateTime;
        private int bannerId;
        private String title;
        private String content;
        private String bannerImgUrl;
        private String bannerImgHref;
        private String effectiveStartTime;
        private String effectiveEndTime;
        private int bannerImgHrefType;
        private Object qrPosition;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getBannerId() {
            return bannerId;
        }

        public void setBannerId(int bannerId) {
            this.bannerId = bannerId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getBannerImgUrl() {
            return bannerImgUrl;
        }

        public void setBannerImgUrl(String bannerImgUrl) {
            this.bannerImgUrl = bannerImgUrl;
        }

        public String getBannerImgHref() {
            return bannerImgHref;
        }

        public void setBannerImgHref(String bannerImgHref) {
            this.bannerImgHref = bannerImgHref;
        }

        public String getEffectiveStartTime() {
            return effectiveStartTime;
        }

        public void setEffectiveStartTime(String effectiveStartTime) {
            this.effectiveStartTime = effectiveStartTime;
        }

        public String getEffectiveEndTime() {
            return effectiveEndTime;
        }

        public void setEffectiveEndTime(String effectiveEndTime) {
            this.effectiveEndTime = effectiveEndTime;
        }

        public int getBannerImgHrefType() {
            return bannerImgHrefType;
        }

        public void setBannerImgHrefType(int bannerImgHrefType) {
            this.bannerImgHrefType = bannerImgHrefType;
        }

        public Object getQrPosition() {
            return qrPosition;
        }

        public void setQrPosition(Object qrPosition) {
            this.qrPosition = qrPosition;
        }
    }
}
