package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;
import java.util.List;

public class OrderEnity {
    /**
     * code : 0
     * data : {"current":0,"pages":0,"records":[{"another":"string","appeal":0,"createTime":"2019-07-23T12:16:00.092Z","currencyCode":"string","legalCurrencyCode":"string","orderNo":"string","orderType":0,"payMode":0,"price":0,"quantity":0,"status":0,"totalPrice":0}],"searchCount":true,"size":0,"total":0}
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
         * current : 0
         * pages : 0
         * records : [{"another":"string","appeal":0,"createTime":"2019-07-23T12:16:00.092Z","currencyCode":"string","legalCurrencyCode":"string","orderNo":"string","orderType":0,"payMode":0,"price":0,"quantity":0,"status":0,"totalPrice":0}]
         * searchCount : true
         * size : 0
         * total : 0
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<RecordsBean> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean implements Serializable {
            /**
             * another : string
             * appeal : 0
             * createTime : 2019-07-23T12:16:00.092Z
             * currencyCode : string
             * legalCurrencyCode : string
             * orderNo : string
             * orderType : 0
             * payMode : 0
             * price : 0
             * quantity : 0
             * status : 0
             * totalPrice : 0
             */

            private String another;
            private int appeal;
            private String createTime;
            private String currencyCode;
            private String legalCurrencyCode;
            private String orderNo;
            private int orderType;
            private int payMode;
            private double price;
            private double quantity;
            //状态[1待支付|2待确认|3已取消|4申诉中|5已完成]
            private int status;
            private double totalPrice;

            private int  loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public String getAnother() {
                return another;
            }

            public void setAnother(String another) {
                this.another = another;
            }

            public int getAppeal() {
                return appeal;
            }

            public void setAppeal(int appeal) {
                this.appeal = appeal;
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

            public double getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }
        }
    }
}
