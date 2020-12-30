package cn.suozhi.DiBi.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by LZY on 2017/9/1.
 */

public class GsonUtil {
    private static Gson mGson = new Gson();

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }
        return mGson.fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return mGson.toJson(src);
    }

    public static Gson getGson() {
        return mGson;
    }
}
