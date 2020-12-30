package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;

/**
 * 订单详情
 */
public class OrderDetailEnity {
    /**
     * code : 0
     * data : {"accid":"string","accountName":"string","accountNumber":"string","another":"string","bank":"string","branchBank":"string","countdown":0,"currencyCode":"string","legalCurrencyCode":"string","orderNo":"string","orderType":0,"payMode":0,"price":0,"qrCode":"string","quantity":0,"status":0,"time":"2019-07-24T07:35:47.554Z","totalPrice":0}
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
         * accid : string
         * accountName : string
         * accountNumber : string
         * another : string
         * bank : string
         * branchBank : string
         * countdown : 0
         * currencyCode : string
         * legalCurrencyCode : string
         * orderNo : string
         * orderType : 0
         * payMode : 0
         * price : 0
         * qrCode : string
         * quantity : 0
         * status : 0
         * time : 2019-07-24T07:35:47.554Z
         * totalPrice : 0
         */

        private String accid;
        private String accountName;
        private String accountNumber;
        private String another;
        private String bank;
        private String branchBank;
        private int countdown;
        private String currencyCode;
        private String legalCurrencyCode;
        private String orderNo;
        //订单类型[1您向XXX购买|2您向XXX出售|3XXX向您购买|4XXX向您出售]
        private int orderType;
        private int payMode;
        private double price;
        private String qrCode;
        private double quantity;
        private int status;
        private String time;
        private double totalPrice;

        public String getAccid() {
            return accid;
        }

        public void setAccid(String accid) {
            this.accid = accid;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getAnother() {
            return another;
        }

        public void setAnother(String another) {
            this.another = another;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBranchBank() {
            return branchBank;
        }

        public void setBranchBank(String branchBank) {
            this.branchBank = branchBank;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
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

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
        }

        public int getPayMode() {
            return payMode;
        }

        public void setPayMode(int payMode) {
            this.payMode = payMode;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}
