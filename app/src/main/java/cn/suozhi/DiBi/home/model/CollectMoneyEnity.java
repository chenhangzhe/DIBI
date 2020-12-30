package cn.suozhi.DiBi.home.model;

import java.io.Serializable;
import java.util.List;

/**
 * 收款界面的实体类
 */
public class CollectMoneyEnity {
    /**
     * code : 0
     * data : {"current":0,"pages":0,"records":[{"accountName":"string","accountNumber":"string","accountType":0,"bank":"string","branchBank":"string","qrCode":"string"}],"searchCount":true,"size":0,"total":0}
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
         * records : [{"accountName":"string","accountNumber":"string","accountType":0,"bank":"string","branchBank":"string","qrCode":"string"}]
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
             * accountName : string
             * accountNumber : string
             * accountType : 0
             * bank : string
             * branchBank : string
             * qrCode : string
             */
            //账户名
            private String accountName;
            //收款账号
            private String accountNumber;
            //账号类型 1支付宝 2微信 3银行卡
            private int accountType;
            //开户银行
            private String bank;
            //开户支行
            private String branchBank;
            //二维码
            private String qrCode;

            public String getAccountName() {
                return accountName;
            }

            public void setAccountName(String accountName) {
                this.accountName = accountName;
            }

            public String getAccountNumber() {
                return accountNumber;
            }

            public void setAccountNumber(String accountNumber) {
                this.accountNumber = accountNumber;
            }

            public int getAccountType() {
                return accountType;
            }

            public void setAccountType(int accountType) {
                this.accountType = accountType;
            }

            public String getBank() {
                return bank;
            }

            public void setBank(String bank) {
                this.bank = bank;
            }

            public String getBranchBank() {
                return branchBank;
            }

            public void setBranchBank(String branchBank) {
                this.branchBank = branchBank;
            }

            public String getQrCode() {
                return qrCode;
            }

            public void setQrCode(String qrCode) {
                this.qrCode = qrCode;
            }
        }
    }
}
