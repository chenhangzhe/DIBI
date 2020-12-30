package cn.suozhi.DiBi.wallet.model;

import java.util.Collection;
import java.util.List;

public class TransferRecordEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {"records":[{"currencyCode":"USDT","transferType":"W","amount":"33.00000000","transferUid":"2014195","phoneOrEmail":"","createdDate":"2020-03-25 12:05:57"},{"currencyCode":"USDT","transferType":"W","amount":"45.00000000","transferUid":"2014195","phoneOrEmail":"","createdDate":"2020-03-26 10:19:33"}],"total":2,"size":15,"current":1,"searchCount":true,"pages":1}
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
         * records : [{"currencyCode":"USDT","transferType":"W","amount":"33.00000000","transferUid":"2014195","phoneOrEmail":"","createdDate":"2020-03-25 12:05:57"},{"currencyCode":"USDT","transferType":"W","amount":"45.00000000","transferUid":"2014195","phoneOrEmail":"","createdDate":"2020-03-26 10:19:33"}]
         * total : 2
         * size : 15
         * current : 1
         * searchCount : true
         * pages : 1
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
             * currencyCode : USDT
             * transferType : W
             * amount : 33.00000000
             * transferUid : 2014195
             * phoneOrEmail :
             * createdDate : 2020-03-25 12:05:57
             */

            private String currencyCode;
            private String transferType;
            private String amount;
            private String transferUid;
            private String phoneOrEmail;
            private String createdDate;

            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public String getCurrencyCode() {
                return currencyCode;
            }

            public void setCurrencyCode(String currencyCode) {
                this.currencyCode = currencyCode;
            }

            public String getTransferType() {
                return transferType;
            }

            public void setTransferType(String transferType) {
                this.transferType = transferType;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getTransferUid() {
                return transferUid;
            }

            public void setTransferUid(String transferUid) {
                this.transferUid = transferUid;
            }

            public String getPhoneOrEmail() {
                return phoneOrEmail;
            }

            public void setPhoneOrEmail(String phoneOrEmail) {
                this.phoneOrEmail = phoneOrEmail;
            }

            public String getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
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
