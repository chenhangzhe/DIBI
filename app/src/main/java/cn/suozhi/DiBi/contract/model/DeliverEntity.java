package cn.suozhi.DiBi.contract.model;

import java.util.List;

/**
 * 预测合约交割
 */
public class DeliverEntity {

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
         * currentPage : 1
         * pageSize : 20
         * totalPage : 2
         * totalCount : 40
         * list : []
         */

        private int currentPage;
        private int pageSize;
        private int totalPage;
        private int totalCount;
        private List<ListEntity> list;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<ListEntity> getList() {
            return list;
        }

        public void setList(List<ListEntity> list) {
            this.list = list;
        }

        public static class ListEntity {
            /**
             * createdDate : 1566295613000
             * filledAmount : 8.5
             * orderAmount : 10.0
             * orderStatus : F
             * round : 201908201807
             */

            private long createdDate;
            private double filledAmount;
            private double orderAmount;
            private String orderStatus;
            private long round;

            public long getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(long createdDate) {
                this.createdDate = createdDate;
            }

            public double getFilledAmount() {
                return filledAmount;
            }

            public void setFilledAmount(double filledAmount) {
                this.filledAmount = filledAmount;
            }

            public double getOrderAmount() {
                return orderAmount;
            }

            public void setOrderAmount(double orderAmount) {
                this.orderAmount = orderAmount;
            }

            public String getOrderStatus() {
                return orderStatus;
            }

            public void setOrderStatus(String orderStatus) {
                this.orderStatus = orderStatus;
            }

            public long getRound() {
                return round;
            }

            public void setRound(long round) {
                this.round = round;
            }
        }
    }
}
