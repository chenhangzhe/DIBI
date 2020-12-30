package cn.suozhi.DiBi.login.model;

import java.io.Serializable;
import java.util.List;

/**
 * 获取Banner
 */
public class PhoneCodeEntity {

    /**
     * code : 0
     * msg : 成功
     * data : [{"areaCode":"+86","description":"中国"}]
     */

    private long code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * areaCode : +86
         * description : 中国
         */
        private String mSortLetters;
        private String value;
        private String name;
        private String icon;

        public String getAreaCode() {
            return value;
        }

        public void setAreaCode(String areaCode) {
            this.value = areaCode;
        }

        public String getDescription() {
            return name;
        }

        public void setDescription(String description) {
            this.name = description;
        }

        public String getmSortLetters() {
            return mSortLetters;
        }

        public void setmSortLetters(String mSortLetters) {
            this.mSortLetters = mSortLetters;
        }
    }
}
