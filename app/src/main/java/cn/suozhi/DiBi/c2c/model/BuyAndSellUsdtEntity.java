package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;
import java.util.List;

public class BuyAndSellUsdtEntity {
    /**
     * code : 0
     * data : {"current":1,"pages":1,"records":[{"adId":36,"avgDealTime":0,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"nickName":"我是商家","payModes":"1","price":1,"totalAmount":100,"totalQuantity":100,"transOrder":3},{"adId":44,"avgDealTime":4,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"nickName":"137****1459","payModes":"2,3,1","price":2.2,"totalAmount":220,"totalQuantity":100,"transOrder":8},{"adId":45,"avgDealTime":4,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":1000,"limitMin":10,"nickName":"137****1459","payModes":"1,2,3","price":2.2,"totalAmount":220,"totalQuantity":100,"transOrder":8},{"adId":38,"avgDealTime":128,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":200,"limitMin":10,"nickName":"188****8889","payModes":"1,2","price":6.95,"totalAmount":695298.85,"totalQuantity":100043,"transOrder":1},{"adId":43,"avgDealTime":6,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"nickName":"ces","payModes":"3","price":6.95,"totalAmount":695,"totalQuantity":100,"transOrder":1},{"adId":37,"avgDealTime":0,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"nickName":"185****1783","payModes":"1,2","price":7.86,"totalAmount":7860,"totalQuantity":1000,"transOrder":4},{"adId":33,"avgDealTime":5,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":500,"limitMin":1,"nickName":"Lvzhao","payModes":"1,3","price":7.89,"totalAmount":3945,"totalQuantity":500,"transOrder":8}],"searchCount":true,"size":10,"total":7}
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
         * records : [{"adId":36,"avgDealTime":0,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"nickName":"我是商家","payModes":"1","price":1,"totalAmount":100,"totalQuantity":100,"transOrder":3},{"adId":44,"avgDealTime":4,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"nickName":"137****1459","payModes":"2,3,1","price":2.2,"totalAmount":220,"totalQuantity":100,"transOrder":8},{"adId":45,"avgDealTime":4,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":1000,"limitMin":10,"nickName":"137****1459","payModes":"1,2,3","price":2.2,"totalAmount":220,"totalQuantity":100,"transOrder":8},{"adId":38,"avgDealTime":128,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":200,"limitMin":10,"nickName":"188****8889","payModes":"1,2","price":6.95,"totalAmount":695298.85,"totalQuantity":100043,"transOrder":1},{"adId":43,"avgDealTime":6,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":10,"nickName":"ces","payModes":"3","price":6.95,"totalAmount":695,"totalQuantity":100,"transOrder":1},{"adId":37,"avgDealTime":0,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":100,"limitMin":1,"nickName":"185****1783","payModes":"1,2","price":7.86,"totalAmount":7860,"totalQuantity":1000,"transOrder":4},{"adId":33,"avgDealTime":5,"currencyCode":"USDT","legalCurrencyCode":"CNY","limitMax":500,"limitMin":1,"nickName":"Lvzhao","payModes":"1,3","price":7.89,"totalAmount":3945,"totalQuantity":500,"transOrder":8}]
         * searchCount : true
         * size : 10
         * total : 7
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
             * adId : 36
             * avgDealTime : 0
             * currencyCode : USDT
             * legalCurrencyCode : CNY
             * limitMax : 100.0
             * limitMin : 1.0
             * nickName : 我是商家
             * payModes : 1
             * price : 1.0
             * totalAmount : 100.0
             * totalQuantity : 100.0
             * transOrder : 3
             */

            private int adId;
            private int avgDealTime;
            private String currencyCode;
            private String legalCurrencyCode;
            private double limitMax;
            private double limitMin;
            private String nickName;
            private String payModes;
            private double price;
            private double totalAmount;
            private double totalQuantity;
            private int transOrder;
            private String remark;
            private double surplusQuantity;

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

            public int getAvgDealTime() {
                return avgDealTime;
            }

            public void setAvgDealTime(int avgDealTime) {
                this.avgDealTime = avgDealTime;
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

            public double getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(double totalAmount) {
                this.totalAmount = totalAmount;
            }

            public double getTotalQuantity() {
                return totalQuantity;
            }

            public void setTotalQuantity(double totalQuantity) {
                this.totalQuantity = totalQuantity;
            }

            public int getTransOrder() {
                return transOrder;
            }

            public void setTransOrder(int transOrder) {
                this.transOrder = transOrder;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
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
}
