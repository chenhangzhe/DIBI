package cn.suozhi.DiBi.contract.model;

import java.util.List;

/**
 * 预测合约资金流水
 */
public class FlowEntity {

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
         * totalPage : 1
         * totalCount : 6
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
             * amount : 12.00000000
             * currency : USDT
             * reasonCode : FB
             * reasonName : 买入锁定
             * status : S
             * updatedDate : 1566372541000
             */

            private String amount;
            private String currency;
            private String reasonCode;
            private String reasonName;
            private String status;
            private long updatedDate;

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getReasonCode() {
                return reasonCode;
            }

            public void setReasonCode(String reasonCode) {
                this.reasonCode = reasonCode;
            }

            public String getReasonName() {
                return reasonName;
            }

            public void setReasonName(String reasonName) {
                this.reasonName = reasonName;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public long getUpdatedDate() {
                return updatedDate;
            }

            public void setUpdatedDate(long updatedDate) {
                this.updatedDate = updatedDate;
            }
        }
    }
}
