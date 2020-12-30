package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DELL on 2019/7/13.
 */

public class AccountRecordEnity {
    /**
     * code : 0
     * data : {"current":1,"pages":1,"records":[{"amount":70,"approvalStatus":"W","blockchainAddress":"sghsjsnsbbahjaha.?2626","blockchainExplorer":"https://omniexplorer.info","code":"USDT","createdDate":"2019-08-06 18:17:06","executionStatus":"N","fee":10},{"amount":90.9,"approvalStatus":"R","blockchainAddress":"sghsjsnsbbahjaha.?2626","blockchainExplorer":"https://omniexplorer.info","code":"USDT","createdDate":"2019-08-06 15:05:05","executionStatus":"F","fee":10.1},{"amount":59.996,"approvalStatus":"R","blockchainAddress":"432fsfdfd ","blockchainExplorer":"https://www.blockchain.com/btc","code":"BTC","createdDate":"2019-08-03 15:17:34","executionStatus":"F","fee":0.002}],"searchCount":true,"size":15,"total":3}
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
         * records : [{"amount":70,"approvalStatus":"W","blockchainAddress":"sghsjsnsbbahjaha.?2626","blockchainExplorer":"https://omniexplorer.info","code":"USDT","createdDate":"2019-08-06 18:17:06","executionStatus":"N","fee":10},{"amount":90.9,"approvalStatus":"R","blockchainAddress":"sghsjsnsbbahjaha.?2626","blockchainExplorer":"https://omniexplorer.info","code":"USDT","createdDate":"2019-08-06 15:05:05","executionStatus":"F","fee":10.1},{"amount":59.996,"approvalStatus":"R","blockchainAddress":"432fsfdfd ","blockchainExplorer":"https://www.blockchain.com/btc","code":"BTC","createdDate":"2019-08-03 15:17:34","executionStatus":"F","fee":0.002}]
         * searchCount : true
         * size : 15
         * total : 3
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
             * amount : 70.0
             * approvalStatus : W
             * blockchainAddress : sghsjsnsbbahjaha.?2626
             * blockchainExplorer : https://omniexplorer.info
             * code : USDT
             * createdDate : 2019-08-06 18:17:06
             * executionStatus : N
             * fee : 10.0
             */

            private double amount;
            private String approvalStatus;
            private String blockchainAddress;
            //区块浏览器前缀,需要拼接txId跳转到区块浏览器(拼接中间最好加个/,防止后台运营人员没填/)
            private String blockchainExplorer;
            private String code;
            private String createdDate;
            private String executionStatus;
            //txid
            private String blockchainTxId;
            private double fee;

            private boolean isExpand;
            private int  loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getApprovalStatus() {
                return approvalStatus;
            }

            public void setApprovalStatus(String approvalStatus) {
                this.approvalStatus = approvalStatus;
            }

            public String getBlockchainAddress() {
                return blockchainAddress;
            }

            public void setBlockchainAddress(String blockchainAddress) {
                this.blockchainAddress = blockchainAddress;
            }

            public String getBlockchainExplorer() {
                return blockchainExplorer;
            }

            public void setBlockchainExplorer(String blockchainExplorer) {
                this.blockchainExplorer = blockchainExplorer;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public String getExecutionStatus() {
                return executionStatus;
            }

            public void setExecutionStatus(String executionStatus) {
                this.executionStatus = executionStatus;
            }

            public double getFee() {
                return fee;
            }

            public void setFee(double fee) {
                this.fee = fee;
            }

            public boolean isExpand() {
                return isExpand;
            }

            public void setExpand(boolean expand) {
                isExpand = expand;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public String getBlockchainTxId() {
                return blockchainTxId;
            }

            public void setBlockchainTxId(String blockchainTxId) {
                this.blockchainTxId = blockchainTxId;
            }
        }
    }

//
//    /**
//     * code : 0
//     * data : {"current":0,"pages":0,"records":[{"amount":0,"approvalStatus":"string","blockchainAddress":"string","blockchainExplorer":"string","blockchainTxId":"string","code":"string","createdDate":"2019-07-25T12:42:41.227Z","executionStatus":"string","fee":0}],"searchCount":true,"size":0,"total":0}
//     * msg : string
//     */
//
//    private int code;
//    private DataBean data;
//    private String msg;
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public static class DataBean implements Serializable {
//        /**
//         * current : 0
//         * pages : 0
//         * records : [{"amount":0,"approvalStatus":"string","blockchainAddress":"string","blockchainExplorer":"string","blockchainTxId":"string","code":"string","createdDate":"2019-07-25T12:42:41.227Z","executionStatus":"string","fee":0}]
//         * searchCount : true
//         * size : 0
//         * total : 0
//         */
//
//        private int current;
//        private int pages;
//        private boolean searchCount;
//        private int size;
//        private int total;
//        private List<RecordsBean> records;
//
//
//        public int getCurrent() {
//            return current;
//        }
//
//        public void setCurrent(int current) {
//            this.current = current;
//        }
//
//        public int getPages() {
//            return pages;
//        }
//
//        public void setPages(int pages) {
//            this.pages = pages;
//        }
//
//        public boolean isSearchCount() {
//            return searchCount;
//        }
//
//        public void setSearchCount(boolean searchCount) {
//            this.searchCount = searchCount;
//        }
//
//        public int getSize() {
//            return size;
//        }
//
//        public void setSize(int size) {
//            this.size = size;
//        }
//
//        public int getTotal() {
//            return total;
//        }
//
//        public void setTotal(int total) {
//            this.total = total;
//        }
//
//        public List<RecordsBean> getRecords() {
//            return records;
//        }
//
//        public void setRecords(List<RecordsBean> records) {
//            this.records = records;
//        }
//
//        public static class RecordsBean implements Serializable {
//            /**
//             * amount : 0
//             * approvalStatus : string
//             * blockchainAddress : string
//             * blockchainExplorer : string
//             * blockchainTxId : string
//             * code : string
//             * createdDate : 2019-07-25T12:42:41.227Z
//             * executionStatus : string
//             * fee : 0
//             */
//
//            private int amount;
//            //审核状态 W等待审批 C取消 A审批通过 R审批不通过 P入金待确认 充币状态用该字段标识,P 待审核 A 成功 R 审核不通过
//            private String approvalStatus;
//            private String blockchainAddress;
//            private String blockchainExplorer;
//            private String blockchainTxId;
//            private String code;
//            private String createdDate;
//            //执行状态 N未开始 W等待执行 P正在执行 S执行成功 F执行失败 提币状态用该字段标识,N 待审核 W P 区块确认中 S 区块确认成功 F 区块确认失败
//            private String executionStatus;
//            private int fee;
//            private boolean isExpand;
//            private int  loadType;
//
//            public RecordsBean(int loadType) {
//                this.loadType = loadType;
//            }
//
//            public int getAmount() {
//                return amount;
//            }
//
//            public void setAmount(int amount) {
//                this.amount = amount;
//            }
//
//            public String getApprovalStatus() {
//                return approvalStatus;
//            }
//
//            public void setApprovalStatus(String approvalStatus) {
//                this.approvalStatus = approvalStatus;
//            }
//
//            public String getBlockchainAddress() {
//                return blockchainAddress;
//            }
//
//            public void setBlockchainAddress(String blockchainAddress) {
//                this.blockchainAddress = blockchainAddress;
//            }
//
//            public String getBlockchainExplorer() {
//                return blockchainExplorer;
//            }
//
//            public void setBlockchainExplorer(String blockchainExplorer) {
//                this.blockchainExplorer = blockchainExplorer;
//            }
//
//            public String getBlockchainTxId() {
//                return blockchainTxId;
//            }
//
//            public void setBlockchainTxId(String blockchainTxId) {
//                this.blockchainTxId = blockchainTxId;
//            }
//
//            public String getCode() {
//                return code;
//            }
//
//            public void setCode(String code) {
//                this.code = code;
//            }
//
//            public String getCreatedDate() {
//                return createdDate;
//            }
//
//            public void setCreatedDate(String createdDate) {
//                this.createdDate = createdDate;
//            }
//
//            public String getExecutionStatus() {
//                return executionStatus;
//            }
//
//            public void setExecutionStatus(String executionStatus) {
//                this.executionStatus = executionStatus;
//            }
//
//            public int getFee() {
//                return fee;
//            }
//
//            public void setFee(int fee) {
//                this.fee = fee;
//            }
//
//            public boolean isExpand() {
//                return isExpand;
//            }
//
//            public void setExpand(boolean expand) {
//                isExpand = expand;
//            }
//
//            public int getLoadType() {
//                return loadType;
//            }
//
//            public void setLoadType(int loadType) {
//                this.loadType = loadType;
//            }
//        }
//    }
}
