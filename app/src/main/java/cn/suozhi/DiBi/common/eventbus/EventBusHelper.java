package cn.suozhi.DiBi.common.eventbus;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBus 工具类
 */
public class EventBusHelper {

    public static void post(String msg) {
        post(msg, null);
    }

    public static void post(String msg, Object obj) {
        EventBus.getDefault().post(new MessageEvent(msg, obj));
    }

    public static void register(@NonNull Object subscribe) {
        EventBus.getDefault().register(subscribe);
    }

    public static void unregister(@NonNull Object subscribe) {
        EventBus.getDefault().unregister(subscribe);
    }
}
