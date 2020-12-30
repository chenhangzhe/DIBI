package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;

public class CoinListEntity {

    /**
     * code : 0
     * msg : 成功
     * data : [{"id":4,"code":"USDT","precis":8,"icon":"http://106.13.25.151/img//images/common/2020-03-21/158479727321944040.png"},{"id":16,"code":"DIC","precis":8,"icon":"http://106.13.25.151/img//images/common/2019-07-30/156446774913571310.png"}]
     */

    private int code;
    private String msg;
    private List<DataEntity> data;

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

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * id : 4
         * code : USDT
         * precis : 8
         * icon : http://106.13.25.151/img//images/common/2020-03-21/158479727321944040.png
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
    }
}
