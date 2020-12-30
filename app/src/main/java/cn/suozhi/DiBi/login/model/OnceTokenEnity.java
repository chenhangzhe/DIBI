package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

/**
 * Created by issuser on 2019/7/10.
 */

public class OnceTokenEnity {
    /**
     * code : 0
     * msg : 成功
     * data : {"uid":"57e2b28b-5325-4ab2-95b4-289553b4fd9b","resultToken":"eyJ1c2VyTmFtZSI6IjEyMyIsImFsZyI6IkhTMjU2In0.eyJqdGkiOiJmY29pbmp3dCIsImlhdCI6MTU2Mjc0OTgwOSwic3ViIjoie1wicmVxdWVzdElkXCI6XCJlMzliMWVhMC1lM2ZhLTQxNTMtYWQ2ZC03OTcwMjg3NmVlMTlcIixcIm1vZHVsZUlkXCI6XCIxXCIsXCJ1c2VySWRcIjpcIjU3ZTJiMjhiLTUzMjUtNGFiMi05NWI0LTI4OTU1M2I0ZmQ5YlwifSIsImV4cCI6MTU2Mjc1MDEwOX0.WpTyegUFNO0DkFDRPUu-Dv15DXywLqXRxijh8huNwvI"}
     */

    private long code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * uid : 57e2b28b-5325-4ab2-95b4-289553b4fd9b
         * resultToken : eyJ1c2VyTmFtZSI6IjEyMyIsImFsZyI6IkhTMjU2In0.eyJqdGkiOiJmY29pbmp3dCIsImlhdCI6MTU2Mjc0OTgwOSwic3ViIjoie1wicmVxdWVzdElkXCI6XCJlMzliMWVhMC1lM2ZhLTQxNTMtYWQ2ZC03OTcwMjg3NmVlMTlcIixcIm1vZHVsZUlkXCI6XCIxXCIsXCJ1c2VySWRcIjpcIjU3ZTJiMjhiLTUzMjUtNGFiMi05NWI0LTI4OTU1M2I0ZmQ5YlwifSIsImV4cCI6MTU2Mjc1MDEwOX0.WpTyegUFNO0DkFDRPUu-Dv15DXywLqXRxijh8huNwvI
         */

        private String uid;
        private String resultToken;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getResultToken() {
            return resultToken;
        }

        public void setResultToken(String resultToken) {
            this.resultToken = resultToken;
        }
    }
}
