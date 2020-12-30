package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

/**
 * Created by DELL on 2019/7/10.
 */

public class LoginEnity {
    /**
     * code : 0
     * msg : 成功
     * data : {"token":"eyJ1c2VyTmFtZSI6IjEyMyIsImFsZyI6IkhTMjU2In0.eyJqdGkiOiJmY29pbmp3dCIsImlhdCI6MTU2Mjc2NTc4NSwic3ViIjoie1widXNlcklkXCI6ODIsXCJ1c2VyQ29kZVwiOlwiNjY2NjU1NlwiLFwidXNlclR5cGVcIjowLFwiY2VsbFBob25lXCI6XCIxODU1MTcxODQ4OVwiLFwiZW1haWxcIjpcIlwiLFwiaWRUeXBlXCI6MSxcInZlcmlmaWVkU3RhdHVzXCI6MCxcInZlcmlmaWVkTGV2ZWxcIjowLFwicGljXCI6XCJodHRwOi8vNDcuMjQ0LjQ1LjE2Mzo4MW51bGxcIixcImdhRW5hYmxlZFwiOjAsXCJlbWFpbEVuYWJsZWRcIjowLFwicGhvbmVFbmFibGVkXCI6MSxcInBob25lQXJlYVwiOlwiKzg2XCIsXCJpbVRva2VuXCI6XCI0YmNmNGNiOTRlZmRiODk2MTBiZDY1ZTYxNmE5YjE3MFwifSIsImV4cCI6MTU2NTM1Nzc4NX0.8mMAYdsiuRGDwZIApoGpMRk43kWf6_SmATQOaUEL4Ps","expiry":"2019-08-09 21:36:25","data":{"userId":82,"userCode":"6666556","userName":null,"userType":0,"cellPhone":"18551718489","email":"","idType":1,"idNumber":null,"verifiedStatus":0,"verifiedLevel":0,"pic":"http://47.244.45.163:81null","country":null,"fishingCode":null,"gaEnabled":0,"emailEnabled":0,"phoneEnabled":1,"fullName":null,"phoneArea":"+86","imToken":"4bcf4cb94efdb89610bd65e616a9b170"}}
     */

    private int code;
    private String msg;
    private DataBeanX data;

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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX implements Serializable {
        /**
         * token : eyJ1c2VyTmFtZSI6IjEyMyIsImFsZyI6IkhTMjU2In0.eyJqdGkiOiJmY29pbmp3dCIsImlhdCI6MTU2Mjc2NTc4NSwic3ViIjoie1widXNlcklkXCI6ODIsXCJ1c2VyQ29kZVwiOlwiNjY2NjU1NlwiLFwidXNlclR5cGVcIjowLFwiY2VsbFBob25lXCI6XCIxODU1MTcxODQ4OVwiLFwiZW1haWxcIjpcIlwiLFwiaWRUeXBlXCI6MSxcInZlcmlmaWVkU3RhdHVzXCI6MCxcInZlcmlmaWVkTGV2ZWxcIjowLFwicGljXCI6XCJodHRwOi8vNDcuMjQ0LjQ1LjE2Mzo4MW51bGxcIixcImdhRW5hYmxlZFwiOjAsXCJlbWFpbEVuYWJsZWRcIjowLFwicGhvbmVFbmFibGVkXCI6MSxcInBob25lQXJlYVwiOlwiKzg2XCIsXCJpbVRva2VuXCI6XCI0YmNmNGNiOTRlZmRiODk2MTBiZDY1ZTYxNmE5YjE3MFwifSIsImV4cCI6MTU2NTM1Nzc4NX0.8mMAYdsiuRGDwZIApoGpMRk43kWf6_SmATQOaUEL4Ps
         * expiry : 2019-08-09 21:36:25
         * data : {"userId":82,"userCode":"6666556","userName":null,"userType":0,"cellPhone":"18551718489","email":"","idType":1,"idNumber":null,"verifiedStatus":0,"verifiedLevel":0,"pic":"http://47.244.45.163:81null","country":null,"fishingCode":null,"gaEnabled":0,"emailEnabled":0,"phoneEnabled":1,"fullName":null,"phoneArea":"+86","imToken":"4bcf4cb94efdb89610bd65e616a9b170"}
         */

        private String token;
        private String expiry;
        private DataBean data;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean implements Serializable {
            /**
             * userId : 82
             * userCode : 6666556
             * userName : null
             * userType : 0
             * cellPhone : 18551718489
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
             * imToken : 4bcf4cb94efdb89610bd65e616a9b170
             */

            private int userId;
            private String userCode;
            private Object userName;
            private int userType;
            private String cellPhone;
            private String email;
            private int idType;
            private Object idNumber;
            private int verifiedStatus;
            private int verifiedLevel;
            private String pic;
            private Object country;
            private Object fishingCode;
            private int gaEnabled;
            private int emailEnabled;
            private int phoneEnabled;
            private Object fullName;
            private String phoneArea;
            private String imToken;
            private int loginType;

            public int  getLoginType() {
                return loginType;
            }

            public void setLoginType(int loginType) {
                this.loginType = loginType;
            }

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

            public Object getUserName() {
                return userName;
            }

            public void setUserName(Object userName) {
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

            public Object getIdNumber() {
                return idNumber;
            }

            public void setIdNumber(Object idNumber) {
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

            public Object getCountry() {
                return country;
            }

            public void setCountry(Object country) {
                this.country = country;
            }

            public Object getFishingCode() {
                return fishingCode;
            }

            public void setFishingCode(Object fishingCode) {
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

            public Object getFullName() {
                return fullName;
            }

            public void setFullName(Object fullName) {
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
    }
}
