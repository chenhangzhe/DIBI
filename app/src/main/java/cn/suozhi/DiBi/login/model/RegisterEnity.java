package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

/**
 * Created by issuser on 2019/7/10.
 */

public class RegisterEnity {
    /**
     * code : 0
     * data : {"data":{"cellPhone":"string","country":"string","email":"string","emailEnabled":0,"fishingCode":"string","fullName":"string","gaEnabled":0,"idNumber":"string","idType":0,"imToken":"string","phoneArea":"string","phoneEnabled":0,"pic":"string","userCode":"string","userId":0,"userName":"string","userType":0,"verifiedLevel":0,"verifiedStatus":0},"expiry":"2019-07-10T09:13:33.331Z","token":"string"}
     * msg : string
     */

    private int code;
    private DataBeanX data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBeanX implements Serializable {
        /**
         * data : {"cellPhone":"string","country":"string","email":"string","emailEnabled":0,"fishingCode":"string","fullName":"string","gaEnabled":0,"idNumber":"string","idType":0,"imToken":"string","phoneArea":"string","phoneEnabled":0,"pic":"string","userCode":"string","userId":0,"userName":"string","userType":0,"verifiedLevel":0,"verifiedStatus":0}
         * expiry : 2019-07-10T09:13:33.331Z
         * token : string
         */

        private DataBean data;
        private String expiry;
        private String token;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public static class DataBean implements Serializable {
            /**
             * cellPhone : string
             * country : string
             * email : string
             * emailEnabled : 0
             * fishingCode : string
             * fullName : string
             * gaEnabled : 0
             * idNumber : string
             * idType : 0
             * imToken : string
             * phoneArea : string
             * phoneEnabled : 0
             * pic : string
             * userCode : string
             * userId : 0
             * userName : string
             * userType : 0
             * verifiedLevel : 0
             * verifiedStatus : 0
             */

            private String cellPhone;
            private String country;
            private String email;
            private int emailEnabled;
            private String fishingCode;
            private String fullName;
            private int gaEnabled;
            private String idNumber;
            private int idType;
            private String imToken;
            private String phoneArea;
            private int phoneEnabled;
            private String pic;
            private String userCode;
            private int userId;
            private String userName;
            private int userType;
            private int verifiedLevel;
            private int verifiedStatus;

            public String getCellPhone() {
                return cellPhone;
            }

            public void setCellPhone(String cellPhone) {
                this.cellPhone = cellPhone;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public int getEmailEnabled() {
                return emailEnabled;
            }

            public void setEmailEnabled(int emailEnabled) {
                this.emailEnabled = emailEnabled;
            }

            public String getFishingCode() {
                return fishingCode;
            }

            public void setFishingCode(String fishingCode) {
                this.fishingCode = fishingCode;
            }

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }

            public int getGaEnabled() {
                return gaEnabled;
            }

            public void setGaEnabled(int gaEnabled) {
                this.gaEnabled = gaEnabled;
            }

            public String getIdNumber() {
                return idNumber;
            }

            public void setIdNumber(String idNumber) {
                this.idNumber = idNumber;
            }

            public int getIdType() {
                return idType;
            }

            public void setIdType(int idType) {
                this.idType = idType;
            }

            public String getImToken() {
                return imToken;
            }

            public void setImToken(String imToken) {
                this.imToken = imToken;
            }

            public String getPhoneArea() {
                return phoneArea;
            }

            public void setPhoneArea(String phoneArea) {
                this.phoneArea = phoneArea;
            }

            public int getPhoneEnabled() {
                return phoneEnabled;
            }

            public void setPhoneEnabled(int phoneEnabled) {
                this.phoneEnabled = phoneEnabled;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getUserCode() {
                return userCode;
            }

            public void setUserCode(String userCode) {
                this.userCode = userCode;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
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

            public int getVerifiedLevel() {
                return verifiedLevel;
            }

            public void setVerifiedLevel(int verifiedLevel) {
                this.verifiedLevel = verifiedLevel;
            }

            public int getVerifiedStatus() {
                return verifiedStatus;
            }

            public void setVerifiedStatus(int verifiedStatus) {
                this.verifiedStatus = verifiedStatus;
            }
        }
    }
}
