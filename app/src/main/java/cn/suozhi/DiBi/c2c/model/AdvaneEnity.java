package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;
import java.util.List;


/**
 * 我的广告
 */

public class AdvaneEnity {

    /**
     * code : 0
     * data : {"current":1,"pages":1,"records":[{"adId":47,"adNo":"20190724151604320310","createTime":"2019-07-24 15:16:04","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,3,1","price":1.1,"remark":"","status":1,"totalQuantity":100,"type":1},{"adId":45,"adNo":"20190723103042498428","createTime":"2019-07-23 10:30:42","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":1000,"limitMin":10,"payModes":"1,2,3","price":2.2,"remark":"","status":1,"totalQuantity":100,"type":2},{"adId":44,"adNo":"20190723103026730922","createTime":"2019-07-23 10:30:26","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,3,1","price":2.2,"remark":"","status":1,"totalQuantity":100,"type":2},{"adId":35,"adNo":"20190717103356159108","createTime":"2019-07-17 10:33:56","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,1,3","price":1,"remark":"","status":3,"totalQuantity":100,"type":2},{"adId":34,"adNo":"20190717103041131098","createTime":"2019-07-17 10:30:42","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"payModes":"1,2,3","price":9.9,"remark":"","status":3,"totalQuantity":100,"type":1}],"searchCount":true,"size":10,"total":5}
     * msg : 成功
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
         * current : 1
         * pages : 1
         * records : [{"adId":47,"adNo":"20190724151604320310","createTime":"2019-07-24 15:16:04","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,3,1","price":1.1,"remark":"","status":1,"totalQuantity":100,"type":1},{"adId":45,"adNo":"20190723103042498428","createTime":"2019-07-23 10:30:42","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":1000,"limitMin":10,"payModes":"1,2,3","price":2.2,"remark":"","status":1,"totalQuantity":100,"type":2},{"adId":44,"adNo":"20190723103026730922","createTime":"2019-07-23 10:30:26","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,3,1","price":2.2,"remark":"","status":1,"totalQuantity":100,"type":2},{"adId":35,"adNo":"20190717103356159108","createTime":"2019-07-17 10:33:56","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"payModes":"2,1,3","price":1,"remark":"","status":3,"totalQuantity":100,"type":2},{"adId":34,"adNo":"20190717103041131098","createTime":"2019-07-17 10:30:42","currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"payModes":"1,2,3","price":9.9,"remark":"","status":3,"totalQuantity":100,"type":1}]
         * searchCount : true
         * size : 10
         * total : 5
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
             * adId : 47
             * adNo : 20190724151604320310
             * createTime : 2019-07-24 15:16:04
             * currencyCode : USDT
             * legalCurrencyCode : CNY
             * limitMax : 100.0
             * limitMin : 10.0
             * payModes : 2,3,1
             * price : 1.1
             * remark :
             * status : 1
             * totalQuantity : 100.0
             * type : 1
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
            private int status; //状态[1上架|2下架|3已完成]
            private double totalQuantity;
            private int type; //广告类型[1买|2卖]
            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

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

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }
        }
    }
}
