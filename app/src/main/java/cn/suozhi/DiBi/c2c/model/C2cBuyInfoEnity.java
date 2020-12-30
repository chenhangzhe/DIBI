package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;


/**
 * 下单界面购买信息
 */
public class C2cBuyInfoEnity {
    /**
     * code : 0
     * data : {"adId":0,"currencyCode":"string","decimalPlace":0,"legalCurrencyCode":"string","limitMax":0,"limitMin":0,"nickName":"string","payModes":"string","price":0,"remark":"string","surplusQuantity":0}
     * msg : string
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable {
        /**
         * adId : 0
         * currencyCode : string
         * decimalPlace : 0
         * legalCurrencyCode : string
         * limitMax : 0
         * limitMin : 0
         * nickName : string
         * payModes : string
         * price : 0
         * remark : string
         * surplusQuantity : 0
         */

        private int adId;
        private String currencyCode;
        private int decimalPlace;
        private String legalCurrencyCode;
        private double limitMax;
        private double limitMin;
        private String nickName;
        private String payModes;
        private double price;
        private String remark;
        private double surplusQuantity;

        public int getAdId() {
            return adId;
        }

        public void setAdId(int adId) {
            this.adId = adId;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public int getDecimalPlace() {
            return decimalPlace;
        }

        public void setDecimalPlace(int decimalPlace) {
            this.decimalPlace = decimalPlace;
        }

        public String getLegalCurrencyCode() {
            return legalCurrencyCode;
        }

        public void setLegalCurrencyCode(String legalCurrencyCode) {
            this.legalCurrencyCode = legalCurrencyCode;
        }

        public double getLimitMax() {
            return limitMax;
        }

        public void setLimitMax(double limitMax) {
            this.limitMax = limitMax;
        }

        public double getLimitMin() {
            return limitMin;
        }

        public void setLimitMin(double limitMin) {
            this.limitMin = limitMin;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPayModes() {
            return payModes;
        }

        public void setPayModes(String payModes) {
            this.payModes = payModes;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public double getSurplusQuantity() {
            return surplusQuantity;
        }

        public void setSurplusQuantity(double surplusQuantity) {
            this.surplusQuantity = surplusQuantity;
        }
    }
}
