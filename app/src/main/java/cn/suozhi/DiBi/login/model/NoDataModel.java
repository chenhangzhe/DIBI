package cn.suozhi.DiBi.login.model;

import java.io.Serializable;

/**
 * Created by issuser on 2019/7/10.
 */

public class NoDataModel {

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
    }
}
