package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 获取下拉列表
 */
public class SelectEntity {

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
         * value : cn
         * name : 中国
         * icon :
         */

        private String value;
        private String name;
        private String icon;
        private String sortLetter;

        public DataEntity(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSortLetter() {
            return sortLetter;
        }

        public void setSortLetter(String sortLetter) {
            this.sortLetter = sortLetter;
        }
    }
}
