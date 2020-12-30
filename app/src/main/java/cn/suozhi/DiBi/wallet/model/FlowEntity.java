package cn.suozhi.DiBi.wallet.model;

import java.util.List;

public class FlowEntity {
    /**
     * code : 0
     * msg : 成功
     * data : {"records":[{"createdDate":"2020-03-24 22:17:58","code":"DIC","amount":9.88767787E8,"reasonCode":"BAD","fundType":"IA"},{"createdDate":"2020-03-24 22:18:01","code":"USDT","amount":1.0E7,"reasonCode":"BAD","fundType":"IA"},{"createdDate":"2020-03-25 12:05:57","code":"USDT","amount":33,"reasonCode":"TF","fundType":"F"},{"createdDate":"2020-03-25 12:05:57","code":"USDT","amount":33,"reasonCode":"TS","fundType":"DF"},{"createdDate":"2020-03-26 10:19:33","code":"USDT","amount":45,"reasonCode":"TF","fundType":"F"},{"createdDate":"2020-03-26 10:19:33","code":"USDT","amount":45,"reasonCode":"TS","fundType":"DF"},{"createdDate":"2020-03-29 15:18:14","code":"USDT","amount":100,"reasonCode":"WF","fundType":"F"},{"createdDate":"2020-03-30 10:31:53","code":"DIC","amount":1,"reasonCode":"AT","fundType":"IA"},{"createdDate":"2020-03-30 10:35:34","code":"DIC","amount":1.2,"reasonCode":"AT","fundType":"IA"},{"createdDate":"2020-03-30 10:38:45","code":"DIC","amount":1.4,"reasonCode":"AT","fundType":"IA"}],"total":33,"size":10,"current":1,"searchCount":true,"pages":4}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * records : [{"createdDate":"2020-03-24 22:17:58","code":"DIC","amount":9.88767787E8,"reasonCode":"BAD","fundType":"IA"},{"createdDate":"2020-03-24 22:18:01","code":"USDT","amount":1.0E7,"reasonCode":"BAD","fundType":"IA"},{"createdDate":"2020-03-25 12:05:57","code":"USDT","amount":33,"reasonCode":"TF","fundType":"F"},{"createdDate":"2020-03-25 12:05:57","code":"USDT","amount":33,"reasonCode":"TS","fundType":"DF"},{"createdDate":"2020-03-26 10:19:33","code":"USDT","amount":45,"reasonCode":"TF","fundType":"F"},{"createdDate":"2020-03-26 10:19:33","code":"USDT","amount":45,"reasonCode":"TS","fundType":"DF"},{"createdDate":"2020-03-29 15:18:14","code":"USDT","amount":100,"reasonCode":"WF","fundType":"F"},{"createdDate":"2020-03-30 10:31:53","code":"DIC","amount":1,"reasonCode":"AT","fundType":"IA"},{"createdDate":"2020-03-30 10:35:34","code":"DIC","amount":1.2,"reasonCode":"AT","fundType":"IA"},{"createdDate":"2020-03-30 10:38:45","code":"DIC","amount":1.4,"reasonCode":"AT","fundType":"IA"}]
         * total : 33
         * size : 10
         * current : 1
         * searchCount : true
         * pages : 4
         */

        private int total;
        private int size;
        private int current;
        private boolean searchCount;
        private int pages;
        private List<RecordsBean> records;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * createdDate : 2020-03-24 22:17:58
             * code : DIC
             * amount : 9.88767787E8
             * reasonCode : BAD
             * fundType : IA
             * showCode : USDT-E
             * isSpecialCurrency : 0
             */

            private String createdDate;
            private String code;
            private double amount;
            private String reasonCode;
            private String fundType;
            private String showCode;
            private int isSpecialCurrency;

            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public String getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getReasonCode() {
                return reasonCode;
            }

            public void setReasonCode(String reasonCode) {
                this.reasonCode = reasonCode;
            }

            public String getFundType() {
                return fundType;
            }

            public void setFundType(String fundType) {
                this.fundType = fundType;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public String getShowCode() {
                return showCode;
            }

            public void setShowCode(String showCode) {
                this.showCode = showCode;
            }

            public int getIsSpecialCurrency() {
                return isSpecialCurrency;
            }

            public void setIsSpecialCurrency(int isSpecialCurrency) {
                this.isSpecialCurrency = isSpecialCurrency;
            }
        }
    }

}