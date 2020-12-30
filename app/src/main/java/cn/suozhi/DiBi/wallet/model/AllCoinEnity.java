package cn.suozhi.DiBi.wallet.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DELL on 2019/7/13.
 */

public class AllCoinEnity {


    /**
     * code : 0
     * msg : 成功
     * data : {"latestCurrency":[{"currencyId":2,"code":"USDT","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317879839712029.png","precis":5,"transactionNeedTag":false,"depositTips":[],"withdrawTips":[]}],"currencyDto":[{"currencyId":5,"code":"AAA","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317658848377531.jpg","precis":8,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":12,"code":"ALC","icon":"http://47.244.45.163:81/images/common/2019-07-09/156265470164622532.jpg","precis":8,"transactionNeedTag":false,"depositTips":[],"withdrawTips":[]},{"currencyId":1,"code":"BTC","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317565049116783.png","precis":8,"transactionNeedTag":false,"depositTips":[],"withdrawTips":[]},{"currencyId":7,"code":"DDD","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317659650135016.jpg","precis":8,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":8,"code":"EOS","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317662107142795.jpg","precis":8,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":9,"code":"ETH","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317904102888801.png","precis":8,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":14,"code":"LED","icon":"http://47.244.45.163:81/images/common/2019-07-09/156265481409027555.jpg","precis":2,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":13,"code":"LEX","icon":"http://47.244.45.163:81/images/common/2019-07-09/156265481409027555.jpg","precis":2,"transactionNeedTag":true,"depositTips":[],"withdrawTips":[]},{"currencyId":3,"code":"RUB","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317652911673983.jpg","precis":8,"transactionNeedTag":false,"depositTips":[],"withdrawTips":[]},{"currencyId":2,"code":"USDT","icon":"http://47.244.45.163:81/images/common/2019-07-15/156317879839712029.png","precis":5,"transactionNeedTag":false,"depositTips":[],"withdrawTips":[]}]}
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
        private List<LatestCurrencyBean> latestCurrency;
        private List<CurrencyDtoBean> currencyDto;

        public List<LatestCurrencyBean> getLatestCurrency() {
            return latestCurrency;
        }

        public void setLatestCurrency(List<LatestCurrencyBean> latestCurrency) {
            this.latestCurrency = latestCurrency;
        }

        public List<CurrencyDtoBean> getCurrencyDto() {
            return currencyDto;
        }

        public void setCurrencyDto(List<CurrencyDtoBean> currencyDto) {
            this.currencyDto = currencyDto;
        }

        public static class LatestCurrencyBean implements Serializable {
            /**
             * currencyId : 2
             * code : USDT
             * icon : http://47.244.45.163:81/images/common/2019-07-15/156317879839712029.png
             * precis : 5
             * transactionNeedTag : false
             * depositTips : []
             * withdrawTips : []
             */

            private int currencyId;
            private String code;
            private String icon;
            private int precis;
            private boolean transactionNeedTag;
            private List<?> depositTips;
            private List<?> withdrawTips;

            public int getCurrencyId() {
                return currencyId;
            }

            public void setCurrencyId(int currencyId) {
                this.currencyId = currencyId;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getPrecis() {
                return precis;
            }

            public void setPrecis(int precis) {
                this.precis = precis;
            }

            public boolean isTransactionNeedTag() {
                return transactionNeedTag;
            }

            public void setTransactionNeedTag(boolean transactionNeedTag) {
                this.transactionNeedTag = transactionNeedTag;
            }

            public List<?> getDepositTips() {
                return depositTips;
            }

            public void setDepositTips(List<?> depositTips) {
                this.depositTips = depositTips;
            }

            public List<?> getWithdrawTips() {
                return withdrawTips;
            }

            public void setWithdrawTips(List<?> withdrawTips) {
                this.withdrawTips = withdrawTips;
            }
        }

        public static class CurrencyDtoBean implements Serializable {
            /**
             * currencyId : 5
             * code : AAA
             * icon : http://47.244.45.163:81/images/common/2019-07-15/156317658848377531.jpg
             * precis : 8
             * transactionNeedTag : true
             * depositTips : []
             * withdrawTips : []
             */

            private int currencyId;
            private String code;
            private String icon;
            private int precis;
            private boolean transactionNeedTag;
            private String mSortLetters;
            private int isDepositable;
            private int isWithdrawable;
            private List<?> depositTips;
            private List<?> withdrawTips;

            public int getCurrencyId() {
                return currencyId;
            }

            public void setCurrencyId(int currencyId) {
                this.currencyId = currencyId;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getPrecis() {
                return precis;
            }

            public void setPrecis(int precis) {
                this.precis = precis;
            }

            public boolean isTransactionNeedTag() {
                return transactionNeedTag;
            }

            public void setTransactionNeedTag(boolean transactionNeedTag) {
                this.transactionNeedTag = transactionNeedTag;
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

            public List<?> getDepositTips() {
                return depositTips;
            }

            public void setDepositTips(List<?> depositTips) {
                this.depositTips = depositTips;
            }

            public List<?> getWithdrawTips() {
                return withdrawTips;
            }

            public void setWithdrawTips(List<?> withdrawTips) {
                this.withdrawTips = withdrawTips;
            }

            public String getmSortLetters() {
                return mSortLetters;
            }

            public void setmSortLetters(String mSortLetters) {
                this.mSortLetters = mSortLetters;
            }
        }
    }
}
