package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 基本信息
 */
public class UserEntity {

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
         * info : {}
         * currencyInfo : []
         * totalBtcValuation : 0.00000000
         */

        private InfoEntity info;
        private String c2VerifyFailureReason;
        private String totalBtcValuation;
        private String totalCnyValuation;
        private List<CurrencyEntity> currencyInfo;

        public InfoEntity getInfo() {
            return info;
        }

        public void setInfo(InfoEntity info) {
            this.info = info;
        }

        public String getC2VerifyFailureReason() {
            return c2VerifyFailureReason;
        }

        public void setC2VerifyFailureReason(String c2VerifyFailureReason) {
            this.c2VerifyFailureReason = c2VerifyFailureReason;
        }

        public String getTotalBtcValuation() {
            return totalBtcValuation;
        }

        public void setTotalBtcValuation(String totalBtcValuation) {
            this.totalBtcValuation = totalBtcValuation;
        }

        public List<CurrencyEntity> getCurrencyInfo() {
            return currencyInfo;
        }

        public void setCurrencyInfo(List<CurrencyEntity> currencyInfo) {
            this.currencyInfo = currencyInfo;
        }

        public String getTotalCnyValuation() {
            return totalCnyValuation;
        }

        public void setTotalCnyValuation(String totalCnyValuation) {
            this.totalCnyValuation = totalCnyValuation;
        }

        public static class InfoEntity {
            /**
             * userId : 75
             * userCode : 9997600
             * userName : null
             * userType : 0
             * cellPhone : 18575672219
             * email :
             * idType : 1
             * idNumber : null
             * verifiedStatus : 0
             * verifiedLevel : 0
             * pic : http://47.244.45.163:81null
             * country : null
             * fishingCode : null
             * gaEnabled : 0
             * emailEnabled : 0
             * phoneEnabled : 1
             * fullName : null
             * phoneArea : +86
             * imToken : 6171bf0928cd162f5303763bf44ee88a
             */

            private int userId;
            private String userCode;
            private String userName;
            private int userType;//用户类型1普通用户2商家3操盘4机构
            private String cellPhone;
            private String email;
            private int idType;
            private String idNumber;
            private int verifiedStatus;//0未提交 1已提交 2审核不通过 3审核通过
            private int verifiedLevel;//认证级别 0未认证 1 C1 2 C2 3 C3
            private String pic;
            private String country;
            private String fishingCode;
            private int gaEnabled;
            private int emailEnabled;
            private int phoneEnabled;
            private String fullName;
            private String phoneArea;
            private String imToken;

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserCode() {
                return userCode;
            }

            public void setUserCode(String userCode) {
                this.userCode = userCode;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getUserType() {
                return userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public String getCellPhone() {
                return cellPhone;
            }

            public void setCellPhone(String cellPhone) {
                this.cellPhone = cellPhone;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public int getIdType() {
                return idType;
            }

            public void setIdType(int idType) {
                this.idType = idType;
            }

            public String getIdNumber() {
                return idNumber;
            }

            public void setIdNumber(String idNumber) {
                this.idNumber = idNumber;
            }

            public int getVerifiedStatus() {
                return verifiedStatus;
            }

            public void setVerifiedStatus(int verifiedStatus) {
                this.verifiedStatus = verifiedStatus;
            }

            public int getVerifiedLevel() {
                return verifiedLevel;
            }

            public void setVerifiedLevel(int verifiedLevel) {
                this.verifiedLevel = verifiedLevel;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getFishingCode() {
                return fishingCode;
            }

            public void setFishingCode(String fishingCode) {
                this.fishingCode = fishingCode;
            }

            public int getGaEnabled() {
                return gaEnabled;
            }

            public void setGaEnabled(int gaEnabled) {
                this.gaEnabled = gaEnabled;
            }

            public int getEmailEnabled() {
                return emailEnabled;
            }

            public void setEmailEnabled(int emailEnabled) {
                this.emailEnabled = emailEnabled;
            }

            public int getPhoneEnabled() {
                return phoneEnabled;
            }

            public void setPhoneEnabled(int phoneEnabled) {
                this.phoneEnabled = phoneEnabled;
            }

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }

            public String getPhoneArea() {
                return phoneArea;
            }

            public void setPhoneArea(String phoneArea) {
                this.phoneArea = phoneArea;
            }

            public String getImToken() {
                return imToken;
            }

            public void setImToken(String imToken) {
                this.imToken = imToken;
            }
        }

        public static class CurrencyEntity {
            /**
             * accountId : 303
             * userId : 75
             * currencyId : 1
             * availableAmount : 0.00000000
             * frozenAmount : 0.00000000
             * code : BTC
             * sort : 0
             * isDepositable : 1
             * isWithdrawable : 1
             * icon : /common/2019-07-03/156214901459275143.jpg
             * minWithdrawAmount : null
             * depositLimit : null
             * withdrawalFeeType : null
             * withdrawalFee : null
             * btcValuation : 0.00000000
             * cnyValuation : null
             * forbiddenReason : []
             */

            private int accountId;
            private int userId;
            private int currencyId;
            private String availableAmount;
            private String frozenAmount;
            private String code;
            private int sort;
            private int isDepositable;
            private int isWithdrawable;
            private String icon;
            private String minWithdrawAmount;
            private String depositLimit;
            private String withdrawalFeeType;
            private Object withdrawalFee;
            private String btcValuation;
            private String cnyValuation;
            private List<Object> forbiddenReason;

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

            public String getMinWithdrawAmount() {
                return minWithdrawAmount;
            }

            public void setMinWithdrawAmount(String minWithdrawAmount) {
                this.minWithdrawAmount = minWithdrawAmount;
            }

            public String getDepositLimit() {
                return depositLimit;
            }

            public void setDepositLimit(String depositLimit) {
                this.depositLimit = depositLimit;
            }

            public String getWithdrawalFeeType() {
                return withdrawalFeeType;
            }

            public void setWithdrawalFeeType(String withdrawalFeeType) {
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

            public List<Object> getForbiddenReason() {
                return forbiddenReason;
            }

            public void setForbiddenReason(List<Object> forbiddenReason) {
                this.forbiddenReason = forbiddenReason;
            }
        }
    }
}
