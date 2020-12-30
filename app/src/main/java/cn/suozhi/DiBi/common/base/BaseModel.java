package cn.suozhi.DiBi.common.base;

/**
 * Json解析基础类
 */
public class BaseModel {

    protected Long code;
    protected String msg;
    protected Object data;//用来解析复杂对象

    public BaseModel(Long code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Long getCode() {
        return code;
    }

    public BaseModel setCode(Long code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public BaseModel setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
