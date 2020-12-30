package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 创建时间：2019-07-26 18:45
 * 作者：Lich_Cool
 * 邮箱：licheng@ld.chainsdir.com
 * 功能描述：
 */
public class QEntity {


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
         * value : 1
         * name : 提币
         * icon :
         */

        private int value;
        private String name;
        private String icon;


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
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
    }
}
