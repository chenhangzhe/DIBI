package cn.suozhi.DiBi.common.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import cn.suozhi.DiBi.common.base.BaseModel;

public class JsonUtil {

    //对象转json时不过滤null
    private static Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * 默认解析 -- 适用于data为基本类型
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, classOfT);
    }

    /**
     * 避免data为基本类型时被解析为复杂对象
     * 【仅在需要时调用】
     */
    public static <T> T fromJsonO(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        try {
            JSONObject jo = new JSONObject(json);
            long code = jo.getLong("code");
            String msg = jo.getString("msg");
            if (jo.has("data")) {
                Object o = jo.get("data");
                if (o instanceof Integer || o instanceof Long || o instanceof Double ||
                        o instanceof Number || o instanceof Boolean || o instanceof String) {
                    return dataNull(code, msg, classOfT);
                }
                return gson.fromJson(json, classOfT);
            }
            return dataNull(code, msg, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> T dataNull(long code, String msg, Class<T> classOfT) {
        String json = gson.toJson(new BaseModel(code, msg, null));
        return gson.fromJson(json, classOfT);
    }
}
