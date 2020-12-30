package cn.suozhi.DiBi.wallet.model;

import java.util.List;

public class CurrenciesBean {

    /**
     * code : 0
     * msg : 成功
     * data : [{"id":34,"code":"BITP","precis":4,"icon":"https://www.dibic.net/img//images/common/2020-11-05/160454373999952708.png"}]
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
         * id : 34
         * code : BITP
         * precis : 4
         * icon : https://www.dibic.net/img//images/common/2020-11-05/160454373999952708.png
         */

        private int id;
        private String code;
        private int precis;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getPrecis() {
            return precis;
        }

        public void setPrecis(int precis) {
            this.precis = precis;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", code='" + code + '\'' +
                    ", precis=" + precis +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CurrenciesBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
