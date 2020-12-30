package com.netease.nim.uikit.business.session.viewholder.extension;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.business.session.viewholder.extension.CustomAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.CustomAttachmentType;

public class MyOrderAttachment extends CustomAttachment {

    public String title;
    public String content;

    public MyOrderAttachment() {
        super(CustomAttachmentType.MyOrderCustomMsg);
    }

    @Override
    protected void parseData(JSONObject data) {

        Log.d("hahhahah","MyOrderAttachment==="+data.toString());
        title=data.getString("msg");
        content=data.getString("orderNo");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();

        data.put("msg",title);
        data.put("orderNo",content);

        return data;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
