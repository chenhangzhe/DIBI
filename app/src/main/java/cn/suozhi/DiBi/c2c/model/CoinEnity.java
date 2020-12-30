package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;
import java.util.List;

public class CoinEnity {


    /**
     * code : 0
     * msg : 成功
     * data : [{"value":"CNY","name":"CNY","icon":""},{"value":"USD","name":"USD","icon":""}]
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

    public static class DataBean implements Serializable {
        /**
         * value : CNY
         * name : CNY
         * icon :
         */

        private String value;
        private String name;
        private String icon;

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
    }
}
