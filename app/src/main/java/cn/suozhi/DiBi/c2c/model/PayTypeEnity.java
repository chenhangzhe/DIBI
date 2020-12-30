package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;
import java.util.List;

public class PayTypeEnity {
    /**
     * code : 0
     * data : [{"accountName":"string","accountNumber":"string","accountType":0,"bank":"string","branchBank":"string","qrCode":"string","status":0}]
     * msg : string
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * accountName : string
         * accountNumber : string
         * accountType : 0
         * bank : string
         * branchBank : string
         * qrCode : string
         * status : 0
         */

        private String accountName;
        private String accountNumber;
        private int accountType;
        private String bank;
        private String branchBank;
        private String qrCode;
        //是否禁用 1 可用 0 禁用
        private int status;
        private boolean isSelected;
        //是否可用
        private boolean isUseable;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isUseable() {
            return isUseable;
        }

        public void setUseable(boolean useable) {
            isUseable = useable;
        }
    }
}
