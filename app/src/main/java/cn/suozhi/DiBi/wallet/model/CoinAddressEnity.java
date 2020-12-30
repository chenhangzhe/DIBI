package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;


/**
 * 币种地址列表
 */
public class CoinAddressEnity {
    /**
     * code : 0
     * msg : 成功
     * data : {"records":[{"id":23,"code":"USDT","tag":"添加usdt","address":"nxnxnjxjxjxkxkxk","remark":""},{"id":22,"code":"EOS","tag":"jxjxjjxjx","address":"nxnxnnxjxjxjjxjxjxjjxjxjxjx","remark":"memo"},{"id":21,"code":"BTC","tag":"女魔头","address":"jxjxjcjjxjxjcjcjjcjc","remark":""}],"total":3,"size":10,"current":1,"searchCount":true,"pages":1}
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

    public static class DataBean implements Serializable {
        /**
         * records : [{"id":23,"code":"USDT","tag":"添加usdt","address":"nxnxnjxjxjxkxkxk","remark":""},{"id":22,"code":"EOS","tag":"jxjxjjxjx","address":"nxnxnnxjxjxjjxjxjxjjxjxjxjx","remark":"memo"},{"id":21,"code":"BTC","tag":"女魔头","address":"jxjxjcjjxjxjcjcjjcjc","remark":""}]
         * total : 3
         * size : 10
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

        public static class RecordsBean implements Serializable {
            /**
             * id : 23
             * code : USDT
             * tag : 添加usdt
             * address : nxnxnjxjxjxkxkxk
             * remark :
             */

            private int id;
            private String code;
            private String tag;
            private String address;
            private String remark;
            private String tagDescribe;
            private boolean transactionNeedTag;

            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public String getTagDescribe() {
                return tagDescribe;
            }

            public void setTagDescribe(String tagDescribe) {
                this.tagDescribe = tagDescribe;
            }

            public boolean isTransactionNeedTag() {
                return transactionNeedTag;
            }

            public void setTransactionNeedTag(boolean transactionNeedTag) {
                this.transactionNeedTag = transactionNeedTag;
            }
        }
    }
}
