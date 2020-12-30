package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 获取Banner
 */
public class BannerEntity {

    /**
     * code : 0
     * msg : 成功
     * data : []
     */

    private long code;
    private String msg;
    private List<DataEntity> data;

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

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BannerEntity{" +
                "data=" + data +
                '}';
    }

    public static class DataEntity {
        /**
         * createTime : 2019-07-01 04:34:05
         * updateTime : 2019-07-04 08:03:36
         * createBy : 1
         * updateBy : 1
         * isDel : 0
         * bannerId : 31
         * title : null
         * content : null
         * bannerImgUrl : /banner/2019-07-04/156221617222558559.png
         * bannerImgHref :
         * language : zh_CN
         * effectiveStartTime : null
         * effectiveEndTime : null
         * sort : 4
         * category : 1
         * platform : 2
         * bannerImgHrefType : null
         */

        private String createTime;
        private String updateTime;
        private String createBy;
        private String updateBy;
        private int isDel;
        private int bannerId;
        private String title;
        private Object content;
        private String bannerImgUrl;
        private String bannerImgHref;
        private String language;
        private Object effectiveStartTime;
        private Object effectiveEndTime;
        private int sort;
        private int category;
        private int platform;
        private Integer bannerImgHrefType;
        private boolean isPublished;
        private QrEntity qrPosition;

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

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public int getIsDel() {
            return isDel;
        }

        public void setIsDel(int isDel) {
            this.isDel = isDel;
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

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
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

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Object getEffectiveStartTime() {
            return effectiveStartTime;
        }

        public void setEffectiveStartTime(Object effectiveStartTime) {
            this.effectiveStartTime = effectiveStartTime;
        }

        public Object getEffectiveEndTime() {
            return effectiveEndTime;
        }

        public void setEffectiveEndTime(Object effectiveEndTime) {
            this.effectiveEndTime = effectiveEndTime;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public Integer getBannerImgHrefType() {
            return bannerImgHrefType;
        }

        public void setBannerImgHrefType(Integer bannerImgHrefType) {
            this.bannerImgHrefType = bannerImgHrefType;
        }

        public boolean isPublished() {
            return isPublished;
        }

        public void setPublished(boolean published) {
            isPublished = published;
        }

        public QrEntity getQrPosition() {
            return qrPosition;
        }

        public void setQrPosition(QrEntity qrPosition) {
            this.qrPosition = qrPosition;
        }

        public static class QrEntity {

            private int width;
            private int x;
            private int length;
            private int y;
            private int height;

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "createTime='" + createTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", createBy='" + createBy + '\'' +
                    ", updateBy='" + updateBy + '\'' +
                    ", isDel=" + isDel +
                    ", bannerId=" + bannerId +
                    ", title='" + title + '\'' +
                    ", content=" + content +
                    ", bannerImgUrl='" + bannerImgUrl + '\'' +
                    ", bannerImgHref='" + bannerImgHref + '\'' +
                    ", language='" + language + '\'' +
                    ", effectiveStartTime=" + effectiveStartTime +
                    ", effectiveEndTime=" + effectiveEndTime +
                    ", sort=" + sort +
                    ", category=" + category +
                    ", platform=" + platform +
                    ", bannerImgHrefType=" + bannerImgHrefType +
                    ", isPublished=" + isPublished +
                    ", qrPosition=" + qrPosition +
                    '}';
        }
    }
}
