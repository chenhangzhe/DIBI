package cn.suozhi.DiBi.login;

import android.util.Log;
import android.webkit.JavascriptInterface;

import cn.suozhi.DiBi.common.eventbus.EventBusHelper;
import cn.suozhi.DiBi.common.eventbus.Message;

public class testInterface {

    @JavascriptInterface
    public void getSlideData(String callData) {
        //Log.d("verifyweww", "滑动验证   "  +callData );
        EventBusHelper.post(Message.SLIDE_SCROLL_SUCCESS,callData);
    }
}
