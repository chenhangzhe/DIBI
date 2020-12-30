package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;

public class AdvanceDetailEnity {
    /**
     * code : 0
     * data : {"adId":0,"adNo":"string","createTime":"2019-07-25T12:07:38.490Z","currencyCode":"string","legalCurrencyCode":"string","limitMax":0,"limitMin":0,"payModes":"string","price":0,"remark":"string","status":0,"totalQuantity":0,"type":0}
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
         * adNo : string
         * createTime : 2019-07-25T12:07:38.490Z
         * currencyCode : string
         * legalCurrencyCode : string
         * limitMax : 0
         * limitMin : 0
         * payModes : string
         * price : 0
         * remark : string
         * status : 0
         * totalQuantity : 0
         * type : 0
         */

        private int adId;
        private String adNo;
        private String createTime;
        private String currencyCode;
        private String legalCurrencyCode;
        private double limitMax;
        private double limitMin;
        private String payModes;
        private double price;
        private String remark;
        private int status;
        private double totalQuantity;
        private int type;

        public int getAdId() {
            return adId;
        }

        public void setAdId(int adId) {
            this.adId = adId;
        }

        public String getAdNo() {
            return adNo;
        }

        public void setAdNo(String adNo) {
            this.adNo = adNo;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(double totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
