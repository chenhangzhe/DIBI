package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by issuser on 2019/7/11.
 */

public class CoinEnity {


    /**
     * code : 0
     * msg : 成功
     * data : [{"accountId":376,"userId":82,"currencyId":1,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"BTC","sort":0,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81/common/2019-07-03/156214901459275143.jpg","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":378,"userId":82,"currencyId":3,"availableAmount":"0","frozenAmount":"0","code":"RUB","sort":0,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":377,"userId":82,"currencyId":2,"availableAmount":"0.00000","frozenAmount":"0.00000","code":"USDT","sort":0,"isDepositable":0,"isWithdrawable":0,"icon":"http://47.244.45.163:81","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":379,"userId":82,"currencyId":5,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"AAA","sort":1,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81/common/2019-07-01/156195297729046131.png","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":380,"userId":82,"currencyId":7,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"DDD","sort":1,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81/common/2019-07-01/156196703303611852.png","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":381,"userId":82,"currencyId":8,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"EOS","sort":1,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81/common/2019-07-01/156197111911079634.png","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":382,"userId":82,"currencyId":9,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"ETH","sort":4,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81/common/2019-07-01/156197119321622735.png","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":383,"userId":82,"currencyId":12,"availableAmount":"0.00000000","frozenAmount":"0.00000000","code":"ALC","sort":1000,"isDepositable":1,"isWithdrawable":0,"icon":"http://47.244.45.163:81http://47.244.45.163:81/images/common/2019-07-09/156265470164622532.jpg","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":385,"userId":82,"currencyId":14,"availableAmount":"0.00","frozenAmount":"0.00","code":"LED","sort":1000,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81http://47.244.45.163:81/images/common/2019-07-09/156265481409027555.jpg","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]},{"accountId":384,"userId":82,"currencyId":13,"availableAmount":"0.00","frozenAmount":"0.00","code":"LEX","sort":1000,"isDepositable":1,"isWithdrawable":1,"icon":"http://47.244.45.163:81http://47.244.45.163:81/images/common/2019-07-09/156265481409027555.jpg","minWithdrawAmount":null,"depositLimit":null,"withdrawalFeeType":null,"withdrawalFee":null,"btcValuation":"0.00000000","cnyValuation":"0.00","forbiddenReason":[]}]
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
         * accountId : 376
         * userId : 82
         * currencyId : 1
         * availableAmount : 0.00000000
         * frozenAmount : 0.00000000
         * code : BTC
         * sort : 0
         * isDepositable : 1
         * isWithdrawable : 1
         * icon : http://47.244.45.163:81/common/2019-07-03/156214901459275143.jpg
         * minWithdrawAmount : null
         * depositLimit : null
         * withdrawalFeeType : null
         * withdrawalFee : null
         * btcValuation : 0.00000000
         * cnyValuation : 0.00
         * forbiddenReason : []
         * isSpecialCurrency : 0
         * showCode : BTC
         */

        private int accountId;
        private int userId;
        private int currencyId;
        private String availableAmount;
        private String frozenAmount;
        //币种
        private String code;
        private int sort;
        //是否可充 1 是 0 否
        private int isDepositable;
//        //是否可提 1 是 0 否
        private int isWithdrawable;
        //图标
        private String icon;
        private Object minWithdrawAmount;
        private Object depositLimit;
        private Object withdrawalFeeType;
        private Object withdrawalFee;
        private String btcValuation;
        private String cnyValuation;
        private List<ForbiddenDataBean> forbiddenReason;
        private int isSpecialCurrency;
        private String showCode;

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(int currencyId) {
            this.currencyId = currencyId;
        }

        public String getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(String availableAmount) {
            this.availableAmount = availableAmount;
        }

        public String getFrozenAmount() {
            return frozenAmount;
        }

        public void setFrozenAmount(String frozenAmount) {
            this.frozenAmount = frozenAmount;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getIsDepositable() {
            return isDepositable;
        }

        public void setIsDepositable(int isDepositable) {
            this.isDepositable = isDepositable;
        }

        public int getIsWithdrawable() {
            return isWithdrawable;
        }

        public void setIsWithdrawable(int isWithdrawable) {
            this.isWithdrawable = isWithdrawable;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Object getMinWithdrawAmount() {
            return minWithdrawAmount;
        }

        public void setMinWithdrawAmount(Object minWithdrawAmount) {
            this.minWithdrawAmount = minWithdrawAmount;
        }

        public Object getDepositLimit() {
            return depositLimit;
        }

        public void setDepositLimit(Object depositLimit) {
            this.depositLimit = depositLimit;
        }

        public Object getWithdrawalFeeType() {
            return withdrawalFeeType;
        }

        public void setWithdrawalFeeType(Object withdrawalFeeType) {
            this.withdrawalFeeType = withdrawalFeeType;
        }

        public Object getWithdrawalFee() {
            return withdrawalFee;
        }

        public void setWithdrawalFee(Object withdrawalFee) {
            this.withdrawalFee = withdrawalFee;
        }

        public String getBtcValuation() {
            return btcValuation;
        }

        public void setBtcValuation(String btcValuation) {
            this.btcValuation = btcValuation;
        }

        public String getCnyValuation() {
            return cnyValuation;
        }

        public void setCnyValuation(String cnyValuation) {
            this.cnyValuation = cnyValuation;
        }

        public List<ForbiddenDataBean> getForbiddenReason() {
            return forbiddenReason;
        }

        public void setForbiddenReason(List<ForbiddenDataBean> forbiddenReason) {
            this.forbiddenReason = forbiddenReason;
        }

        public int getIsSpecialCurrency() {
            return isSpecialCurrency;
        }

        public void setIsSpecialCurrency(int isSpecialCurrency) {
            this.isSpecialCurrency = isSpecialCurrency;
        }

        public String getShowCode() {
            return showCode;
        }

        public void setShowCode(String showCode) {
            this.showCode = showCode;
        }


        public static class ForbiddenDataBean{

            /**
             * content : 不支持
             * currencyId : 10
             * id : 21
             * language : zh_CN
             * type : 3
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
