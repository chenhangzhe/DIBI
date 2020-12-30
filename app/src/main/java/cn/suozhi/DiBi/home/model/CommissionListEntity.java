package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 返佣记录
 */
public class CommissionListEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {}
     */

    private long code;
    private String msg;
    private DataEntity data;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * records : []
         * total : 3
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
        private List<RecordsEntity> records;

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

        public List<RecordsEntity> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsEntity> records) {
            this.records = records;
        }

        public static class RecordsEntity {
            /**
             * userId : 101000
             * currency : USDT
             * rakeBackAmount : 0.3
             * settleTime : 2019-08-22 17:36:58
             * type : future
             */

            private long userId;
            private String currency;
            private double rakeBackAmount;
            private String settleTime;
            private String type;

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
                this.userId = userId;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public double getRakeBackAmount() {
                return rakeBackAmount;
            }

            public void setRakeBackAmount(double rakeBackAmount) {
                this.rakeBackAmount = rakeBackAmount;
            }

            public String getSettleTime() {
                return settleTime;
            }

            public void setSettleTime(String settleTime) {
                this.settleTime = settleTime;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
