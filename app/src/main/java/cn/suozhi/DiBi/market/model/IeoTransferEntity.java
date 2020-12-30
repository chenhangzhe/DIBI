package cn.suozhi.DiBi.market.model;

import java.util.List;

public class IeoTransferEntity {

    /**
     * code : 0
     * msg : 成功
     * data : [{"normalCurrencyId":4,"normalCurrencyCode":"USDT","normalShowCurrencyCode":"USDT","specialCurrencyId":22,"specialCurrencyCode":"USDT-E","specialShowCurrencyCode":"USDT","normalAvailableAmount":"100000.00000000","normalFreezeAmount":"98.00000000","specialAvailableAmount":"0","specialFreezeAmount":"0","precis":8},{"normalCurrencyId":20,"normalCurrencyCode":"MOVE","normalShowCurrencyCode":"MOVE","specialCurrencyId":23,"specialCurrencyCode":"MOVE-E","specialShowCurrencyCode":"MOVE","normalAvailableAmount":"0","normalFreezeAmount":"0","specialAvailableAmount":"0","specialFreezeAmount":"0","precis":8}]
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
         * normalCurrencyId : 4     普通币种ID
         * normalCurrencyCode : USDT    普通币种code
         * normalShowCurrencyCode : USDT    普通币种显示code
         *
         * specialCurrencyId : 22     多解一币种ID
         * specialCurrencyCode : USDT-E     多解一币种code
         * specialShowCurrencyCode : USDT     多解一币种显示code
         *
         * normalAvailableAmount : 100000.00000000     普通币种余额
         * normalFreezeAmount : 98.00000000     普通币种冻结
         *
         * specialAvailableAmount : 0     多解一币种余额
         * specialFreezeAmount : 0      多解一币种冻结
         *
         * precis : 8     保留的小数位数
         */

        private int normalCurrencyId;
        private String normalCurrencyCode;
        private String normalShowCurrencyCode;
        private int specialCurrencyId;
        private String specialCurrencyCode;
        private String specialShowCurrencyCode;
        private String normalAvailableAmount;
        private String normalFreezeAmount;
        private String specialAvailableAmount;
        private String specialFreezeAmount;
        private int precis;

        public int getNormalCurrencyId() {
            return normalCurrencyId;
        }

        public void setNormalCurrencyId(int normalCurrencyId) {
            this.normalCurrencyId = normalCurrencyId;
        }

        public String getNormalCurrencyCode() {
            return normalCurrencyCode;
        }

        public void setNormalCurrencyCode(String normalCurrencyCode) {
            this.normalCurrencyCode = normalCurrencyCode;
        }

        public String getNormalShowCurrencyCode() {
            return normalShowCurrencyCode;
        }

        public void setNormalShowCurrencyCode(String normalShowCurrencyCode) {
            this.normalShowCurrencyCode = normalShowCurrencyCode;
        }

        public int getSpecialCurrencyId() {
            return specialCurrencyId;
        }

        public void setSpecialCurrencyId(int specialCurrencyId) {
            this.specialCurrencyId = specialCurrencyId;
        }

        public String getSpecialCurrencyCode() {
            return specialCurrencyCode;
        }

        public void setSpecialCurrencyCode(String specialCurrencyCode) {
            this.specialCurrencyCode = specialCurrencyCode;
        }

        public String getSpecialShowCurrencyCode() {
            return specialShowCurrencyCode;
        }

        public void setSpecialShowCurrencyCode(String specialShowCurrencyCode) {
            this.specialShowCurrencyCode = specialShowCurrencyCode;
        }

        public String getNormalAvailableAmount() {
            return normalAvailableAmount;
        }

        public void setNormalAvailableAmount(String normalAvailableAmount) {
            this.normalAvailableAmount = normalAvailableAmount;
        }

        public String getNormalFreezeAmount() {
            return normalFreezeAmount;
        }

        public void setNormalFreezeAmount(String normalFreezeAmount) {
            this.normalFreezeAmount = normalFreezeAmount;
        }

        public String getSpecialAvailableAmount() {
            return specialAvailableAmount;
        }

        public void setSpecialAvailableAmount(String specialAvailableAmount) {
            this.specialAvailableAmount = specialAvailableAmount;
        }

        public String getSpecialFreezeAmount() {
            return specialFreezeAmount;
        }

        public void setSpecialFreezeAmount(String specialFreezeAmount) {
            this.specialFreezeAmount = specialFreezeAmount;
        }

        public int getPrecis() {
            return precis;
        }

        public void setPrecis(int precis) {
            this.precis = precis;
        }
    }
}
