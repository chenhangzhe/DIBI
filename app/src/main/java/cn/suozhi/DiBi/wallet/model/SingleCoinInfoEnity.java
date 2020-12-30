package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;

/**
 * 单个币种信息
 */
public class SingleCoinInfoEnity {

    /**
     * code : 0
     * data : {"accountId":642,"availableAmount":"1000000.00000000","code":"BTC","currencyId":1,"depositLimit":"0.20000000","depositTips":[{"content":"简体中文-充币提示12","currencyId":1,"id":4,"language":"zh_CN","type":1}],"forbiddenReason":[],"frozenAmount":"0.00000000","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317565049116783.png","minWithdrawAmount":"0.10000000","transactionNeedTag":false,"userId":104,"withdrawTips":[{"content":"简体中文1-提币提示","currencyId":1,"id":1,"language":"zh_CN","type":2}],"withdrawalFee":0.1,"withdrawalFeeType":"P"}
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
         * accountId : 642
         * availableAmount : 1000000.00000000
         * code : BTC
         * currencyId : 1
         * depositLimit : 0.20000000
         * depositTips : [{"content":"简体中文-充币提示12","currencyId":1,"id":4,"language":"zh_CN","type":1}]
         * forbiddenReason : []
         * frozenAmount : 0.00000000
         * icon : http://47.244.45.163:81/images/common/2019-07-15/156317565049116783.png
         * minWithdrawAmount : 0.10000000
         * transactionNeedTag : false
         * userId : 104
         * withdrawTips : [{"content":"简体中文1-提币提示","currencyId":1,"id":1,"language":"zh_CN","type":2}]
         * withdrawalFee : 0.1
         * withdrawalFeeType : P
         */

        private int accountId;
        private String availableAmount;
        private String code;
        private int currencyId;
        private String depositLimit;
        private String frozenAmount;
        private String icon;
        private String minWithdrawAmount;
        //最小费用
        private double withdrawalMinFee;
        private boolean transactionNeedTag;
        private int userId;
        private double withdrawalFee;
        private String withdrawalFeeType;
        private List<DepositTipsBean> depositTips;
        private List<?> forbiddenReason;
        private List<WithdrawTipsBean> withdrawTips;
        private String tagDescribe;

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public String getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(String availableAmount) {
            this.availableAmount = availableAmount;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(int currencyId) {
            this.currencyId = currencyId;
        }

        public String getDepositLimit() {
            return depositLimit;
        }

        public void setDepositLimit(String depositLimit) {
            this.depositLimit = depositLimit;
        }

        public String getFrozenAmount() {
            return frozenAmount;
        }

        public void setFrozenAmount(String frozenAmount) {
            this.frozenAmount = frozenAmount;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getMinWithdrawAmount() {
            return minWithdrawAmount;
        }

        public void setMinWithdrawAmount(String minWithdrawAmount) {
            this.minWithdrawAmount = minWithdrawAmount;
        }

        public boolean isTransactionNeedTag() {
            return transactionNeedTag;
        }

        public void setTransactionNeedTag(boolean transactionNeedTag) {
            this.transactionNeedTag = transactionNeedTag;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public double getWithdrawalFee() {
            return withdrawalFee;
        }

        public void setWithdrawalFee(double withdrawalFee) {
            this.withdrawalFee = withdrawalFee;
        }

        public String getWithdrawalFeeType() {
            return withdrawalFeeType;
        }

        public void setWithdrawalFeeType(String withdrawalFeeType) {
            this.withdrawalFeeType = withdrawalFeeType;
        }

        public List<DepositTipsBean> getDepositTips() {
            return depositTips;
        }

        public void setDepositTips(List<DepositTipsBean> depositTips) {
            this.depositTips = depositTips;
        }

        public List<?> getForbiddenReason() {
            return forbiddenReason;
        }

        public void setForbiddenReason(List<?> forbiddenReason) {
            this.forbiddenReason = forbiddenReason;
        }

        public List<WithdrawTipsBean> getWithdrawTips() {
            return withdrawTips;
        }

        public void setWithdrawTips(List<WithdrawTipsBean> withdrawTips) {
            this.withdrawTips = withdrawTips;
        }

        public double getWithdrawalMinFee() {
            return withdrawalMinFee;
        }

        public void setWithdrawalMinFee(double withdrawalMinFee) {
            this.withdrawalMinFee = withdrawalMinFee;
        }

        public String getTagDescribe() {
            return tagDescribe;
        }

        public void setTagDescribe(String tagDescribe) {
            this.tagDescribe = tagDescribe;
        }

        public static class DepositTipsBean implements Serializable {
            /**
             * content : 简体中文-充币提示12
             * currencyId : 1
             * id : 4
             * language : zh_CN
             * type : 1
             */

            private String content;
            private int currencyId;
            private int id;
            private String language;
            private int type;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getCurrencyId() {
                return currencyId;
            }

            public void setCurrencyId(int currencyId) {
                this.currencyId = currencyId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }

        public static class WithdrawTipsBean implements Serializable {
            /**
             * content : 简体中文1-提币提示
             * currencyId : 1
             * id : 1
             * language : zh_CN
             * type : 2
             */

            private String content;
            private int currencyId;
            private int id;
            private String language;
            private int type;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getCurrencyId() {
                return currencyId;
            }

            public void setCurrencyId(int currencyId) {
                this.currencyId = currencyId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
