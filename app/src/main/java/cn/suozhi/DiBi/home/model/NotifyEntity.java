package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 公告
 */
public class NotifyEntity {

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
         * records : []
         * total : 1
         * size : 1
         * current : 1
         * searchCount : true
         * pages : 1
         */

        private int total;
        private int size;
        private int current;
        private boolean searchCount;
        private int pages;
        private List<RecordsEntity> records;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<RecordsEntity> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsEntity> records) {
            this.records = records;
        }

        public static class RecordsEntity {
            /**
             * id : 41
             * author : admin
             * title : 如何评价李彦宏在被泼水后的表现？
             * content : null
             * introduction : 如何评价李彦宏在被泼水后的表现？
             * pictureUrl : http://47.244.45.163:81/images/article/2019-07-09/156263782987166237.jpg
             * bgPictureUrl : null
             * isTop : false
             * pv : 0
             * initPv : null
             * language : zh_CN
             * source : null
             * sourceLink :
             * startDate : null
             * endDate : null
             * category : 22
             * categoryName : 系统公告
             * seoKeyWord :
             * seoRemark :
             * createTime : 2019-07-09 02:02:24
             * updateTime : 2019-07-09 02:04:14
             * createBy : 1
             * updateBy : 1
             * effectiveStartTime : 2019-06-30 16:00:00
             * effectiveEndTime : 2019-08-27 16:00:00
             */

            private long id;
            private String author;
            private String title;
            private String content;
            private String introduction;
            private String pictureUrl;
            private String bgPictureUrl;
            private Boolean isTop;
            private int pv;
            private int likeNum;
            private int notLikeNum;
            private Object initPv;
            private String language;
            private Object source;
            private String sourceLink;
            private Object startDate;
            private Object endDate;
            private int category;
            private String categoryName;
            private String seoKeyWord;
            private String seoRemark;
            private String createTime;
            private String updateTime;
            private String createBy;
            private String updateBy;
            private String effectiveStartTime;
            private String effectiveEndTime;

            private boolean isNew;
            private int type;

            public RecordsEntity(int type) {
                this.type = type;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
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

            public String getIntroduction() {
                return introduction;
            }

            public void setIntroduction(String introduction) {
                this.introduction = introduction;
            }

            public String getPictureUrl() {
                return pictureUrl;
            }

            public void setPictureUrl(String pictureUrl) {
                this.pictureUrl = pictureUrl;
            }

            public String getBgPictureUrl() {
                return bgPictureUrl;
            }

            public void setBgPictureUrl(String bgPictureUrl) {
                this.bgPictureUrl = bgPictureUrl;
            }

            public Boolean getTop() {
                return isTop;
            }

            public void setTop(Boolean top) {
                isTop = top;
            }

            public int getPv() {
                return pv;
            }

            public void setPv(int pv) {
                this.pv = pv;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public void setLikeNum(int likeNum) {
                this.likeNum = likeNum;
            }

            public int getNotLikeNum() {
                return notLikeNum;
            }

            public void setNotLikeNum(int notLikeNum) {
                this.notLikeNum = notLikeNum;
            }

            public Object getInitPv() {
                return initPv;
            }

            public void setInitPv(Object initPv) {
                this.initPv = initPv;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public Object getSource() {
                return source;
            }

            public void setSource(Object source) {
                this.source = source;
            }

            public String getSourceLink() {
                return sourceLink;
            }

            public void setSourceLink(String sourceLink) {
                this.sourceLink = sourceLink;
            }

            public Object getStartDate() {
                return startDate;
            }

            public void setStartDate(Object startDate) {
                this.startDate = startDate;
            }

            public Object getEndDate() {
                return endDate;
            }

            public void setEndDate(Object endDate) {
                this.endDate = endDate;
            }

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getSeoKeyWord() {
                return seoKeyWord;
            }

            public void setSeoKeyWord(String seoKeyWord) {
                this.seoKeyWord = seoKeyWord;
            }

            public String getSeoRemark() {
                return seoRemark;
            }

            public void setSeoRemark(String seoRemark) {
                this.seoRemark = seoRemark;
            }

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

            public boolean isNew() {
                return isNew;
            }

            public void setNew(boolean aNew) {
                isNew = aNew;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
