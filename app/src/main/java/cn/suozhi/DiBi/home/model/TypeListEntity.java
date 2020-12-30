package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 功能描述：帮助选择类别
 */
public class TypeListEntity {

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

    public static class DataEntity {
        /**
         * createTime : 2019-06-29 14:32:14
         * updateTime : 2019-07-24 18:31:47
         * id : 18
         * typeName : 新手教程
         * typeCode : beginnerTutorial
         * sort : 11
         * status : 1
         * language : zh_CN
         * parentId : 17
         * description : 基础数据请勿动
         */

        private String createTime;
        private String updateTime;
        private long id;
        private String typeName;
        private String typeCode;
        private int sort;
        private int status;
        private String language;
        private long parentId;
        private String description;

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

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public long getParentId() {
            return parentId;
        }

        public void setParentId(long parentId) {
            this.parentId = parentId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
