package cn.suozhi.DiBi.c2c.model;

import java.util.List;

public class C2cCoinSelectedEnity {
    /**
     * code : 0
     * msg : 成功
     * data : ["USDT","HKDT"]
     */

    private int code;
    private String msg;
    private List<String> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
